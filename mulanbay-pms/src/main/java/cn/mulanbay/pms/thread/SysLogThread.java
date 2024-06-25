package cn.mulanbay.pms.thread;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.handler.SysCodeHandler;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import cn.mulanbay.pms.persistent.domain.SysCode;
import cn.mulanbay.pms.persistent.domain.SysFunc;
import cn.mulanbay.pms.persistent.domain.SysLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * 系统日记记录线程
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
public class SysLogThread extends BaseLogThread {

    private static final Logger logger = LoggerFactory.getLogger(SysLogThread.class);

    private SysLog log;

    public SysLogThread(SysLog log) {
        super("系统日志");
        this.log = log;
    }

    @Override
    public void run() {
        handleLog(log);
    }

    /**
     * 增加系统日志
     *
     * @param log
     */
    private void handleLog(SysLog log) {
        try {
            SysCodeHandler sysCodeHandler =  BeanFactoryUtil.getBean(SysCodeHandler.class);
            SysCode ec = sysCodeHandler.getSysCode(log.getErrorCode());
            if (ec != null && ec.getLoggable()) {
                SysFunc sf = log.getSysFunc();
                if (sf != null) {
                    log.setSysFunc(sf);
                    log.setIdValue(this.getParaIdValue(sf, log.getParaMap()));
                }
                log.setLogLevel(ec.getLevel());
                Date now = new Date();
                log.setStoreTime(now);
                SystemConfigHandler systemConfigHandler =  BeanFactoryUtil.getBean(SystemConfigHandler.class);
                //会比较慢
                log.setHostIpAddress(systemConfigHandler.getHostIpAddress());
                log.setCreatedTime(now);
                Map map = log.getParaMap();
                if (map != null && !map.isEmpty()) {
                    //序列化比较耗时间
                    log.setParas(JsonUtil.beanToJson(map));
                }
                log.setStoreDuration(log.getStoreTime().getTime() - log.getOccurTime().getTime());
                String content = log.getContent();
                if (content.length() >= 2000) {
                    content = content.substring(0, 2000);
                    log.setContent(content);
                }
                BaseService baseService = BeanFactoryUtil.getBean(BaseService.class);
                baseService.saveObject(log);
                this.notifyError(log.getUserId(), ec, log.getContent());
            }else{
                logger.warn("系统代码{}找不到配置",log.getErrorCode());
            }
        } catch (Exception e) {
            String msg = "增加系统日志异常，log=" + log.getContent();
            logger.error(msg, e);
        }
    }

}
