package cn.mulanbay.pms.thread;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.handler.UserHandler;
import cn.mulanbay.pms.persistent.domain.SysFunc;
import cn.mulanbay.pms.persistent.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 日志记录基础线程
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
public class BaseLogThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(BaseLogThread.class);

    public BaseLogThread(String name) {
        super(name);
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
        try {
            if (code == ErrorCode.SUCCESS) {
                return;
            }
            //通知
            NotifyHandler notifyHandler = BeanFactoryUtil.getBean(NotifyHandler.class);
            notifyHandler.addMessageToNotifier(code, "错误代码通知", message + "," + getUserInfo(userId),
                    null, null);
        } catch (Exception e) {
            logger.error("处理错误代码异常", e);
        }
    }

    private String getUserInfo(Long userId) {
        if (userId == null) {
            return "";
        } else {
            String s = "操作人UserId:" + userId;
            UserHandler userHandler = BeanFactoryUtil.getBean(UserHandler.class);
            User user = userHandler.getUser(userId);
            if (user != null) {
                s += ",手机号:" + user.getPhone();
            }
            return s;
        }
    }
}
