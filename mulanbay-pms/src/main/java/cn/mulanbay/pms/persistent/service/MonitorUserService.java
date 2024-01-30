package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.MonitorUser;
import cn.mulanbay.pms.persistent.enums.MonitorBussType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
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

    /**
     * 保存用户监控配置
     *
     * @param userId
     * @param bussTypes
     */
    public void save(Long userId, String bussTypes) {
        try {
            String hql = "delete from MonitorUser where userId=?1";
            this.updateEntities(hql, userId);

            if (StringUtil.isNotEmpty(bussTypes)) {
                List<MonitorUser> list = new ArrayList<>();
                String[] ids = bussTypes.split(",");
                for (String s : ids) {
                    MonitorUser rf = new MonitorUser();
                    rf.setUserId(userId);
                    MonitorBussType mbt = MonitorBussType.getMonitorBussType(Integer.parseInt(s));
                    rf.setBussType(mbt);
                    rf.setCreatedTime(new Date());
                    rf.setSmsNotify(true);
                    rf.setSysMsgNotify(true);
                    rf.setWxNotify(true);
                    list.add(rf);
                }
                this.saveEntities(list.toArray());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存用户监控配置异常", e);
        }
    }

}
