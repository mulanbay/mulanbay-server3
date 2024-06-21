package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.lock.DistributedLock;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.service.UserRewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 积分奖励
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class RewardHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(RewardHandler.class);

    /**
     * 分布式锁重试次数
     */
    @Value("${mulanbay.lock.retryTimes:3}")
    int retryTimes;

    /**
     * 分布式锁超时时间(毫秒)
     */
    @Value("${mulanbay.lock.expire:3000}")
    long expire;

    @Autowired
    UserRewardService userRewardService;

    @Autowired
    ThreadPoolHandler threadPoolHandler;

    @Autowired
    LogHandler logHandler;

    @Autowired
    DistributedLock distributedLock;

    public RewardHandler() {
        super("积分奖励处理");
    }

    /**
     * 积分奖励
     *
     * @param userId
     * @param rewards
     * @param sourceId
     * @param bussSource
     * @param remark
     * @param messageId
     */
    public void reward(Long userId, int rewards, Long sourceId, BussSource bussSource, String remark, Long messageId) {
        threadPoolHandler.pushThread(() -> {
            String key = CacheKey.getKey(CacheKey.REWARD_POINT_LOCK, String.valueOf(userId));
            boolean lock = false;
            try {
                lock = distributedLock.lock(key, expire, retryTimes);
                if (!lock) {
                    logger.error("获取更新用户积分锁失败");
                    return;
                }
                // 获取当前的积分
                userRewardService.updateUserPoint(userId, rewards, sourceId, bussSource, remark, messageId);
            } catch (Exception e) {
                logger.error("更新用户ID=" + userId + "积分异常", e);
                logHandler.addSysLog("更新用户积分锁异常", "更新用户积分锁异常,key=" + key, PmsCode.USER_REWARD_UPDATE_ERROR);
            } finally {
                if (lock) {
                    distributedLock.releaseLock(key);
                }
            }
        });
    }
}
