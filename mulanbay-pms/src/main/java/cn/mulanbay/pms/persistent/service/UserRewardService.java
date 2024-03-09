package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.User;
import cn.mulanbay.pms.persistent.domain.UserReward;
import cn.mulanbay.pms.persistent.dto.reward.UserRewardDateStat;
import cn.mulanbay.pms.persistent.dto.reward.UserRewardSourceStat;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.web.bean.req.data.reward.UserRewardDateStatSH;
import cn.mulanbay.pms.web.bean.req.data.reward.UserRewardSourceStatSH;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author fenghong
 * @date 2024/2/26
 */
@Transactional
@Service
public class UserRewardService  extends BaseHibernateDao {

    /**
     * 获取积分列表
     *
     * @param userId
     * @return
     */
    public List<UserReward> getUserRewardList(Long userId, int pageSize) {
        try {
            String hql = "from UserReward where userId=?1 order by createdTime desc";
            return this.getEntityListHI(hql, 1, pageSize,UserReward.class, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获获取积分列表异常", e);
        }
    }

    /**
     * 更新用户积分
     *
     * @param userId
     */
    public void updateUserPoint(Long userId, int rewards, Long sourceId, BussSource BussSource, String remark, Long messageId) {
        try {
            //更新积分值
            User user = this.getEntityById(User.class, userId);
            int afterPoints = user.getPoints() + rewards;
            user.setPoints(afterPoints);
            this.updateEntity(user);
            //增加记录
            UserReward record = new UserReward();
            record.setSourceId(sourceId);
            record.setSource(BussSource);
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

    /**
     * 还原用户积分
     *
     * @param bean
     */
    public void revertUserPoint(UserReward bean, String remark) {
        try {
            //更新积分
            this.updateUserPoint(bean.getUserId(), -bean.getRewards(), bean.getSourceId(), BussSource.MANUAL, remark, bean.getMessageId());
            //删除记录
            this.removeEntity(bean);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "还原用户积分异常", e);
        }
    }

    /**
     * 来源统计
     *
     * @param sf
     * @return
     */
    public List<UserRewardSourceStat> getUserRewardSourceStat(UserRewardSourceStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String groupField = "source";
            if(sf.getSource()!=null){
                groupField = "source_id";
            }
            String sql= """
                    select {group_field} as id,count(0) as totalCount,sum(rewards) as totalRewards from user_reward
                     {query_para}
                     group by {group_field}
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString())
                     .replace("{group_field}",groupField);
            return this.getEntityListSI(sql, NO_PAGE,NO_PAGE_SIZE,UserRewardSourceStat.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取来源统计异常", e);
        }
    }

    /**
     * 分数统计
     *
     * @param sf
     * @return
     */
    public List<UserRewardSourceStat> getUserRewardScoreStat(UserRewardSourceStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String sql= """
                    select score as id,count(0) as totalCount,sum(rewards) as totalRewards from
                    (
                     select
                     (CASE rewards \n
                     WHEN rewards>0 THEN 1
                     WHEN rewards<0 THEN -1
                     ELSE 0 END) as score,rewards from user_reward
                     {query_para}
                    ) as res
                    group by score
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString());
            return this.getEntityListSI(sql, NO_PAGE,NO_PAGE_SIZE,UserRewardSourceStat.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "分数统计异常", e);
        }
    }


    /**
     * 口琴练习统计
     *
     * @param sf
     * @return
     */
    public List<UserRewardDateStat> getDateStat(UserRewardDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            String statSql = """
                    select indexValue,sum(rewards) as totalRewards ,count(0) as totalCount
                    from (select {date_group_field} as indexValue,rewards from user_reward
                    {query_para}
                    ) tt group by indexValue order by indexValue
                    """;
            statSql = statSql.replace("{date_group_field}", MysqlUtil.dateTypeMethod("created_time", dateGroupType))
                    .replace("{query_para}", pr.getParameterString());
            List<UserRewardDateStat> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, UserRewardDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "口琴练习统计异常", e);
        }
    }

    /**
     * 获取时间列表
     * @param sf
     * @return
     */
    public List<Date> getRewardDateList(UserRewardDateStatSH sf) {
        try {
            String sql = """
                    select created_time from music_practice
                    {query_para}
                     order by user_reward
                    """;
            PageRequest pr = sf.buildQuery();
            sql = sql.replace("{query_para}",pr.getParameterString());
            List<Date> list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,Date.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取时间列表异常", e);
        }
    }

}
