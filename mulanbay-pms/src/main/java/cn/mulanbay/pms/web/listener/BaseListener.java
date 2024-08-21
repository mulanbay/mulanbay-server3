package cn.mulanbay.pms.web.listener;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.pms.handler.LogHandler;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统监听器基类
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class BaseListener {

    private static final Logger logger = LoggerFactory.getLogger(BaseListener.class);

    /**
     * 记录日志
     * @param code
     * @param title
     * @param msg
     */
    protected void doLog(int code, String title, String msg) {
        try {
            LogHandler logHandler = BeanFactoryUtil.getBean(LogHandler.class);
            //增加IP地址和节点
            SystemConfigHandler systemConfigHandler = BeanFactoryUtil.getBean(SystemConfigHandler.class);
            msg = msg + ",IP地址:" + systemConfigHandler.getHostIpAddress() + "，节点编号:" + systemConfigHandler.getNodeId() + ".";
            logHandler.addSysLog(title, msg, code);
        } catch (Exception e) {
            logger.error("doLog 异常", e);
        }

    }
}
