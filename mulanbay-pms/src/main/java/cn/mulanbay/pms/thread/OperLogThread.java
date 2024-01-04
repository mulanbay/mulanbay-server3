package cn.mulanbay.pms.thread;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.PmsErrorCode;
import cn.mulanbay.pms.handler.LogHandler;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import cn.mulanbay.pms.persistent.domain.OperLog;
import cn.mulanbay.pms.persistent.domain.SysFunc;
import cn.mulanbay.pms.persistent.domain.SysLog;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import cn.mulanbay.pms.util.BeanCopy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * 操作记录记录线程
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
public class OperLogThread extends BaseLogThread {

    private static final Logger logger = LoggerFactory.getLogger(OperLogThread.class);

    private OperLog log;

    public OperLogThread(OperLog log) {
        super("操作日志");
        this.log = log;
    }

    @Override
    public void run() {
        handleLog(log);
    }

    /**
     * 增加操作日志
     *
     * @param log
     */
    private void handleLog(OperLog log) {
        try {
            SystemConfigHandler systemConfigHandler = getSystemConfigHandler();
            SysFunc sf = log.getSysFunc();
            int errorCode = 0;
            String msgContent = "";
            if (log.getUrlAddress() != null) {
                msgContent = log.getUrlAddress();
            }
            if (sf == null) {
                logger.warn("找不到请求地址[" + log.getUrlAddress() + "],method[" + log.getMethod() + "]功能点配置信息");
            } else {
                errorCode = sf.getErrorCode();
                msgContent += "(" + sf.getFuncName() + ")";
                log.setSysFunc(sf);
                if (StringUtil.isNotEmpty(sf.getIdField())&&StringUtil.isEmpty(log.getIdValue())) {
                    Map<String, String> paraMap = (Map<String, String>) JsonUtil.jsonToBean(log.getParas(), Map.class);
                    log.setIdValue(this.getParaIdValue(sf, paraMap));
                }
            }
            Date now = new Date();
            log.setStoreTime(now);
            //会比较慢
            log.setHostIpAddress(systemConfigHandler.getHostIpAddress());
            log.setCreatedTime(now);
            //序列化比较耗时间
            //log.setParas(JsonUtil.beanToJson(changeToNormalMap(log.getParaMap())));
            log.setHandleDuration(log.getOccurEndTime().getTime() - log.getOccurStartTime().getTime());
            log.setStoreDuration(log.getStoreTime().getTime() - log.getOccurEndTime().getTime());
            if (log.getUserId() == null) {
                log.setUserId(0L);
                log.setUserName("未知");
            }
            BaseService baseService = BeanFactoryUtil.getBean(BaseService.class);
            baseService.saveObject(log);
            this.handleReward(sf, log);
            this.notifyError(log.getUserId(), errorCode, msgContent);
        } catch (Exception e) {
            logger.error("增加操作日志异常", e);
        }
    }

    /**
     * 积分奖励
     *
     * @param sf
     * @param log
     */
    private void handleReward(SysFunc sf, OperLog log) {
        try {

        } catch (Exception e) {
            logger.error("操作日志积分奖励处理异常", e);
        }

    }

    private void addParaNotFoundSystemLog(OperLog log) {
        //有可能在request的InputStream里面
        SysLog systemLog = new SysLog();
        BeanCopy.copyProperties(log, systemLog);
        systemLog.setLogLevel(LogLevel.WARNING);
        systemLog.setTitle("获取不到请求参数信息");
        systemLog.setContent("获取不到请求参数信息");
        systemLog.setErrorCode(PmsErrorCode.OPERATION_LOG_PARA_IS_NULL);
        BeanFactoryUtil.getBean(LogHandler.class).addSystemLog(systemLog);
    }

    private SystemConfigHandler getSystemConfigHandler() {
        return BeanFactoryUtil.getBean(SystemConfigHandler.class);
    }

}
