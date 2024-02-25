package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.City;
import cn.mulanbay.pms.persistent.domain.Country;
import cn.mulanbay.pms.persistent.domain.District;
import cn.mulanbay.pms.persistent.domain.Province;
import cn.mulanbay.pms.persistent.dto.work.BusinessTripDateStat;
import cn.mulanbay.pms.persistent.dto.work.BusinessTripMapStat;
import cn.mulanbay.pms.persistent.dto.work.WorkOvertimeDateStat;
import cn.mulanbay.pms.persistent.dto.work.WorkOvertimeStat;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.MapField;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.web.bean.req.work.businessTrip.BusinessTripDateStatSH;
import cn.mulanbay.pms.web.bean.req.work.businessTrip.BusinessTripMapStatSH;
import cn.mulanbay.pms.web.bean.req.work.overtime.WorkOvertimeDateStatSH;
import cn.mulanbay.pms.web.bean.req.work.overtime.WorkOvertimeStatSH;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * 工作
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
@Service
@Transactional
public class WorkService extends BaseHibernateDao {

    /**
     *   统计加班时间
     *
     * @param sf
     * @return
     */
    public List<WorkOvertimeDateStat> getWorkOvertimeDateStat(WorkOvertimeDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            String statSql = """
                    select indexValue,count(0) as totalCount,sum(hours) as totalHours from (
                    select {date_group_field} as indexValue,hours from work_overtime
                    {query_para}
                    ) tt group by indexValue order by indexValue
                    """;
            statSql = statSql.replace("{date_group_field}",MysqlUtil.dateTypeMethod("work_date", dateGroupType))
                             .replace("{query_para}",pr.getParameterString());
            List<WorkOvertimeDateStat> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, WorkOvertimeDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "加班统计异常", e);
        }
    }

    /**
     * 加班统计
     * @param sf
     * @return
     */
    public WorkOvertimeStat getWorkOvertimeStat(WorkOvertimeStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String sql = "select count(*) as totalCount,sum(hours) as totalHours from work_overtime";
            sql += pr.getParameterString();
            List<WorkOvertimeStat> list = this.getEntityListSI(sql, NO_PAGE,NO_PAGE_SIZE, WorkOvertimeStat.class, pr.getParameterValue());
            return list.get(0);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "加班统计异常", e);
        }
    }

    /**
     * 获取时间列表
     * @param sf
     * @return
     */
    public List<Date> getWorkOvertimeDateList(WorkOvertimeDateStatSH sf) {
        try {
            String sql = """
                    select start_time from work_overtime
                    {query_para}
                     order by start_time
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

    /**
     * 出差统计
     *
     * @param sf
     * @return
     */
    public List<BusinessTripDateStat> getBusinessTripDateStat(BusinessTripDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            String statSql = """
                    select indexValue,count(0) as totalCount,sum(days) as totalDays from (
                    select {date_group_field} as indexValue,days from business_trip
                    {query_para}
                    ) tt group by indexValue order by indexValue
                    """;
            statSql = statSql.replace("{date_group_field}",MysqlUtil.dateTypeMethod("trip_date", dateGroupType))
                    .replace("{query_para}",pr.getParameterString());
            List<BusinessTripDateStat> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, BusinessTripDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "出差统计异常", e);
        }
    }

    /**
     * 出差地图统计
     *
     * @param sf
     * @return
     */
    public List<BusinessTripMapStat> getBusinessTripMapStat(BusinessTripMapStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            MapField field = sf.getField();
            String statSql = """
                    select {field_name} as id,count(0) as totalCount,sum(days) as totalDays from business_trip
                     {query_para}
                    group by {field_name}
                    """;

            statSql = statSql.replace("{query_para}",pr.getParameterString())
                             .replace("{field_name}",field.getField());
            List<BusinessTripMapStat> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE, BusinessTripMapStat.class, pr.getParameterValue());
            for (BusinessTripMapStat bb : list) {
                switch (field){
                    case COUNTRY -> {
                        Country country = this.getEntityById(Country.class, bb.getId().longValue());
                        bb.setName(country.getCnName());
                        bb.setLocation(country.getLocation());
                    }
                    case PROVINCE -> {
                        Province province = this.getEntityById(Province.class, bb.getId().longValue());
                        bb.setName(province.getMapName());
                        bb.setLocation(province.getLocation());
                    }
                    case CITY -> {
                        City city = this.getEntityById(City.class, bb.getId().longValue());
                        bb.setName(city.getCityName());
                        bb.setLocation(city.getLocation());
                    }
                    case DISTRICT -> {
                        District district = this.getEntityById(District.class, bb.getId().longValue());
                        bb.setName(district.getDistrictName());
                        bb.setLocation(district.getLocation());
                    }
                }
            }
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "出差地图统计统计", e);
        }
    }


}
