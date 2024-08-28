package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.handler.bean.data.UserOpBean;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.FunctionType;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import cn.mulanbay.pms.util.ClazzUtils;
import cn.mulanbay.schedule.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

import static cn.mulanbay.pms.common.Constant.ADMIN_USER_ID;
import static cn.mulanbay.pms.common.Constant.DAY_MILLS_SECONDS;

/**
 * 日志处理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class LogHandler extends BaseHandler implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(LogHandler.class);

    @Value("${mulanbay.log.operLog}")
    boolean enableOperLog;

    @Value("${mulanbay.log.sysLog}")
    boolean enableSysLog;

    /**
     * 修改类型是否同步
     */
    @Value("${mulanbay.cache.bean.syncEdit:false}")
    boolean syncEdit;

    /**
     * 删除类型是否同步
     */
    @Value("${mulanbay.cache.bean.syncDelete:false}")
    boolean syncDelete;

    /**
     * todo 加倍奖励的天数
     * 规则：
     * 1天：功能配置的原始值，例如5分
     * 2天：5+1=6分
     * 3天：5+2=7分
     * 4天：5+3=8分
     */
    @Value("${mulanbay.reward.ctsOP.maxDays:7}")
    int rewardMaxDays;

    @Autowired
    ThreadPoolHandler threadPoolHandler;

    @Autowired
    SystemConfigHandler systemConfigHandler;

    @Autowired
    RewardHandler rewardHandler;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    CommonCacheHandler commonCacheHandler;

    @Autowired
    SysCodeHandler sysCodeHandler;

    /**
     * 解决依赖循环
     */
    @Lazy
    @Autowired
    NotifyHandler notifyHandler;

    @Autowired
    UserHandler userHandler;

    @Autowired
    BaseService baseService;

    public LogHandler() {
        super("日志处理");
    }

    @Override
    public void init() {

    }

    /**
     * 操作日志
     *
     * @param log
     */
    public void addOperLog(OperLog log) {
        if(!enableOperLog){
            return;
        }
        this.handleOperLog(log);
    }

    /**
     * 操作日志
     * @param log
     */
    private void handleOperLog(OperLog log){
        threadPoolHandler.pushThread(() -> {
            try {
                SysFunc sf = log.getSysFunc();
                if (StringUtil.isNotEmpty(sf.getIdField())&&StringUtil.isEmpty(log.getIdValue())) {
                    Map<String, String> paraMap = (Map<String, String>) JsonUtil.jsonToBean(log.getParas(), Map.class);
                    log.setIdValue(this.getParaIdValue(sf, paraMap));
                }
                log.setStoreTime(new Date());
                log.setHostIpAddress(systemConfigHandler.getHostIpAddress());
                //序列化比较耗时间
                //log.setParas(JsonUtil.beanToJson(changeToNormalMap(log.getParaMap())));
                log.setHandleDuration(log.getOccurEndTime().getTime() - log.getOccurStartTime().getTime());
                log.setStoreDuration(log.getStoreTime().getTime() - log.getOccurEndTime().getTime());
                if (log.getUserId() == null) {
                    log.setUserId(ADMIN_USER_ID);
                    log.setUsername("未知");
                }
                baseService.saveObject(log);
                this.handleReward(sf, log);
                this.notifyError(log.getUserId(), sf.getCode(), sf.getFuncName());
                //同步缓存（如果需要同步bean的缓存，则日志操作无法进行异步存储）
                this.syncCache(sf,log.getIdValue());
            } catch (Exception e) {
                logger.error("增加操作日志异常", e);
            }
        });
    }

    /**
     * 积分奖励
     *
     * @param sf
     * @param log
     */
    private void handleReward(SysFunc sf, OperLog log) {
        try {
            if (sf.getRewardPoint() != 0 && log.getUserId() > ADMIN_USER_ID) {
                //积分奖励(操作类的积分记录管理的messageId为操作记录的编号)
                rewardHandler.reward(log.getUserId(), sf.getRewardPoint(), log.getId(),
                        BussSource.OPERATION, "功能操作奖励", log.getId());
                //连续操作奖励
                String key = CacheKey.getKey(CacheKey.USER_CONTINUE_OP, log.getUserId().toString(), sf.getFuncId().toString());
                Date ost = log.getOccurStartTime();
                UserOpBean uco = cacheHandler.get(key, UserOpBean.class);
                //缓存失效时间两天，保证至少连续两天
                long leftExpired = 2*DAY_MILLS_SECONDS;
                if (uco == null) {
                    uco = new UserOpBean();
                    uco.setFistDay(ost);
                    uco.setLastDay(ost);
                    uco.setDays(1);
                    cacheHandler.setMS(key, uco, leftExpired);
                } else {
                    if (!isSameDay(uco.getLastDay(),ost)) {
                        uco.setLastDay(ost);
                        //添加的不是间隔天数，而是连续的次数
                        uco.addDay();
                        if (uco.getDays() <= rewardMaxDays) {
                            //奖励连续操作
                            int rewards = sf.getRewardPoint();
                            if(sf.getRewardPoint()>0){
                                rewards+= uco.getDays()-1;
                            }else{
                                //如果是负数，则负值更大
                                rewards-= uco.getDays()+1;
                            }
                            rewardHandler.reward(log.getUserId(), rewards, log.getId(),
                                    BussSource.OPERATION, "功能操作连续" + uco.getDays() + "天奖励", log.getId());
                            cacheHandler.setMS(key, uco, leftExpired);
                        }else{
                            //清除,重新开始计算
                            cacheHandler.delete(key);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("操作日志积分奖励处理异常", e);
        }
    }

    /**
     * 是否同一天
     * @param d1
     * @param d2
     * @return
     */
    private boolean isSameDay(Date d1,Date d2){
        String s1 = DateUtil.getFormatDate(d1,DateUtil.FormatDay1);
        String s2 = DateUtil.getFormatDate(d2,DateUtil.FormatDay1);
        return s1.equals(s2);
    }

    /**
     * 同步缓存
     *
     * @param sf
     * @param id
     */
    private void syncCache(SysFunc sf,String id){
        try {
            if(StringUtil.isEmpty(id)){
                return;
            }
            if(syncEdit&&sf.getFuncType() == FunctionType.EDIT){
                Class clz = ClazzUtils.getClass(sf.getBeanName());
                commonCacheHandler.removeBean(clz,id);
            }else if(syncDelete&&sf.getFuncType() == FunctionType.DELETE){
                String[] ids = id.split(",");
                for(String s:ids){
                    Class clz = ClazzUtils.getClass(sf.getBeanName());
                    commonCacheHandler.removeBean(clz,s);
                }
            }
        } catch (Exception e) {
            logger.error("同步缓存异常",e);
        }
    }

    /**
     * 系统日志
     *
     * @param log
     */
    public void addSysLog(SysLog log) {
        if(!enableSysLog){
            return;
        }
        if (log.getOccurTime() == null) {
            log.setOccurTime(new Date());
        }
        this.handleSysLog(log);
    }

    /**
     * 系统日志
     *
     * @param title
     * @param content
     * @param errorCode
     */
    public void addSysLog(String title, String content, int errorCode) {
        if(!enableSysLog){
            return;
        }
        SysLog log = new SysLog();
        log.setUserId(0L);
        log.setUsername("系统操作");
        log.setTitle(title);
        log.setContent(content);
        log.setErrorCode(errorCode);
        if (log.getOccurTime() == null) {
            log.setOccurTime(new Date());
        }
        this.handleSysLog(log);
    }

    /**
     * 系统日志
     * @param log
     */
    private void handleSysLog(SysLog log){
        threadPoolHandler.pushThread(() -> {
            try {
                SysCode ec = sysCodeHandler.getSysCode(log.getErrorCode());
                if(ec!=null&&!ec.getLoggable()){
                    //默认需要记录日志
                    return;
                }
                SysFunc sf = log.getSysFunc();
                if (sf != null) {
                    log.setIdValue(this.getParaIdValue(sf, log.getParaMap()));
                }
                LogLevel logLevel = ec==null? LogLevel.NORMAL:ec.getLevel();
                log.setLogLevel(logLevel);
                Date now = new Date();
                log.setStoreTime(now);
                //会比较慢
                log.setHostIpAddress(systemConfigHandler.getHostIpAddress());
                log.setCreatedTime(now);
                Map map = log.getParaMap();
                if (map != null && !map.isEmpty()) {
                    //序列化比较耗时间
                    log.setParas(JsonUtil.beanToJson(map));
                }
                log.setStoreDuration(log.getStoreTime().getTime() - log.getOccurTime().getTime());
                baseService.saveObject(log);
                this.notifyError(log.getUserId(), ec, log.getContent());
            } catch (Exception e) {
                String msg = "增加系统日志异常，log=" + log.getContent();
                logger.error(msg, e);
            }
        });
    }

    /**
     * 获取参数ID值
     *
     * @param sf
     * @param paraMap
     * @return
     */
    protected String getParaIdValue(SysFunc sf, Map paraMap) {
        if (!StringUtil.isEmpty(sf.getIdField())) {
            if (paraMap != null && !paraMap.isEmpty()) {
                //设置key的值，方便后期查找比对使用,目前只对修改类有效
                Object oo = paraMap.get(sf.getIdField());
                if (oo != null) {
                    return oo.toString();
                }
            }
        }
        return null;
    }

    /**
     * 消息提醒
     * @param userId
     * @param code
     * @param message
     */
    protected void notifyError(Long userId,  int code, String message) {
        if (code == ErrorCode.SUCCESS) {
            //正常的代码不做处理
            return;
        }
        SysCode ec = sysCodeHandler.getSysCode(code);
        this.notifyError(userId,ec,message);
    }

    /**
     * 消息提醒
     * @param userId
     * @param ec
     * @param message
     */
    protected void notifyError(Long userId, SysCode ec, String message) {
        try {
            if (ec==null||ec.getCode() == ErrorCode.SUCCESS) {
                return;
            }
            //通知
            notifyHandler.addMessageToNotifier(ec.getCode(), "系统代码["+ec.getName()+"]通知", message + "," + getUserInfo(userId),
                    null, null);
        } catch (Exception e) {
            logger.error("处理系统代码通知异常", e);
        }
    }

    private String getUserInfo(Long userId) {
        if (userId == null) {
            return "";
        } else {
            String s = "操作人UserId:" + userId;
            User user = userHandler.getUser(userId);
            if (user != null) {
                s += ",手机号:" + user.getPhone();
            }
            return s;
        }
    }

    /**
     * 调度的消息通知
     *
     * @param triggerId
     * @param code          系统代码
     * @param title
     * @param message
     */
    @Override
    public void handleScheduleMessage(Long triggerId, int code, String title, String message) {
        //todo 后期可以通过taskTriggerId来选择通知给谁
        //记录系统日志
        this.addSysLog(title,message,code);
    }

}
