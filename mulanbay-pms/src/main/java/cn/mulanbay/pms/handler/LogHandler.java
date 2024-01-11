package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.pms.persistent.domain.OperLog;
import cn.mulanbay.pms.persistent.domain.SysLog;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import cn.mulanbay.pms.thread.OperLogThread;
import cn.mulanbay.pms.thread.SysLogThread;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    ThreadPoolHandler threadPoolHandler;

    public LogHandler() {
        super("日志处理");
    }

    @Override
    public void init() {

    }

    public void addOperationLog(OperLog log) {
        OperLogThread thread = new OperLogThread(log);
        threadPoolHandler.pushThread(thread);
    }

    public void addSystemLog(SysLog log) {
        if (log.getOccurTime() == null) {
            log.setOccurTime(new Date());
        }
        SysLogThread thread = new SysLogThread(log);
        threadPoolHandler.pushThread(thread);
    }

    public void addSystemLog(LogLevel logLevel, String title, String content, int errorCode) {
        SysLog log = new SysLog();
        log.setUserId(0L);
        log.setUsername("系统操作");
        log.setTitle(title);
        log.setContent(content);
        log.setLogLevel(logLevel);
        log.setErrorCode(errorCode);
        if (log.getOccurTime() == null) {
            log.setOccurTime(new Date());
        }
        SysLogThread thread = new SysLogThread(log);
        threadPoolHandler.pushThread(thread);
    }

}
