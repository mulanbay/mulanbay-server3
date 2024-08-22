package cn.mulanbay.schedule.impl;

import cn.mulanbay.schedule.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于日志的提醒
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class LogMessageProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(LogMessageProcessor.class);

    @Override
    public void handleScheduleMessage(Long taskTriggerId, int code, String title, String message) {
        logger.info("taskTriggerId="+taskTriggerId+",提醒消息,code="+code+",title="+title+",message="+message);
    }
}
