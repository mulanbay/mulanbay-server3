package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static cn.mulanbay.pms.common.PmsCode.SYSTEM_LOCK;

/**
 * 系统状态
 *
 * @author fenghong
 * @date 2024/2/1
 */
@Component
public class SystemStatusHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(SystemStatusHandler.class);

    @Value("${mulanbay.security.lock.unlockRandoms:6}")
    private int randoms;

    /**
     * 解锁的代码=加锁码
     * 避免重复加锁
     */
    private int unlockStatus;

    /**
     * 系统当前状态
     */
    private int status = ErrorCode.SUCCESS;

    /**
     * 消息
     */
    private String message;

    /**
     * 解锁码
     */
    private String unlockCode;

    public SystemStatusHandler() {
        super("系统状态");
    }

    /**
     * 设置状态
     *
     * @param afterStatus
     * @param msg
     * @param expireTime
     * @param ulc  解锁码
     * @return
     */
    public boolean lock(int afterStatus, String msg, Date expireTime, String ulc) {
        if (afterStatus > status) {
            if (unlockStatus > afterStatus) {
                //无法上锁，上次的解锁码比此次大
                return false;
            }
            this.lockSystem(afterStatus, msg, expireTime, ulc);
            return true;
        } else {
            return false;
        }
    }

    private synchronized void lockSystem(int afterStatus, String msg, Date expireTime, String ulc) {
        status = afterStatus;
        message = (msg == null ? "系统锁定" : msg);
        this.handleUnlockCode(ulc);
        //增加定定时恢复
        if (expireTime != null) {
            message += ",重新开启时间:" + DateUtil.getFormatDate(expireTime, DateUtil.Format24Datetime);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    BeanFactoryUtil.getBean(SystemStatusHandler.class).revert(afterStatus);
                }
            }, expireTime);
        }
        logger.warn("设置系统状态,code={}", afterStatus);
    }

    /**
     * 处理解锁码
     *
     * @param ulc 解锁码，可以用户直接输入，默认由系统产生
     */
    private void handleUnlockCode(String ulc) {
        if (StringUtil.isEmpty(ulc)) {
            ulc = NumberUtil.getRandNum(randoms);
        }
        this.unlockCode = ulc;
        logger.warn("系统解锁码:" + unlockCode);
        NotifyHandler notifyHandler = BeanFactoryUtil.getBean(NotifyHandler.class);
        notifyHandler.addMessageToNotifier(SYSTEM_LOCK, "系统解锁码", "系统解锁码为" + unlockCode, new Date());
    }

    /**
     * 获取状态
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 系统解锁
     *
     * @param uc
     * @return
     */
    public synchronized boolean unlock(String uc, int beforeStatus) {
        if (uc.equals(unlockCode)) {
            this.revert();
            //需要设置解锁状态码，否则可能其他模块会再次上锁
            this.unlockStatus = beforeStatus;
            logger.warn("系统解锁成功");
            return true;
        } else {
            logger.warn("系统解锁失败");
            return false;
        }

    }

    /**
     * 恢复
     *
     * @param beforeStatus 原来的设置代码，等级高的可以对低的恢复
     * @return
     */
    public boolean revert(int beforeStatus) {
        if (beforeStatus >= status) {
            this.revert();
            logger.warn("恢复系统状态,beforeCode={}", beforeStatus);
            return true;
        } else {
            logger.warn("恢复系统状态失败,级别不够,beforeCode={},status={}", beforeStatus, status);
            return false;
        }
    }

    private synchronized void revert() {
        status = ErrorCode.SUCCESS;
        unlockStatus = ErrorCode.SUCCESS;
        message = null;
        unlockCode = null;
    }
}
