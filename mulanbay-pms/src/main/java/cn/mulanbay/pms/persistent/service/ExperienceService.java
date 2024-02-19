package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.dto.life.*;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.ExperienceType;
import cn.mulanbay.pms.persistent.enums.MapType;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.web.bean.req.life.experience.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ExperienceService extends BaseHibernateDao {

    /**
     * 人生经历地图统计
     *
     * @param sf
     * @return
     */
    public List<ExperienceMapStat> getMapStat(ExperienceMapStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            MapType mapType = sf.getMapType();
            String statSql = """
                    select {group_cdn},count(0) as totalCount,count(0) as totalDays,sum(cost) as totalCost from experience_detail
                     {query_para}
                    and map_stat=1 group by {group_field}
                    """;
            String groupCdn = null;
            String groupField = null;
            if (mapType == MapType.CHINA) {
                groupCdn ="province_id as id,null as name";
                groupField = "province_id";
            } else if (mapType == MapType.WORLD) {
                groupCdn ="country_id as id,null as name";
                groupField = "country_id";
            } else if (mapType == MapType.LOCATION) {
                groupCdn ="null as id,arrive_city as name";
                groupField = "arrive_city";
            }
            statSql = statSql.replace("{query_para}",pr.getParameterString())
                             .replace("{group_cdn}",groupCdn)
                             .replace("{group_field}",groupField);
            List<ExperienceMapStat> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE, ExperienceMapStat.class, pr.getParameterValue());
            for (ExperienceMapStat bb : list) {
                if(bb.getId()==null){

                }else if (mapType == MapType.CHINA) {
                    Province province = this.getEntityById(Province.class, bb.getId().longValue());
                    bb.setName(province.getMapName());
                }else if (mapType == MapType.WORLD) {
                    Country country = this.getEntityById(Country.class, bb.getId().longValue());
                    bb.setName(country.getCnName());
                }
            }
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "人生经历地图统计", e);
        }
    }

    /**
     * 人生经历地图统计
     *
     * @param sf
     * @return
     */
    public List<ExperienceWorldMapStat> getWorldMapStat(ExperienceMapStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select country_id as countryId,max(country_location) as countryLocation,count(0) as totalCount,count(0) as totalDays,sum(cost) as totalCost
                     from experience_detail
                     {query_para}
                    and map_stat=1 group by country_id
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<ExperienceWorldMapStat> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE, ExperienceWorldMapStat.class, pr.getParameterValue());
            for(ExperienceWorldMapStat ms : list){
                //toDo 后期缓存或者map
                Country c = this.getEntityById(Country.class,ms.getCountryId().longValue());
                ms.setCountryName(c.getCnName());
            }
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "人生经历地图统计", e);
        }
    }

    /**
     * 获取出发城市列表
     *
     * @return
     */
    public List<String> getStartCityList(Long userId) {
        try {
            String statSql = """
                    select distinct start_city as startCity from experience_detail led,
                    (select exp_id as lid,min(occur_date) as occur_date from experience_detail group by exp_id )  as tt
                    where led.occur_date = tt.occur_date and led.exp_id = tt.lid and led.user_id=?1
                    """;
            return this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE,String.class, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取出发城市列表异常", e);
        }
    }

    /**
     * 获取迁徙地图的数据
     *
     * @param sf
     * @return
     */
    public List<WorldTransferMapStat> getWorldTransMapStat(ExperienceMapStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select start_city as startCity,sc_location as scLocation,arrive_city as arriveCity,ac_location as acLocation
                    from experience_detail
                    {query_para}
                    and map_stat=1 and international=1
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<WorldTransferMapStat> list = this.getEntityListSI(statSql,
                    NO_PAGE,NO_PAGE_SIZE, WorldTransferMapStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "人生经历地图统计", e);
        }
    }

    /**
     * 获取国家地理位置信息
     *
     * @param countryId
     * @return
     */
    public String getCountryLocation(Long countryId) {
        try {
            String hql = "select countryLocation from ExperienceDetail where countryId=?1 ";
            String s = this.getEntity(hql,String.class,countryId);
            return s;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取国家地理位置信息异常", e);
        }
    }

    /**
     * 获取城市地理位置信息
     *
     * @param city
     * @return
     */
    public String getCityLocation(String city) {
        try {
            String hql = "from ExperienceDetail where startCity=?1 or arriveCity=?2";
            ExperienceDetail ed = this.getEntity(hql,ExperienceDetail.class,city,city);
            if(ed==null){
                return null;
            }else{
                if(ed.getStartCity().equals(city)){
                    return ed.getScLocation();
                }else{
                    return ed.getAcLocation();
                }
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取城市地理位置信息异常", e);
        }
    }

    /**
     * 人生经历基于日期的统计
     *
     * @param sf
     * @return
     */
    public List<ExperienceDateStat> getDateStat(ExperienceDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            String statSql = """
                    select indexValue,count(0) as totalCount,sum(days) as totalDays
                    from ( select {date_group_field} as indexValue,days from experience
                    {query_para}
                    ) tt group by indexValue order by indexValue
                    """;
            statSql = statSql.replace("{date_group_field}",MysqlUtil.dateTypeMethod("start_date", dateGroupType))
                             .replace("{query_para}",pr.getParameterString());
            List<ExperienceDateStat> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE, ExperienceDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "人生经历基于日期的统计异常", e);
        }
    }

    /**
     * 人生经历基于消费类型的花费统计
     *
     * @param sf
     * @return
     */
    public List<ExperienceCostStat> getCostStatByConsume(ExperienceCostStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select lec.goods_type_id as indexValue,ct.type_name as name,sum(cost) as totalCost
                    from experience_consume lec,goods_type ct
                    where lec.goods_type_id=ct.type_id
                    and lec.detail_id in
                    (select detail_id from experience_detail where exp_id in (select exp_id from experience
                    {query_para}
                    )) group by lec.goods_type_id,ct.type_name
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<ExperienceCostStat> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE, ExperienceCostStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "人生经历基于消费类型的花费统计异常", e);
        }
    }

    /**
     * 人生经历基于ID的花费统计
     *
     * @param sf
     * @return
     */
    public List<ExperienceCostStat> getCostStatByExp(ExperienceCostStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select exp_id as indexValue,exp_name as name,sum(cost) as totalCost
                    from experience
                    {query_para}
                    group by exp_id,exp_name
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<ExperienceCostStat> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE, ExperienceCostStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "人生经历基于ID的花费统计异常", e);
        }
    }

    /**
     * 人生经历基于经历类型的花费统计
     *
     * @param sf
     * @return
     */
    public List<ExperienceCostStat> getCostStatByType(ExperienceCostStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select type as indexValue,sum(cost) as totalCost
                    from experience
                    {query_para}
                    group by type
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<ExperienceCostStat> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE, ExperienceCostStat.class, pr.getParameterValue());
            for (ExperienceCostStat oo : list) {
                ExperienceType et = ExperienceType.getExperienceType(oo.getIndexValue().intValue());
                oo.setName(et.getName());
            }
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "人生经历基于经历类型的花费统计异常", e);
        }
    }

    /**
     * 更新人生经历详情，附带更新人生经历的花费
     *
     * @return
     */
    public void saveOrUpdateDetail(ExperienceDetail bean, boolean updateStat) {
        try {
            if (bean.getDetailId() == null) {
                saveOrUpdateExperience(bean, false);
            } else {
                saveOrUpdateExperience(bean, true);
            }
            if (updateStat) {
                updateExperienceCost(bean.getExperience().getExpId());
            }
        } catch (PersistentException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "更新人生经历详情异常", e);
        }
    }

    /**
     * 更新人生经历花费
     *
     * @return
     */
    public void updateExperienceCost(Long expId) {
        try {
            String sql = "update Experience set cost =(select sum(cost) from ExperienceDetail where experience.expId =?1 ) where expId=?2 ";
            this.updateEntities(sql, expId, expId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "更新人生经历花费异常", e);
        }
    }

    /**
     * 更新人生经历明细花费
     *
     * @return
     */
    public void updateDetailStat(Long detailId) {
        try {
            String sql = "update ExperienceDetail set cost =(select sum(cost) from ExperienceConsume where detail.detailId =?1 ) where detailId=?2 ";
            this.updateEntities(sql, detailId, detailId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "更新人生经历明细花费异常", e);
        }
    }

    /**
     * 通过购买消费记录更新人生经历明细花费
     *
     * @return
     */
    public void updateCostByConsume(Consume consume) {
        try {
            String hql = "from ExperienceConsume where scId=?1 ";
            List<ExperienceConsume> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,ExperienceConsume.class, consume.getConsumeId());
            for (ExperienceConsume bean : list) {
                bean.setConsumeName(consume.getGoodsName());
                bean.setCost(consume.getTotalPrice());
                this.saveOrUpdateConsume(bean, true);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "更新人生经历明细花费异常", e);
        }
    }

    /**
     * 更新人生经历消费，附带更新人生经历详情的花费
     *
     * @return
     */
    public void saveOrUpdateConsume(ExperienceConsume bean, boolean updateStat) {
        try {
            if (bean.getConsumeId() == null) {
                saveOrUpdateExperience(bean, false);
            } else {
                saveOrUpdateExperience(bean, true);
            }
            //还需要去更新BuyRecord的关键字
            if (bean.getScId() != null) {
                Consume consume = this.getEntityById(Consume.class, bean.getScId());
                ExperienceDetail detail = this.getEntityById(ExperienceDetail.class, bean.getDetail().getDetailId());
                //更新关键字
                String name = detail.getExperience().getExpName();
                String tags = consume.getTags();
                boolean needUpdate = false;
                if (StringUtil.isEmpty(tags)) {
                    tags = name;
                    needUpdate = true;
                } else {
                    if (tags.contains(name)) {
                        //已经包含就不更新
                    } else {
                        tags += "," + name;
                        needUpdate = true;
                    }
                }
                if (needUpdate) {
                    consume.setTags(tags);
                    this.updateEntity(consume);
                }
            }
            if (updateStat) {
                ExperienceDetail detail = this.getEntityById(ExperienceDetail.class, bean.getDetail().getDetailId());
                //更新明细
                updateDetailStat(bean.getDetail().getDetailId());
                //此时detail没有完全加载，需要手动加载
                //更新总的人生经历
                updateExperienceCost(detail.getExperience().getExpId());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "更新人生经历消费，附带更新人生经历详情的花费异常", e);
        }
    }


    /**
     * 删除人生经历消费
     *
     * @return
     */
    public void deleteExperienceConsume(Long consumeId, boolean updateStat) {
        try {
            ExperienceConsume bean = this.getEntityById(ExperienceConsume.class, consumeId);
            Long detailId = bean.getDetail().getDetailId();
            Long expId = bean.getDetail().getExperience().getExpId();
            //删除
            deleteExperienceConsume(bean);
            if (updateStat) {
                //更新明细
                updateDetailStat(detailId);
                //更新总的人生经历
                updateExperienceCost(expId);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "删除人生经历消费异常", e);
        }
    }

    /**
     * 独立事务删除人生经历消费信息
     *
     * @param bean
     */
    public void deleteExperienceConsume(ExperienceConsume bean) {
        try {
            //删除
            this.removeEntity(bean);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "独立事务删除人生经历消费信息异常", e);
        }
    }

    /**
     * 独立事务删除人生经历消费信息
     *
     * @param bean
     */
    public void deleteExperienceDetail(ExperienceDetail bean) {
        try {
            //删除消费
            String sql = "delete from ExperienceConsume where detail.detailId=?1 ";
            this.updateEntities(sql, bean.getDetailId());
            //删除
            this.removeEntity(bean);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "独立事务删除人生经历消费信息异常", e);
        }
    }

    /**
     * 删除人生经历详情
     *
     * @return
     */
    public void deleteExperienceDetail(Long detailId, Long userId, boolean updateStat) {
        try {
            String hql = "from ExperienceDetail where detailId=?1 and userId=?2 ";
            ExperienceDetail bean = this.getEntity(hql,ExperienceDetail.class, detailId, userId);
            Long expId = bean.getExperience().getExpId();
            deleteExperienceDetail(bean);

            if (updateStat) {
                //更新总的人生经历
                updateExperienceCost(expId);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "删除人生经历消费异常", e);
        }
    }

    /**
     * 独立事务，因为需要更新数据
     *
     * @param bean
     */
    public void saveOrUpdateExperience(Object bean, boolean update) {
        try {
            if (!update) {
                this.saveEntity(bean);
            } else {
                this.updateEntity(bean);
            }
            //this.flushSession();
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "独立事务更新人生经历相关数据异常", e);
        }
    }

    /**
     * 删除人生经历
     *
     * @return
     */
    public void deleteExperience(Long expId) {
        try {
            //删除消费
            String sql = "delete from ExperienceConsume where detail.detailId in (select detailId from ExperienceDetail where experience.expId=?1 )";
            this.updateEntities(sql, expId);

            //删除人生经历详情
            String sql2 = "delete from ExperienceDetail where experience.expId=?1 ";
            this.updateEntities(sql2, expId);

            //删除人生经历
            String sql3 = "delete from Experience where expId=?2 ";
            this.updateEntities(sql3, expId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "删除人生经历异常", e);
        }
    }

    /**
     * 修正人生经历
     *
     * @return
     */
    public void reviseExperience(ExperienceReviseForm revise) {
        try {
            if (revise.getReviseCost() != null && revise.getReviseCost()) {
                this.updateExperienceCost(revise.getExpId());
            }
            if (revise.getReviseDays() != null && revise.getReviseDays()) {
                String sql = "update experience set days =(select count(0) from (select distinct occur_date from experience_detail where exp_id =?1 ) as aa ) where exp_id=?2 ";
                this.execSqlUpdate(sql, revise.getExpId(), revise.getExpId());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "修正人生经历异常", e);
        }
    }

    /**
     * 获取人生经历列表
     *
     * @param year
     * @return
     */
    public List<Experience> selectExperienceList(int year, Long userId) {
        try {
            Date beginDate = DateUtil.getDate(year + "-01-01", DateUtil.FormatDay1);
            Date endDate = DateUtil.getDate(year + "-12-31", DateUtil.FormatDay1);

            String hql = "from Experience where userId=?1 and (startDate<=?2 and endDate>=?3 ) ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Experience.class, userId, endDate, beginDate);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取人生经历列表异常", e);
        }
    }

    /**
     * 获取人生档案
     *
     * @param userId
     * @param bussType
     * @param sourceId
     * @return
     */
    public Archive getArchive(Long userId, BussType bussType, Long sourceId) {
        try {
            String hql = "from Archive where userId=?1 and bussType=?2 and sourceId=?3";
            return this.getEntity(hql,Archive.class, userId, bussType, sourceId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取人生档案异常", e);
        }
    }

    /**
     * 统计城市地理位置
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    public List<CityLocationDTO> statCityLocation(Long userId, Date startDate, Date endDate) {
        try {
            List args = new ArrayList();
            args.add(userId);
            args.add(startDate);
            args.add(endDate);
            args.add(userId);
            args.add(startDate);
            args.add(endDate);
            String statSql = """
                    select city as city,max(location) as location from
                    (
                    select start_city as city,sc_location as location from experience_detail where user_id=?1 and occur_date>=?2 and occur_date<=?3
                    union
                    select arrive_city as city,ac_location as location from experience_detail where user_id=?4 and occur_date>=?5 and occur_date<=?6
                    ) as res  group by city
                    """;
            return this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE,CityLocationDTO.class, args.toArray());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "统计城市地理位置异常", e);
        }
    }

    /**
     * 获取标签列表
     *
     * @param sf
     * @return
     */
    public List<NameCountDTO> statTags(ExperienceWouldCloudStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select name,count(0) as counts from
                    (
                    select (substring_index(substring_index(a.col,',',b.help_topic_id+1),',',-1)) as name
                    from
                    (select tags as col from experience
                    {query_para}
                     ) as a
                     join mysql.help_topic as b
                     on b.help_topic_id < (char_length(a.col) - char_length(replace(a.col,',',''))+1)
                     ) as res group by name
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<NameCountDTO> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE,NameCountDTO.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取标签列表异常", e);
        }
    }

    /**
     * 统计城市列表
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    public List<String> statCityList(Long userId, Date startDate, Date endDate) {
        try {
            List args = new ArrayList();
            args.add(userId);
            StringBuffer sb = new StringBuffer();
            int index=0;
            sb.append("select start_city,arrive_city from experience_detail where user_id=?"+(index++));
            if(startDate!=null){
                sb.append(" and occur_date>=?"+(index++));
                args.add(startDate);
            }
            if(endDate!=null){
                sb.append(" and occur_date<=?"+(index++));
                args.add(endDate);
            }
            List<Object[]> list = this.getEntityListSI(sb.toString(),NO_PAGE,NO_PAGE_SIZE,Object[].class,args.toArray());
            List<String> res = new ArrayList<>();
            for(Object[] oo:list){
                res.add(oo[0].toString());
                res.add(oo[1].toString());
            }
            return res;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "统计城市列表异常", e);
        }
    }
}
