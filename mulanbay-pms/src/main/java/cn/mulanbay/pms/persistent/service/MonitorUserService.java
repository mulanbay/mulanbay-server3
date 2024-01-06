package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.MonitorUser;
import cn.mulanbay.pms.persistent.enums.MonitorBussType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统监控用户维护
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Service
@Transactional
public class MonitorUserService extends BaseHibernateDao {


    /**
     * 获取系统监控用户配置
     *
     * @param bussType
     * @return
     */
    public List<MonitorUser> selectListByType(MonitorBussType bussType) {
        try {
            String hql = "from MonitorUser where bussType=0 or bussType=?1";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE, MonitorUser.class, bussType);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取系统监控用户配置异常", e);
        }
    }

    /**
     * 获取系统监控用户配置
     *
     * @param userId
     * @return
     */
    public List<MonitorUser> selectList(Long userId) {
        try {
            String hql = "from MonitorUser where userId=?1";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE, MonitorUser.class, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取系统监控用户配置异常", e);
        }
    }

}
