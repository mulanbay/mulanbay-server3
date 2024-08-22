package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.handler.MessageSendHandler;
import cn.mulanbay.pms.handler.RedisDelayQueueHandler;
import cn.mulanbay.pms.persistent.domain.Message;
import cn.mulanbay.pms.persistent.enums.MessageSendStatus;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.schedule.para.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
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

    BaseService baseService;

    @Override
    public TaskResult doTask() {
        TaskResult tr = new TaskResult();
        redisDelayQueueHandler = BeanFactoryUtil.getBean(RedisDelayQueueHandler.class);
        messageSendHandler = BeanFactoryUtil.getBean(MessageSendHandler.class);
        baseService = BeanFactoryUtil.getBean(BaseService.class);
        Set<Message> set = redisDelayQueueHandler.getNeedSendMessage(new Date());
        if (StringUtil.isEmpty(set)) {
            tr.setResult(JobResult.SKIP);
        } else {
            tr.setResult(JobResult.SUCCESS);
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
            long expectTime = message.getExpectSendTime().getTime();
            if((System.currentTimeMillis()-expectTime)>para.getExpires()*1000L){
                logger.debug("消息已经过期");
                redisDelayQueueHandler.removeMessage(message);
                message.setSendStatus(MessageSendStatus.SKIP);
                message.setLastSendTime(new Date());
                message.setRemark("消息已经过期,无法发送消息");
                baseService.saveOrUpdateObject(message);
            }
            Message mm = new Message();
            //需要拷贝一个新的，因为sendMessage会修改message内容，导致redisDelayQueueHandler无法删除
            //todo 如果持久化产生msgId，应该不需要在拷贝了
            BeanCopy.copy(message, mm);
            //先移出队列,再发送，发送失败后再添加到队列中
            redisDelayQueueHandler.removeMessage(message);
            boolean res = messageSendHandler.sendMessage(mm);
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
