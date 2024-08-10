package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.lock.DistributedLock;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.domain.Message;
import cn.mulanbay.pms.persistent.domain.User;
import cn.mulanbay.pms.persistent.domain.UserSet;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import cn.mulanbay.pms.persistent.enums.MessageSendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 消息发送处理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class MessageSendHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageSendHandler.class);

    /**
     * 最大发送失败次数
     */
    @Value("${mulanbay.notify.message.send.maxFail:3}")
    int sendMaxFail;

    /**
     * 每次发送失败重试次数
     */
    @Value("${mulanbay.notify.message.send.retryTimes:3}")
    int retryTimes;

    /**
     * 消息发送是否需要锁定
     */
    @Value("${mulanbay.notify.message.send.lock:true}")
    boolean sendLock;

    /**
     * 上锁重试次数
     */
    @Value("${mulanbay.notify.message.send.retryTimes:3}")
    int lockRetryTimes;

    /**
     * 发送是否同步(即一个一个顺序发送)
     */
    @Value("${mulanbay.notify.message.send.sync:true}")
    boolean sync;

    @Value("${mulanbay.nodeId}")
    String nodeId;

    @Value("${mulanbay.mobile.baseUrl}")
    private String mobileBaseUrl;

    @Autowired
    DistributedLock distributedLock;

    @Autowired
    LogHandler logHandler;

    @Autowired
    UserHandler userHandler;

    @Autowired
    WXHandler wxHandler;

    @Autowired
    MailHandler mailHandler;

    @Autowired
    BaseService baseService;

    public MessageSendHandler() {
        super("消息发送");
    }

    /**
     * 发送消息
     *
     * @param message
     * @return
     */
    public boolean sendMessage(Message message) {
        //这个message有可能在其他地方被设置了id
        String key = "messageSendLock:" + message.getMsgId();
        try {
            if (sendLock) {
                boolean b = distributedLock.lock(key, lockRetryTimes);
                if (!b) {
                    logger.warn("消息ID=" + message.getMsgId() + "正在被发送，无法重复发送");
                    logHandler.addSysLog("消息重复发送", "消息ID=" + message.getMsgId() + "正在被发送，无法重复发送",
                            PmsCode.MESSAGE_DUPLICATE_SEND);
                    return true;
                }
                //取得锁后应该再查一次是否发送或者发送失败次数超过
            }
            UserSet us = userHandler.getUserSet(message.getUserId());
            if (us == null) {
                logger.warn("无法获取到userId=" + message.getUserId() + "用户信息,无法发送消息");
                message.setSendStatus(MessageSendStatus.SKIP);
                message.setFailCount(message.getFailCount() + 1);
                message.setLastSendTime(new Date());
                message.setNodeId(nodeId);
                message.setRemark("没有用户相关设置信息,无法发送消息");
                baseService.saveOrUpdateObject(message);
                return true;
            }
            boolean res;
            if (message.getFailCount() < sendMaxFail) {
                res = this.sendUserMessage(us, message);
                if (res) {
                    message.setSendStatus(MessageSendStatus.SUCCESS);
                } else {
                    message.setSendStatus(MessageSendStatus.FAIL);
                    message.setFailCount(message.getFailCount() + 1);
                }
            } else {
                message.setSendStatus(MessageSendStatus.FAIL);
                message.setFailCount(message.getFailCount() + 1);
                logger.info("消息ID=" + message.getMsgId() + "达到最大发送失败次数:" + sendMaxFail + ",本消息已经发送失败次数:" + message.getFailCount());
                res = true;
            }
            message.setLastSendTime(new Date());
            message.setNodeId(nodeId);
            this.saveMessage(message);
            return res;
        } catch (Exception e) {
            logger.error("发送消息失败，id=" + message.getMsgId(), e);
            return false;
        } finally {
            if (sendLock) {
                boolean b = distributedLock.releaseLock(key);
                if (!b) {
                    logger.warn("释放消息发送锁key=" + key + "失败");
                }
            }
        }
    }

    /**
     * 直接扔掉
     *
     * @param message
     */
    private void saveMessage(Message message) {
        try {
            baseService.saveOrUpdateObject(message);
        } catch (Exception e) {
            logger.error("保持用户消息异常", e);
        }
    }

    /**
     * 发送消息(后期比如可以增加短信的发送)
     *
     * @param us
     * @param message
     * @return
     */
    private boolean sendUserMessage(UserSet us, Message message) {
        User user = userHandler.getUser(message.getUserId());
        boolean b1 = true;
        if (us.getSendEmail() && StringUtil.isNotEmpty(user.getEmail())) {
            // 发送邮件
            b1 = this.sendMail(user.getEmail(),message.getTitle(), message.getContent());
        }
        boolean b2 = true;
        if (us.getSendWx()) {
            b2 = this.sendWxMessage(message.getMsgId(),message.getUserId(), message.getTitle(), message.getContent(), message.getCreatedTime(), message.getLogLevel(), message.getUrl());
        }
        //只要有一个发送成功算成功
        return b1 || b2;
    }



    /**
     * 发送微信消息
     *
     * @param userId
     * @param title
     * @param content
     * @param time
     * @param level
     * @return
     */
    public boolean sendWxMessage(Long id,Long userId, String title, String content, Date time, LogLevel level, String url) {
        if(StringUtil.isNotEmpty(url)&& !url.startsWith("http")){
            url = mobileBaseUrl+url;
        }
        int i =1;
        boolean res = false;
        while(i<=retryTimes&&!res){
            res = wxHandler.sendTemplateMessage(id,userId, title, content, time, level, url);
            if(!res){
                try {
                    i++;
                    Thread.sleep(i*1000L);
                    logger.info("微信消息{}发送失败，进行第{}次尝试",title,i);
                } catch (Exception e) {
                    logger.error("sendWxMessage sleep error",e);
                }
            }
        }
        return res;
    }

    /**
     * 发送邮件
     * @param to
     * @param subject
     * @param content
     * @return
     */
    public boolean sendMail(String to, String subject, String content){
        int i =1;
        boolean res = false;
        while(i<=retryTimes&&!res){
            res = mailHandler.sendMail(to,subject, content);
            if(!res){
                try {
                    i++;
                    Thread.sleep(i*1000L);
                    logger.info("邮件{}发送失败，进行第{}次尝试",subject,i);
                } catch (Exception e) {
                    logger.error("sendWxMessage sleep error",e);
                }
            }
        }
        return res;
    }

}
