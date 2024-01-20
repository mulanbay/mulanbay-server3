package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.pms.handler.MessageSendHandler;
import cn.mulanbay.pms.handler.RedisDelayQueueHandler;
import cn.mulanbay.pms.persistent.domain.Message;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobExecuteResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Set;

/**
 * @author fenghong
 * @title: RedisDelayMessageSendJob
 * @description: 基于Redis的延迟消息发送调度
 * @date 2019-10-10 19:40
 */
public class SendRedisDelayMessageJob extends AbstractBaseJob {

    private static final Logger logger = LoggerFactory.getLogger(SendRedisDelayMessageJob.class);

    SendRedisDelayMessageJobPara para;

    RedisDelayQueueHandler redisDelayQueueHandler;

    MessageSendHandler messageSendHandler;

    @Override
    public TaskResult doTask() {
        TaskResult tr = new TaskResult();
        redisDelayQueueHandler = BeanFactoryUtil.getBean(RedisDelayQueueHandler.class);
        messageSendHandler = BeanFactoryUtil.getBean(MessageSendHandler.class);
        Set<Message> set = redisDelayQueueHandler.getNeedSendMessage(new Date());
        if (StringUtil.isEmpty(set)) {
            tr.setExecuteResult(JobExecuteResult.SKIP);
        } else {
            tr.setExecuteResult(JobExecuteResult.SUCCESS);
            int success = 0;
            int fail = 0;
            for (Message message : set) {
                boolean b = sendMessage(message);
                if (b) {
                    success++;
                } else {
                    fail++;
                }
            }
            tr.setComment("一共发送" + set.size() + "个消息,成功:" + success + "个,失败" + fail + "个");
        }
        return tr;
    }

    private boolean sendMessage(Message message) {
        try {
            Message mm = new Message();
            //需要拷贝一个新的，因为sendMessage会修改message内容，导致redisDelayQueueHandler无法删除
            BeanCopy.copyProperties(message, mm);
            boolean res = messageSendHandler.sendMessage(mm);
            redisDelayQueueHandler.removeMessage(message);
            if (!res && mm.getFailCount() < para.getMaxFails()) {
                //发送失败延迟1分钟重新发送
                Date newDate = new Date(message.getExpectSendTime().getTime() + para.getDelaySeconds() * 1000);
                message.setExpectSendTime(newDate);
                redisDelayQueueHandler.addMessage(message);
            }
            logger.debug("从Redis延迟队列消费了一个消息");
            return res;
        } catch (Exception e) {
            logger.error("从Redis延迟队列消费消息异常", e);
            return false;
        }
    }

    @Override
    public ParaCheckResult checkTriggerPara() {
        para = this.getTriggerParaBean();
        if (para == null) {
            para = new SendRedisDelayMessageJobPara();
        }
        return DEFAULT_SUCCESS_PARA_CHECK;
    }

    @Override
    public Class getParaDefine() {
        return SendRedisDelayMessageJobPara.class;
    }
}