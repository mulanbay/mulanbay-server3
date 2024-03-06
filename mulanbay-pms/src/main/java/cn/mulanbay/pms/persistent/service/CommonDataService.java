package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.CommonData;
import cn.mulanbay.pms.persistent.dto.commonData.CommonDataAnalyseStat;
import cn.mulanbay.pms.persistent.dto.commonData.CommonDataDateStat;
import cn.mulanbay.pms.persistent.dto.commonData.CommonDataStat;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.NearestType;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.web.bean.req.commonData.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
@Service
@Transactional
public class CommonDataService extends BaseHibernateDao {

    /**
     * 删除通用记录类型
     *
     * @param typeId
     */
    public void deleteCommonDataType(Long typeId) {
        try {
            //删除数据
            String sql1 = "delete from common_data where type_id=?1";
            this.execSqlUpdate(sql1,typeId);

            //删除类型
            String sql2 = "delete from common_data_type where type_id=?1";
            this.execSqlUpdate(sql2,typeId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "删除通用记录类型异常", e);
        }
    }

    /**
     * 分析
     *
     * @param sf 查询条件
     * @return
     */
    public List<CommonDataAnalyseStat> getCommonDataAnalyseStat(CommonDataAnalyseSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String fieldName = sf.getGroupField();
            String statSql = """
                    select * from (
                    select {field_name} as name,count(0) as totalCount,sum(value) as totalValue from common_data
                    {query_para}
                    group by {field_name}
                    ) as res
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString())
                             .replace("{field_name}",fieldName);
            List<CommonDataAnalyseStat> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, CommonDataAnalyseStat.class, pr.getParameterValue());
           return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "通用记录分享异常", e);
        }
    }

    /**
     * 统计
     *
     * @param sf
     * @return
     */
    public CommonDataStat getCommonDataStat(CommonDataStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select min(occur_time) as minDate,max(occur_time) as maxDate,count(0) as totalCount,sum(value) as totalValue
                    from common_data
                    {query_para}
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            return this.getEntitySQL(statSql,CommonDataStat.class,pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "统计异常", e);
        }
    }

    /**
     * 获取名称列表
     *
     * @return
     */
    public List<String> getNameList(CommonDataNameTreeSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String sql = "select distinct data_name from common_data ";
            sql += pr.getParameterString();
            return this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,String.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取名称列表异常", e);
        }
    }

    /**
     * 获取名称列表
     *
     * @return
     */
    public List<String> getTypeGroupNameList(Long userId) {
        try {
            String sql = "select distinct group_name from common_data_type where user_id=?1 ";
            return this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,String.class, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取名称列表异常", e);
        }
    }

    /**
     * 获取最近一条记录
     *
     * @return
     */
    public CommonData getNearest(CommonDataNearestSH sf) {
        try {
            NearestType nearestType = sf.getNearestType();
            PageRequest pr = sf.buildQuery();
            String hql = "from CommonData ";
            hql += pr.getParameterString();
            if(nearestType==NearestType.MIN_TIME){
                hql+= " order by occurTime asc";
            }else if(nearestType==NearestType.MAX_TIME){
                hql+= " order by occurTime desc";
            }else if(nearestType==NearestType.MIN_VALUE){
                hql+= " order by value asc";
            }else if(nearestType==NearestType.MAX_VALUE){
                hql+= " order by value desc";
            }
            return this.getEntity(hql,CommonData.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取最近一条记录异常", e);
        }
    }

    /**
     * 阅读统计
     *
     * @param sf
     * @return
     */
    public List<CommonDataDateStat> getCommonDataDateStat(CommonDataDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            String statSql = """
                    select indexValue,count(0) as totalCount,sum(value) as totalValue from (
                    select {date_group_field} as indexValue,value from common_data
                    {query_para}
                    ) tt group by indexValue order by indexValue
                    """;
            statSql = statSql.replace("{date_group_field}", MysqlUtil.dateTypeMethod("occur_time", dateGroupType))
                    .replace("{query_para}",pr.getParameterString());
            List<CommonDataDateStat> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, CommonDataDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "阅读统计异常", e);
        }
    }

    /**
     * 获取时间列表
     * @param sf
     * @return
     */
    public List<Date> getCommonDataDateList(CommonDataSH sf) {
        try {
            String hql = """
                    select occurTime from CommonData
                    {query_para}
                     order by occurTime
                    """;
            PageRequest pr = sf.buildQuery();
            hql = hql.replace("{query_para}",pr.getParameterString());
            List<Date> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Date.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取时间列表异常", e);
        }
    }


}
