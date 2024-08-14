package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.HandlerInfo;
import cn.mulanbay.business.handler.HandlerMethod;
import cn.mulanbay.business.handler.lock.RedisDistributedLock;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.persistent.domain.Message;
import cn.mulanbay.pms.persistent.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 基于redis的SortsSet的延迟队列
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class RedisDelayQueueHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(RedisDelayQueueHandler.class);

    @Value("${mulanbay.namespace}")
    String namespace;

    /**
     * 当缓存被清空时，重新从数据库加载时，最大的失效天数，
     */
    @Value("${mulanbay.notify.message.expiredDays:3}")
    int expiredDays;

    /**
     * 是否重启时清空队列中的消息
     */
    @Value("${mulanbay.notify.message.clearAfterRestart}")
    boolean clearAfterRestart;

    /**
     * 最大发送失败次数
     */
    @Value("${mulanbay.notify.message.send.maxFail:3}")
    int sendMaxFail;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    RedisDistributedLock redisDistributedLock;

    @Autowired
    MessageService userMessageService;

    @Autowired
    BaseService baseService;

    private String messageQueue = "userMessage";

    public RedisDelayQueueHandler() {
        super("Redis延迟消息队列");
    }

    @Override
    public void init() {
        if(clearAfterRestart){
            this.clearMessage();
        }
    }

    /**
     * redis缓存被清空的时候才需要
     */
    @HandlerMethod(desc = "加载数据库中未发送消息")
    private void loadUnSendMessage() {
        String key = "loadUnSendMessage";
        try {
            boolean b = redisDistributedLock.lock(key, 0);
            if (!b) {
                logger.warn("未发送消息队列已经被其他进程加载");
                return;
            }
            //第一步：清除
            redisTemplate.delete(this.getQueueName());
            logger.info("开始加载未发送消息队列");
            Date compareDate= DateUtil.getDate(-expiredDays);
            List<Message> list = userMessageService.getNeedSendMessage(1, 1000, sendMaxFail, compareDate);
            if (list.isEmpty()) {
                logger.debug("没有需要加载的未发送消息队列");
            } else {
                for (Message um : list) {
                    this.addMessage(um);
                }
                logger.info("一共加载了" + list.size() + "个未发送消息");
            }
            logger.info("加载未发送消息队列结束");
        } catch (Exception e) {
            logger.error("加载未发送消息队列异常", e);
        } finally {
            boolean b = redisDistributedLock.releaseLock(key);
            if (!b) {
                logger.warn("释放加载未发送消息队列锁key=" + key + "失败");
            }
        }
    }

    /**
     * 添加
     *
     * @param um
     */
    public void addMessage(Message um) {
        try {
            ZSetOperations zSetOperations = redisTemplate.opsForZSet();
            String key = getQueueName();
            if (um.getExpectSendTime() == null) {
                um.setExpectSendTime(new Date());
            }
            if(um.getMsgId()==null){
                //持久化，产生msgId
                baseService.saveObject(um);
            }
            //以预期发送时间顺序排列
            double score = um.getExpectSendTime().getTime();
            zSetOperations.add(key, um, score);
        } catch (Exception e) {
            logger.error("向Redis中添加消息异常", e);
        }
    }

    /**
     * 删除
     *
     * @param um
     */
    public void removeMessage(Message um) {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        String key = getQueueName();
        zSetOperations.remove(key, um);
    }

    /**
     * 清除所有的未发送消息
     */
    @HandlerMethod(desc = "清除所有的未发送消息")
    public void clearMessage() {
        redisTemplate.delete(this.getQueueName());
        logger.info("清除所有的未发送消息");
    }

    /**
     * 获取队列名
     *
     * @return
     */
    private String getQueueName() {
        return namespace + ":" + messageQueue;
    }

    /**
     * 获取需要发送的消息
     *
     * @param now
     * @return
     */
    public Set<Message> getNeedSendMessage(Date now) {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        Set<Message> sets = zSetOperations.rangeByScore(getQueueName(), 0, now.getTime());
        return sets;
    }

    @Override
    public HandlerInfo getHandlerInfo() {
        HandlerInfo hi = super.getHandlerInfo();
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        String key = getQueueName();
        double max = System.currentTimeMillis();
        //10天内需要发送的消息（不包含已经过期的）
        double max10 = max + 10 * 24 * 3600 * 1000;
        hi.addDetail("已经过期的总消息数", zSetOperations.count(key, 0, max).toString());
        hi.addDetail("10天内需要发送的总消息数", zSetOperations.count(key, max, max10).toString());
        return hi;
    }

}
