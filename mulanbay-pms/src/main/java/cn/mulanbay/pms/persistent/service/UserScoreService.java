package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.ScoreConfig;
import cn.mulanbay.pms.persistent.domain.UserScore;
import cn.mulanbay.pms.persistent.domain.UserScoreDetail;
import cn.mulanbay.pms.persistent.dto.score.UserScorePointsCompareDTO;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.LogCompareType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 用户评分
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
@Service
@Transactional
public class UserScoreService extends BaseHibernateDao {

    /**
     * 保存用户评分
     *
     * @param us
     * @param list
     * @param redo 重新保存
     */
    public void saveUserScore(UserScore us, List<UserScoreDetail> list, boolean redo) {
        try {
            if (redo) {
                String hh = "from UserScore where userId=?1 and startTime=?2 and endTime=?3";
                UserScore old = this.getEntity(hh,UserScore.class, us.getUserId(), us.getStartTime(), us.getEndTime());
                if (old != null) {
                    String hql = "delete from UserScoreDetail where userScoreId=?0 ";
                    this.updateEntities(hql, old.getId());
                    this.removeEntity(old);
                }
            }
            this.saveEntity(us);
            for (UserScoreDetail dd : list) {
                dd.setUserScoreId(us.getId());
                this.saveEntity(dd);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR, "保存用户评分失败！", e);
        }
    }

    /**
     * 获取评分配置
     *
     * @return
     */
    public List<ScoreConfig> selectActiveScoreConfigList(Long scoreGroupId) {
        try {
            String hql = "from ScoreConfig where status=?1 and groupId=?2 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,ScoreConfig.class,CommonStatus.ENABLE, scoreGroupId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取积分配置异常", e);
        }
    }

    /**
     * 获取用户最新的评分
     *
     * @param userId
     * @return
     */
    public UserScore getLatestScore(Long userId) {
        try {
            String hql = "from UserScore where userId=?1 order by endTime desc";
            return this.getEntity(hql,UserScore.class, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取用户最新的评分异常", e);
        }
    }

    /**
     * 获取用户最新的评分
     *
     * @param userId
     * @param date
     * @return
     */
    public UserScore getScore(Long userId,Date date) {
        try {
            String hql = "from UserScore where userId=?1 and endTime<=?2 order by endTime desc";
            return this.getEntity(hql,UserScore.class, userId,date);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取用户最新的评分异常", e);
        }
    }

    /**
     * 获取用户评分列表
     *
     * @param userId
     * @return
     */
    public List<UserScore> getList(Long userId, int n) {
        try {
            String hql = "from UserScore where userId=?1 order by endTime desc";
            return this.getEntityListHI(hql, 1, n,UserScore.class, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户评分列表异常", e);
        }
    }

    /**
     * 获取评分详情
     *
     * @return
     */
    public List<UserScoreDetail> selectDetailList(Long userScoreId) {
        try {
            String hql = "from UserScoreDetail where userScoreId=?1 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,UserScoreDetail.class, userScoreId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取评分详情异常", e);
        }
    }

    /**
     * 获取积分的原始值
     *
     * @return
     */
    public double getScoreValue(String sql, Long userId, Date start, Date end) {
        try {
            List list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,Object.class, userId, start, end);
            Object o = list.get(0);
            if (o == null) {
                return 0;
            } else {
                return Double.valueOf(o.toString());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取积分的原始值异常", e);
        }
    }

    /**
     * 获取需要积分统计的用户Id
     *
     * @return
     */
    public List<Long> selectNeedStatScoreUserIdList() {
        try {
            String hql = "select userId from UserSet where statScore=?1";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Long.class,true);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取需要积分统计的用户Id异常", e);
        }
    }

    /**
     * 积分和评分比对
     *
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<UserScorePointsCompareDTO> scorePointsCompare(Long userId, Date startTime, Date endTime) {
        try {
            String sql="""
                    select ss.*,pp.points from
                    (select date ,avg(score) as score from (
                    SELECT  CAST(DATE_FORMAT(end_time,'%Y%m%d') AS signed) as date,score FROM user_score
                    where user_id=?1 and end_time>=?2 and end_time<=?3
                    ) as res group by date) as ss,
                    (select date ,avg(points) as points from (
                    SELECT  CAST(DATE_FORMAT(created_time,'%Y%m%d') AS signed) as date,after_points as points FROM user_reward
                    where user_id=?4 and created_time>=?5 and created_time<=?6
                    ) as res group by date) as pp
                    where ss.date = pp.date
                    order by ss.date
                    """;
            List<UserScorePointsCompareDTO> list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,
                    UserScorePointsCompareDTO.class, userId, startTime, endTime, userId, startTime, endTime);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "积分和评分比对异常", e);
        }
    }

    /**
     * 获取最近的ID
     * @param currentId
     * @param userId
     * @param compareType
     * @return
     */
    public Long getNearestId(Long currentId,Long userId, LogCompareType compareType){
        try {
            String hql=null;
            if(compareType==LogCompareType.EARLY){
               hql = "select id from UserScore where id <?1 and userId=?2 order by id desc";
            }else{
                hql = "select id from UserScore where id >?1 and userId=?2 order by id asc";
            }
            return this.getEntity(hql,Long.class,currentId,userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取最近的ID异常", e);
        }
    }
}
