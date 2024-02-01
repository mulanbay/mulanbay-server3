package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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

    private static int code = ErrorCode.SUCCESS;

    public static String message;

    @Autowired
    NotifyHandler notifyHandler;

    /**
     * 解锁码
     */
    private static String unlockCode ;

    public SystemStatusHandler() {
        super("系统状态");
    }

    /**
     * 设置状态
     *
     * @param afterCode
     * @param msg
     * @return
     */
    public synchronized boolean setStatus(int afterCode, String msg, Date expireTime) {
        if (afterCode > code) {
            code = afterCode;
            message = msg;
            this.createUnlockCode();
            //增加定定时恢复
            if (expireTime != null) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        BeanFactoryUtil.getBean(SystemStatusHandler.class).revert(afterCode);
                    }
                }, expireTime);
            }
            logger.warn("设置系统状态,code={}", afterCode);
            return true;
        } else {
            return false;
        }
    }

    private void createUnlockCode(){
       String uc = NumberUtil.getRandNum(6);
       unlockCode = uc;
       notifyHandler.addMessageToNotifier(SYSTEM_LOCK,"系统解锁码","系统解锁码为"+uc,new Date());
    }

    /**
     * 获取状态
     *
     * @return
     */
    public int getStatus() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 系统解锁
     * @param uc
     * @return
     */
    public synchronized boolean unlock(String uc) {
        if (uc.equals(unlockCode)) {
            this.revert();
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
     * @param beforeCode 原来的设置代码，等级高的可以对低的恢复
     * @return
     */
    public synchronized boolean revert(int beforeCode) {
        if (beforeCode >= code) {
            this.revert();
            logger.warn("恢复系统状态,beforeCode={}", beforeCode);
            return true;
        } else {
            logger.warn("恢复系统状态失败,级别不够,beforeCode={},code={}", beforeCode,code);
            return false;
        }
    }

    private void revert(){
        code = ErrorCode.SUCCESS;
        message = null;
        unlockCode = null;
    }
}
