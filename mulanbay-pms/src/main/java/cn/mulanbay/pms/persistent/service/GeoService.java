package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.City;
import cn.mulanbay.pms.persistent.domain.Country;
import cn.mulanbay.pms.persistent.domain.District;
import cn.mulanbay.pms.persistent.domain.Province;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * GEO
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
@Service
@Transactional
public class GeoService extends BaseHibernateDao {

    /**
     * 获取省份列表
     *
     * @return
     */
    public List<Province> getProvinceList(Long countryId) {
        try {
            String hql = "from Province where countryId=?1 order by orderIndex";
            List<Province> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Province.class,countryId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取省份列表异常", e);
        }
    }

    /**
     * 获取国家列表
     *
     * @return
     */
    public List<Country> getCountryList() {
        try {
            String hql = "from Country where status=?1 order by orderIndex ";
            List<Country> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Country.class, CommonStatus.ENABLE);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取获取国家列表异常", e);
        }
    }

    /**
     * 获取城市列表
     *
     * @return
     */
    public List<City> getCityList(Long provinceId) {
        try {
            String hql = "from City where provinceId=?1 ";
            List<City> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,City.class, provinceId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取城市列表异常", e);
        }
    }

    /**
     * 获取县（地区）列表
     *
     * @return
     */
    public List<District> getDistrictList(Long cityId) {
        try {
            String hql = "from District where cityId=?1  order by orderIndex";
            List<District> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,District.class, cityId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取县（地区）列表异常", e);
        }
    }

    public void updateGeo(String name,String code,String location) {
        try {
            String sql1 = "update province set code=?1,location=?2 where province_name=?3 ";
            this.execSqlUpdate(sql1,code,location,name);

            String sql2 = "update city set code=?1,location=?2 where city_name=?3 ";
            this.execSqlUpdate(sql2,code,location,name);

            String sql3 = "update district set code=?1,location=?2 where district_name=?3 ";
            this.execSqlUpdate(sql3,code,location,name);

        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取县（地区）列表异常", e);
        }
    }
}
