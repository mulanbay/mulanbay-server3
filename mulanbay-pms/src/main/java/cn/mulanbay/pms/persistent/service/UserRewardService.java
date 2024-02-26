package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.User;
import cn.mulanbay.pms.persistent.domain.UserReward;
import cn.mulanbay.pms.persistent.enums.RewardSource;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author fenghong
 * @date 2024/2/26
 */
@Service
public class UserRewardService  extends BaseHibernateDao {


    /**
     * 更新用户积分
     *
     * @param userId
     */
    public void updateUserPoint(Long userId, int rewards, Long sourceId, RewardSource rewardSource, String remark, Long messageId) {
        try {
            //更新积分值
            User user = this.getEntityById(User.class, userId);
            int afterPoints = user.getPoints() + rewards;
            user.setPoints(afterPoints);
            this.updateEntity(user);
            //增加记录
            UserReward record = new UserReward();
            record.setCreatedTime(new Date());
            record.setSourceId(sourceId);
            record.setSource(rewardSource);
            record.setRewards(rewards);
            record.setUserId(userId);
            record.setAfterPoints(afterPoints);
            record.setRemark(remark);
            record.setMessageId(messageId);
            this.saveEntity(record);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "更新用户积分异常", e);
        }
    }

}
