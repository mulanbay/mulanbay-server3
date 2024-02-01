package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

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
     * 恢复
     *
     * @param beforeCode 原来的设置代码，等级高的可以对低的恢复
     * @return
     */
    public synchronized boolean revert(int beforeCode) {
        if (beforeCode >= code) {
            code = ErrorCode.SUCCESS;
            message = null;
            logger.warn("恢复系统状态,beforeCode={}", beforeCode);
            return true;
        } else {
            logger.warn("恢复系统状态失败,级别不够,beforeCode={},code={}", beforeCode,code);
            return false;
        }

    }
}
