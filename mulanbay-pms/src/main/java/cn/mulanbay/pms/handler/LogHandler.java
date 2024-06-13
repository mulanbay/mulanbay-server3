package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.pms.persistent.domain.OperLog;
import cn.mulanbay.pms.persistent.domain.SysLog;
import cn.mulanbay.pms.thread.OperLogThread;
import cn.mulanbay.pms.thread.SysLogThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 日志处理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class LogHandler extends BaseHandler {

    @Value("${mulanbay.log.operLog}")
    boolean enableOperLog;

    @Value("${mulanbay.log.sysLog}")
    boolean enableSysLog;

    @Autowired
    ThreadPoolHandler threadPoolHandler;

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
        OperLogThread thread = new OperLogThread(log);
        threadPoolHandler.pushThread(thread);
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
        SysLogThread thread = new SysLogThread(log);
        threadPoolHandler.pushThread(thread);
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
        SysLogThread thread = new SysLogThread(log);
        threadPoolHandler.pushThread(thread);
    }

}
