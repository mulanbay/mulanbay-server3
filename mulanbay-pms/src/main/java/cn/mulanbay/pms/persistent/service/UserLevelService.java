package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.UserLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户等级
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Service
@Transactional
public class UserLevelService  extends BaseHibernateDao {

    /**
     * 获取预判定用户等级
     *
     * @param score
     * @param points
     * @return
     */
    public UserLevel getPreJudgeLevel(Integer score, Integer points) {
        try {
            String hql = "from UserLevel where points<=?1 and score<=?2 order by level desc";
            return this.getEntity(hql,UserLevel.class, points, score);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取预判定用户等级异常", e);
        }
    }

    /**
     * 获取用户等级配置
     *
     * @param level
     * @return
     */
    public UserLevel getUserLevel(Integer level) {
        try {
            String hql = "from UserLevel where level=?1";
            return this.getEntity(hql,UserLevel.class, level);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取用户等级配置异常", e);
        }
    }
}
