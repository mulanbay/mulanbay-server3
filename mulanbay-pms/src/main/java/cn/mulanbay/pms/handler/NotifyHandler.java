package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.*;
import cn.mulanbay.common.exception.MessageNotify;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.domain.Message;
import cn.mulanbay.pms.persistent.domain.MonitorUser;
import cn.mulanbay.pms.persistent.domain.SysCode;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import cn.mulanbay.pms.persistent.enums.MessageSendStatus;
import cn.mulanbay.pms.persistent.enums.MessageType;
import cn.mulanbay.pms.persistent.service.LogService;
import cn.mulanbay.schedule.NotifiableProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 提醒处理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component(value="notifiableProcessor")
public class NotifyHandler extends BaseHandler implements NotifiableProcessor, MessageNotify {

    private static final Logger logger = LoggerFactory.getLogger(NotifyHandler.class);

    @Value("${mulanbay.nodeId}")
    String nodeId;

    @Value("${mulanbay.notify.message.expectSendTime}")
    String defaultExpectSendTime;

    /**
     * 系统代码次数批量更新
     */
    @Value("${mulanbay.notify.code.batchUpdates}")
    long codeBatchUpdates;

    /**
     * 是否需要提醒表单验证类的系统代码
     */
    @Value("${mulanbay.notify.message.validateError}")
    boolean notifyValidateError;

    /**
     * 表单验证类的系统代码最小值，一般是8位数开始
     */
    @Value("${mulanbay.notify.message.minCode}")
    int minValidateCode;

    @Autowired
    BaseService baseService;

    @Autowired
    LogService logService;

    @Autowired
    SystemConfigHandler systemConfigHandler;

    @Autowired
    RedisDelayQueueHandler redisDelayQueueHandler;

    @Autowired
    LogHandler logHandler;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    UserHandler userHandler;

    public NotifyHandler() {
        super("提醒处理");
    }

    /**
     * 添加通知消息
     *
     * @param message
     */
    private void addNotifyMessage(Message message) {
        //加入到最新的一条消息(两小时有效)
        String key = CacheKey.getKey(CacheKey.USER_LATEST_MESSAGE, message.getUserId().toString());
        cacheHandler.set(key, message, 7200);
        redisDelayQueueHandler.addMessage(message);
    }

    /**
     * 向某个特定的人添加消息
     * 消息可能针对普通用户，或者是系统管理员
     *
     * @param title
     * @param content
     * @param userId
     * @param notifyTime
     */
    public Long addNotifyMessage(int code, String title, String content, Long userId, Date notifyTime) {
        SysCode ec = systemConfigHandler.getSysCode(code);
        if (ec == null) {
            logHandler.addSysLog(LogLevel.WARNING, "系统代码未配置", "代码[" + code + "]没有配置",
                    PmsCode.ERROR_CODE_NOT_DEFINED);
            return null;
        }
        this.updateSysCodeCount(code, 1L);
        //获取发送时间
        Date expectSendTime = this.getExpectSendTime(ec, notifyTime);
        if (expectSendTime == null) {
            return null;
        }
        Message message = this.createUserMessage(ec, userId, expectSendTime, title, content, ec.getMobileUrl(), null);
        //因为用户日历和用户积分奖励都需要这个messageId，所以只能先保存。另外一种方法可以在UserMessage表中新增一个uuid字段来解决
        baseService.saveObject(message);
        this.addNotifyMessage(message);
        return message.getMsgId();
    }

    /**
     * 更新次数
     * @param code
     * @param addCount
     */
    private void updateSysCodeCount(Integer code, long addCount) {
        try {
            String key = CacheKey.getKey(CacheKey.SYS_CODE_COUNTS,code.toString());
            long cv = cacheHandler.incre(key,addCount);
            if(cv>=codeBatchUpdates){
                cacheHandler.incre(key,-cv);
                logService.updateSysCodeCount(code,cv);
            }
        } catch (Exception e) {
            logger.error("更新系统代码次数异常", e);
        }
    }

    public void addMessageToNotifier(int code, String title, String content, Date notifyTime) {
        this.addMessageToNotifier(code, title, content, notifyTime, null, null);
    }

    public void addMessageToNotifier(int code, String title, String content, Date notifyTime, String remark) {
        this.addMessageToNotifier(code, title, content, notifyTime, null, remark);
    }

    /**
     * 向系统中需要通知的人发送系统消息
     * 消息只针对管理员，所以这里发送的都是系统消息
     *
     * @param title
     * @param content
     * @param notifyTime
     */
    public void addMessageToNotifier(int code, String title, String content, Date notifyTime, String url, String remark) {
        try {
            if (!notifyValidateError && code >= minValidateCode) {
                return;
            }
            SysCode ec = systemConfigHandler.getSysCode(code);
            if (ec == null) {
                logHandler.addSysLog(LogLevel.WARNING, "系统代码未配置", "代码[" + code + "]没有配置,系统采用通用提醒代码配置",
                        PmsCode.ERROR_CODE_NOT_DEFINED);
                ec = systemConfigHandler.getSysCode(PmsCode.MESSAGE_NOTIFY_COMMON_CODE);
            }
            this.updateSysCodeCount(code, 1L);
            //获取发送时间
            Date expectSendTime = this.getExpectSendTime(ec, notifyTime);
            if (expectSendTime == null) {
                return;
            }
            List<MonitorUser> userList = userHandler.getMonitorUserList(ec.getBussType());
            if (StringUtil.isEmpty(userList)) {
                logger.warn("业务类型[" + ec.getBussType().getName() + "]没有配置系统监控人员");
                return;
            }
            title += "(code=" + code + ")";
            for (MonitorUser smu : userList) {
                //限流判断
                boolean check = this.checkErrorCodeLimit(ec, smu.getUserId());
                if (check) {
                    Message ssm = this.createUserMessage(ec, smu.getUserId(), expectSendTime, title, content, url, remark);
                    this.addNotifyMessage(ssm);
                } else {
                    logger.debug("code[" + ec.getCode() + "],userId[" + smu.getUserId() + "]触发限流，不发送");
                }
            }
        } catch (Exception e) {
            logger.error("发送系统消息异常", e);
        }
    }

    /**
     * 检查系统代码限流发送
     *
     * @param ec
     * @param userId
     * @return
     */
    private boolean checkErrorCodeLimit(SysCode ec, Long userId) {
        if (ec.getLimitPeriod() <= 0) {
            return true;
        } else {
            String key = CacheKey.getKey(CacheKey.USER_ERROR_CODE_LIMIT, userId.toString(), ec.getCode().toString());
            Integer n = cacheHandler.get(key, Integer.class);
            if (n == null) {
                cacheHandler.set(key, 0, ec.getLimitPeriod());
                return true;
            } else {
                //不通过，不用再发
                return false;
            }
        }
    }

    private Message createUserMessage(SysCode ec, Long userId, Date expectSendTime, String title, String content, String url, String remark) {
        Message message = new Message();
        message.setExpectSendTime(expectSendTime);
        message.setUserId(userId);
        message.setContent(content);
        message.setCreatedTime(new Date());
        message.setFailCount(0);
        message.setCode(ec.getCode());
        //没有作用
        message.setMsgType(MessageType.WX);
        message.setBussType(ec.getBussType());
        message.setLogLevel(ec.getLevel());
        message.setTitle(title);
        message.setUrl(url);
        message.setRemark(remark);
        message.setSendStatus(MessageSendStatus.UN_SEND);
        if (message.getNodeId() == null) {
            message.setNodeId(nodeId);
        }
        return message;
    }

    /**
     * 获取提醒时间
     *
     * @param ec
     * @param notifyTime
     * @return
     */
    private Date getExpectSendTime(SysCode ec, Date notifyTime) {
        if (!ec.getNotifiable()) {
            logger.warn("代码[" + ec.getCode() + "]配置为不发送消息");
            return null;
        }
        if (notifyTime == null) {
            //由代码配置决定
            if (ec.getRealtime()) {
                notifyTime = new Date();
            } else {
                //统一一个时间发送
                notifyTime = DateUtil.getDate(DateUtil.getToday() + " " + defaultExpectSendTime + ":00", DateUtil.Format24Datetime);
            }
        }
        return notifyTime;
    }

    /**
     * 调度的消息通知
     *
     * @param taskTriggerId
     * @param code          系统代码
     * @param title
     * @param message
     */
    @Override
    public void notifyMessage(Long taskTriggerId, int code, String title, String message) {
        //todo 后期可以通过taskTriggerId来选择通知给谁
        //发消息
        this.addMessageToNotifier(code, title, message, null, null, null);
        //记录系统日志
        logHandler.addSysLog(LogLevel.WARNING,title,message,code);
    }

    @HandlerMethod(desc = "清除所有未发送消息")
    public void clearMessage(){
        this.redisDelayQueueHandler.clearMessage();
    }

    @Override
    public void notifyMsg(int code, String title, String content) {
        this.addMessageToNotifier(code, title, content, null);
    }
}
