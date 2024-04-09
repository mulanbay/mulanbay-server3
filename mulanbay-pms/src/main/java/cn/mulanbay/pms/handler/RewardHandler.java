package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.thread.RewardsThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 积分奖励
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class RewardHandler extends BaseHandler {

    @Autowired
    ThreadPoolHandler threadPoolHandler;

    public RewardHandler() {
        super("积分等级处理");
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
        RewardsThread thread = new RewardsThread(userId, rewards, sourceId, bussSource, remark, messageId);
        threadPoolHandler.pushThread(thread);
    }


}
