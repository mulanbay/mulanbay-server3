package cn.mulanbay.pms.thread;

import cn.mulanbay.business.handler.lock.RedisDistributedLock;
import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.LogHandler;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.service.UserRewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 积分记录线程
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
public class RewardsThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RewardsThread.class);

    private Long userId;

    private int rewards;

    private Long sourceId;

    private BussSource bussSource;

    private String remark;

    private Long messageId;

    public RewardsThread(Long userId, int rewards, Long sourceId, BussSource bussSource, String remark, Long messageId) {
        this.userId = userId;
        this.rewards = rewards;
        this.sourceId = sourceId;
        this.bussSource = bussSource;
        this.remark = remark;
        this.messageId = messageId;
    }

    @Override
    public void run() {
        String key = CacheKey.getKey(CacheKey.REWARD_POINT_LOCK, String.valueOf(userId));
        RedisDistributedLock redisDistributedLock = BeanFactoryUtil.getBean(RedisDistributedLock.class);
        try {
            boolean b = redisDistributedLock.lock(key, 5000L, 3, 20);
            if (!b) {
                logger.error("获取更新用户积分锁失败");
                this.addErrorLog("获取更新用户积分锁失败", "获取更新用户积分锁失败,key=" + key, PmsCode.USER_REWARD_UPDATE_LOCK_FAIL);
                return;
            }
            // 获取当前的积分
            UserRewardService userRewardService = BeanFactoryUtil.getBean(UserRewardService.class);
            userRewardService.updateUserPoint(userId, rewards, sourceId, bussSource, remark, messageId);
        } catch (Exception e) {
            logger.error("更新用户ID=" + userId + "积分异常", e);
            this.addErrorLog("更新用户积分锁异常", "更新用户积分锁异常,key=" + key, PmsCode.USER_REWARD_UPDATE_ERROR);
        } finally {
            redisDistributedLock.releaseLock(key);
        }
    }

    private void addErrorLog(String title, String content, int errorCode) {
        try {
            LogHandler logHandler = BeanFactoryUtil.getBean(LogHandler.class);
            logHandler.addSysLog(title, content, errorCode);
        } catch (Exception e) {
            logger.error("处理用户积分的失败日志记录异常", e);
        }
    }
}
