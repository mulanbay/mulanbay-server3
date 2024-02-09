package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.dto.health.*;
import cn.mulanbay.pms.persistent.enums.BodyAbnormalGroupType;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.GoodsConsumeType;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.web.bean.req.health.drug.TreatDrugDetailSH;
import cn.mulanbay.pms.web.bean.req.health.drug.TreatDrugDetailStatSH;
import cn.mulanbay.pms.web.bean.req.health.drug.TreatDrugGroupSH;
import cn.mulanbay.pms.web.bean.req.health.operation.TreatOperationGroupSH;
import cn.mulanbay.pms.web.bean.req.health.operation.TreatOperationStatSH;
import cn.mulanbay.pms.web.bean.req.health.test.TreatTestGroupSH;
import cn.mulanbay.pms.web.bean.req.health.treat.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 看病
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
@Service
@Transactional
public class TreatService extends BaseHibernateDao {

    /**
     * 获取看病手术的分类列表，统计聚合
     *
     * @return
     */
    public List<String> getOperationCateList(TreatOperationGroupSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String hql= """
                    select distinct {group_field} from TreatOperation
                     {query_para}
                     order by {group_field}
                    """;
            hql = hql.replace("{query_para}",pr.getParameterString())
                     .replace("{group_field}",sf.getGroupField());
            return this.getEntityListHI(hql, NO_PAGE,NO_PAGE_SIZE,String.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取看病手术的分类列表异常", e);
        }
    }

    /**
     * 获取检测的分类列表，统计聚合
     *
     * @return
     */
    public List<String> getTestCateList(TreatTestGroupSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String hql= """
                    select distinct name from TreatTest
                     {query_para}
                     order by name
                    """;
            hql = hql.replace("{query_para}",pr.getParameterString());
            return this.getEntityListHI(hql, NO_PAGE,NO_PAGE_SIZE,String.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取检测的分类列表异常", e);
        }
    }


    /**
     * 获取看病用药的分类列表，统计聚合
     *
     * @return
     */
    public List<String> getDrugCateList(TreatDrugGroupSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String hql= """
                    select distinct {group_field} from TreatDrug
                     {query_para}
                     order by {group_field}
                    """;
            hql = hql.replace("{query_para}",pr.getParameterString())
                    .replace("{group_field}",sf.getGroupField());
            return this.getEntityListHI(hql, NO_PAGE,NO_PAGE_SIZE,String.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取看病用药的分类列表异常", e);
        }
    }


    /**
     * 获取看病分类列表，统计聚合
     *
     * @return
     */
    public List<String> getTreatCateList(TreatGroupSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String hql= """
                    select distinct {group_field} from Treat
                     {query_para}
                     and {group_field} is not null
                     order by {group_field}
                    """;
            hql = hql.replace("{query_para}",pr.getParameterString())
                    .replace("{group_field}",sf.getGroupField());
            return this.getEntityListHI(hql, NO_PAGE,NO_PAGE_SIZE,String.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取看病分类列表异常", e);
        }
    }

    /**
     * 获取看病记录总的统计
     *
     * @param sf
     * @return
     */
    public TreatSummaryStat getTreatStat(TreatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String hql = """
                    select count(0) as totalCount,
                    sum(regFee) as totalRegFee,
                    sum(drugFee) as totalDrugFee,
                    sum(operationFee) as totalOperationFee,
                    sum(totalFee) as totalTotalFee,
                    sum(miFee) as totalMiFee,
                    sum(pdFee) as totalPdFee,
                    max(treatTime) as maxTreatTime
                    from Treat
                    """;
            hql+=" "+pr.getParameterString();
            List<TreatSummaryStat> list = this.getEntityListHI(hql, NO_PAGE,NO_PAGE_SIZE, TreatSummaryStat.class, pr.getParameterValue());
            return list.get(0);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取看病记录统计异常", e);
        }
    }

    /**
     * 获取看病记录分析
     *
     * @param sf
     * @return
     */
    public List<TreatAnalyseStat> getTreatAnalyseStat(TreatAnalyseStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String sql= """
                    select {group_field} as name,count(0) as totalCount,sum({fee_field}) as totalFee from Treat
                     {query_para}
                     group by {group_field} order by totalCount desc
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString())
                     .replace("{group_field}",sf.getGroupField())
                     .replace("{fee_field}",sf.getFeeField());

            List<TreatAnalyseStat> list = this.getEntityListSI(sql, NO_PAGE,NO_PAGE_SIZE, TreatAnalyseStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取看病记录分析异常", e);
        }
    }

    /**
     * 获取看病记录手术统计
     *
     * @param sf
     * @return
     */
    public List<TreatOperationStat> getOperationStat(TreatOperationStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(false);
            String sql= """
                    select name,min(date) as minDate,max(date) as maxDate,count(0) as totalCount,sum(total_fee) as totalFee from (
                    select operation.{field} as name,operation.treat_date as date,treat.total_fee from treat_operation operation,treat treat
                    where operation.treat_id= treat.treat_id
                     {query_para}
                    ) as aa
                    group by name order by totalCount desc
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString())
                     .replace("{field}",sf.getGroupField());
            List<TreatOperationStat> list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE, TreatOperationStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取看病记录手术统计异常", e);
        }
    }

    /**
     * 删除看病记录
     *
     * @param treatId
     */
    public void deleteTreat(Long treatId) {
        try {
            // step 1 删除看病记录中的手术记录
            String hql = "delete from TreatOperation where treat.treatId=?1 ";
            this.updateEntities(hql, treatId);

            // step 2 删除看病记录中的用药记录
            hql = "delete from TreatDrug where treat.treatId=?1 ";
            this.updateEntities(hql, treatId);

            // step 3 删除看病记录
            hql = "delete from Treat where treatId=?1 ";
            this.updateEntities(hql, treatId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除看病记录异常", e);
        }
    }

    /**
     * 删除药品
     *
     * @param drugId
     */
    public void deleteDrug(Long drugId) {
        try {
            // step 1 删除用药明细
            String hql = "delete from TreatDrugDetail where drug.drugId=?1 ";
            this.updateEntities(hql, drugId);

            // step 2 删除用药记录
            hql = "delete from TreatDrug where drugId=?1 ";
            this.updateEntities(hql, drugId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除药品异常", e);
        }
    }

    /**
     * 获取最新的看病记录
     *
     * @param name
     * @param groupField
     * @return
     */
    public Treat getLatestTreat(String name, BodyAbnormalGroupType groupField, Long userId) {
        try {
            String fieldName = "organ";
            if (groupField == BodyAbnormalGroupType.DISEASE) {
                fieldName = "disease";
            }
            String hql= """
                    from Treat where {field_name}=?1 and userId=?2
                    and treatTime=(select max(treatTime) from Treat where {field_name}=?3 and userId=?4)
                    """;
            hql = hql.replace("{field_name}",fieldName);
            return this.getEntity(hql,Treat.class, name, userId, name, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取最新的看病记录异常", e);
        }
    }

    /**
     * 获取最新的看病记录
     *
     * @param sf
     * @return
     */
    public TreatAnalyseDetailStat getAnalyseDetailStat(TreatAnalyseDetailStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String paraString = pr.getParameterString();
            List args = pr.getParameterValueList();
            String statSql = """
                    select count(0) as totalCount,sum(totalFee) as totalFee ,min(treatTime) as minTreatTime,max(treatTime) as maxTreatTime
                    from treat
                    """;
            statSql+=paraString;
            TreatAnalyseDetailStat stat = this.getEntity(statSql, TreatAnalyseDetailStat.class, args.toArray());
            //获取详情
            int lastIndex = pr.getNextIndex();
            if (stat.getMinTreatTime() != null) {
                String hql = "from Treat " + paraString + " and treatTime = ?" + lastIndex;
                List newArgs = new ArrayList();
                newArgs.addAll(args);
                newArgs.add(stat.getMinTreatTime());
                Treat minTreat = this.getEntity(hql,Treat.class, newArgs.toArray());
                stat.setMinTreat(minTreat);
            }
            if (stat.getMaxTreatTime() != null) {
                String hql = "from Treat " + paraString + " and treatTime = ?" + lastIndex;
                List newArgs = new ArrayList();
                newArgs.addAll(args);
                newArgs.add(stat.getMaxTreatTime());
                Treat maxTreat = this.getEntity(hql,Treat.class, newArgs.toArray());
                stat.setMaxTreat(maxTreat);
            }
            return stat;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取最新的看病记录异常", e);
        }
    }

    /**
     * 保存用药详情
     *
     * @param list
     * @return
     */
    public void saveDrugDetailList(List<TreatDrugDetail> list) {
        for (TreatDrugDetail bean : list) {
            this.saveOrUpdateTreatDrugDetail(bean);
        }
    }

    /**
     * 保存或者更新用药详情
     *
     * @param bean
     * @return
     */
    public void saveOrUpdateTreatDrugDetail(TreatDrugDetail bean) {
        try {
            if (bean.getDetailId() == null) {
                this.saveEntity(bean);
            } else {
                this.updateEntity(bean);
            }
            Date compareDate = DateUtil.getDate(bean.getOccurTime(), DateUtil.FormatDay1);
            //更新药品记录的数据
            String hql = "update TreatDrug set beginDate=?1 where drugId=?2 and (beginDate is null or beginDate>?3 ) ";
            this.updateEntities(hql, compareDate, bean.getDrug().getDrugId(), compareDate);
            String hql2 = "update TreatDrug set endDate=?1 where drugId=?2 and (endDate is null or endDate<?3 ) ";
            this.updateEntities(hql2, compareDate, bean.getDrug().getDrugId(), compareDate);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "保存或者更新用药详情异常", e);
        }
    }

    /**
     * 统计看病记录
     *
     * @param sf
     * @return
     */
    public List<TreatDateStat> statDateTreatRecord(TreatDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            String feeField = sf.getFeeField();
            String sql= """
                    select indexValue,count(0) as totalCount,sum(weight) as totalWeight,sum({fee_field}) as totalFee
                    from ( select {date_group_field} as indexValue,{fee_field} from treat
                    {query_para}
                    ) tt group by indexValue order by indexValue
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString())
                     .replace("{fee_field}",feeField)
                     .replace("{date_group_field}",MysqlUtil.dateTypeMethod("treat_time", dateGroupType));
            List<TreatDateStat> list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE, TreatDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "统计看病记录异常", e);
        }
    }

    /**
     * 获取需要提醒的药品
     * 条件为：1设置了提醒 2还处在用药期间
     *
     * @param date
     * @return
     */
    public List<TreatDrug> getNeedRemindDrug(Date date) {
        try {

            String hql = "from TreatDrug where remind=1 and beginDate<=?1 and endDate>=?2 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,TreatDrug.class, date, date);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取需要提醒的药品异常", e);
        }
    }

    /**
     * 获取需要提醒的手术
     * 条件为：包含复查时间
     *
     * @param beginDate
     * @return
     */
    public List<TreatOperation> getNeedRemindOperation(Date beginDate, Date endDate, Long userId) {
        try {

            String hql = "from TreatOperation where reviewDate is not null and reviewDate>=?1 and reviewDate<=?2 ";
            if (userId != null) {
                hql += " and userId=" + userId;
            }
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,TreatOperation.class, beginDate, endDate);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取需要提醒的药品异常", e);
        }
    }

    /**
     * 需要在日历显示的药品
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    public List<TreatDrug> getDrugForCalendar(Long userId, String drugName, Date startDate, Date endDate) {
        try {
            String hql = "from TreatDrug where userId=?1 and beginDate<=?2 and endDate>=?3 ";
            if (StringUtil.isNotEmpty(drugName)) {
                hql += " and name like '%" + drugName + "%'";
            }
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,TreatDrug.class, userId, endDate, startDate);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取需要提醒的药品异常", e);
        }
    }


    /**
     * 获取手术
     * 条件为：包含复查时间
     *
     * @param minDate
     * @return
     */
    public TreatOperation getOperation(Date minDate, Long userId, String name) {
        try {

            String hql = "from TreatOperation where userId =?1 and operationName=?2 and treatDate>=?3 ";
            return this.getEntity(hql,TreatOperation.class, userId, name, minDate);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取手术异常", e);
        }
    }

    /**
     * 获取用药次数
     *
     * @param date
     * @return
     */
    public long getDrugDetailCount(Long drugId, Date date) {
        try {
            Date startTime = DateUtil.fromMiddleNight(date);
            Date endTime = DateUtil.tillMiddleNight(date);
            String hql = "select count(0) as n from TreatDrugDetail where drug.drugId=?1 and occurTime>=?2 and occurTime<=?3 ";
            return this.getCount(hql, drugId, startTime, endTime);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用药次数异常", e);
        }
    }

    /**
     * 获取看病记录关键字
     *
     * @return
     */
    public List<String> getTagsList(TreatTagsSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(false);
            String sql = "select distinct tags from treat where tags is not null ";
            sql += pr.getParameterString();
            sql += " order by tags desc";
            return this.getEntityListSI(sql, NO_PAGE,NO_PAGE_SIZE,String.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取看病记录关键字异常", e);
        }
    }

    /**
     * 获取用药统计
     *
     * @param sf
     * @return
     */
    public List<TreatDrugDetailStat> getDrugDetailStat(TreatDrugDetailStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(false);
            String sql= """
                    select td.drug_id as drugId ,td.drug_name as drugName,td.treat_time as treatTime,
                    min(tdd.occur_time) as minTime,max(tdd.occur_time) as maxTime,count(0) as totalCount,datediff(max(tdd.occur_time) ,min(tdd.occur_time)) as days
                    from treat_drug_detail tdd,treat_drug td
                    {query_para}
                    group by td.drug_id ,td.drug_name order by td.drug_name
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString());
            if (sf.isMergeSameName()) {
                StringBuffer nn = new StringBuffer();
                nn.append("select drugName,min(minTime) as minTime,max(maxTime) as maxTime,sum(totalCount) as totalCount,sum(days) as days ");
                nn.append("from ( ");
                nn.append(sql);
                nn.append(") as res group by drugName ");
                nn.append("order by maxTime desc ");
                sql = nn.toString();
            }
            List<TreatDrugDetailStat> list = this.getEntityListSI(sql, pr.getPage(), pr.getPageSize(), TreatDrugDetailStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用药统计异常", e);
        }
    }

    /**
     * 获取最近一次的检查
     *
     * @param name
     * @param userId
     * @return
     */
    public TreatTest getLastTest(String name, Long userId) {
        try {
            String hql = "from TreatTest where name=?1 and userId=?2 order by testDate desc ";
            return this.getEntity(hql,TreatTest.class, name, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取最近一次的检查异常", e);
        }
    }

    /**
     * 获取最近一次的手术
     *
     * @param operationName
     * @param userId
     * @return
     */
    public TreatOperation getLastOperation(String operationName, Long userId) {
        try {

            String hql = "from TreatOperation where operationName=?1 and userId=?2 order by treatDate desc ";
            return this.getEntity(hql,TreatOperation.class, operationName, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取最近一次的手术异常", e);
        }
    }

    /**
     * 获取最近一次的药品
     *
     * @param drugName
     * @param userId
     * @return
     */
    public TreatDrug getLastDrug(String drugName, Long userId) {
        try {

            String hql = "from TreatDrug where drugName=?1 and userId=?2 order by treatDate desc ";
            return this.getEntity(hql,TreatDrug.class, drugName, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取最近一次的药品异常", e);
        }
    }


    /**
     * 获取疾病概况统计
     *
     * @param sf
     * @return
     */
    public List<TreatFullStat> getFullStat(TreatFullStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(false);
            String sql= """
                    SELECT tags,count(0) as totalCount,min(treat_time) as minTreatTime,max(treat_time)  as maxTreatTime,
                    sum(reg_fee) as regFee,sum(drug_fee) as drugFee,sum(operation_fee) as operationFee,
                    sum(mi_fee) as miFee,sum(pd_fee) as pdFee,sum(total_fee) as totalFee
                    FROM treat where tags is not null
                    {query_para}
                    group by tags order by maxTreatTime desc
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString());
            List<TreatFullStat> list = this.getEntityListSI(sql,sf.getPage(),sf.getPageSize(), TreatFullStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取看病总的分析异常", e);
        }
    }

    /**
     * 疾病概况统计
     *
     * @param sf
     * @return
     */
    public long getMaxRowOfFullStat(TreatFullStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(false);
            StringBuffer sb = new StringBuffer();
            sb.append("select count(0) from (");
            sb.append("select tags,count(0) n FROM treat where tags is not null ");
            sb.append(pr.getParameterString());
            sb.append(" group by tags ) as tt");
            long n = this.getCountSQL(sb.toString(), pr.getParameterValue());
            return n;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "疾病概况统计异常", e);
        }
    }

    /**
     * 获取用药明细列表日历统计
     *
     * @param sf
     * @return
     */
    public List<TreatDrugDetail> getDrugDetailCalendarStatList(TreatDrugDetailSH sf, Long drugId, boolean mergeSameName) {
        try {
            PageRequest pr = sf.buildQuery();
            StringBuffer sb = new StringBuffer();
            sb.append("from TreatDrugDetail ");
            sb.append(pr.getParameterString());
            List paras = pr.getParameterValueList();
            if(mergeSameName){
                TreatDrug tt = this.getEntityById(TreatDrug.class,drugId);
                int nextIndex = pr.getNextIndex();
                sb.append(" and drug.drugId in (select drugId from TreatDrug where drugName=?"+(nextIndex++)+" and userId=?"+(nextIndex++)+")");
                paras.add(tt.getDrugName());
                paras.add(sf.getUserId());
            }else{
                sb.append(" and drug.drugId=?"+pr.getNextIndex());
                paras.add(drugId);
            }
            List<TreatDrugDetail> list = this.getEntityListHI(sb.toString(),NO_PAGE,NO_PAGE_SIZE,TreatDrugDetail.class, paras.toArray());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用药明细列表日历统计异常", e);
        }
    }

    /**
     * 获取用药明细的时间列表
     * @param drugId
     * @param startDate
     * @param endDate
     * @param userId
     * @param mergeSameName
     * @return
     */
    public List<Date> getDrugDetailDateList(Long drugId,Date startDate,Date endDate,Long userId,boolean mergeSameName) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select occurTime from TreatDrugDetail ");
            sb.append("where userId=?1 and occurTime>=?2 and occurTime<=?3 ");
            if(!mergeSameName){
                sb.append("and drug.drugId=?4 ");
                sb.append(" order by occurTime");
                List<Date> list = this.getEntityListHI(sb.toString(),NO_PAGE,NO_PAGE_SIZE,Date.class, userId,startDate,endDate,drugId);
                return list;
            }else{
                TreatDrug td = this.getEntityById(TreatDrug.class,drugId);
                sb.append("and drug.drugId in (select drugId from TreatDrug where drugName=?4) ");
                List<Date> list = this.getEntityListHI(sb.toString(),NO_PAGE,NO_PAGE_SIZE,Date.class, userId,startDate,endDate,td.getDrugName());
                return list;
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用药明细的时间列表异常", e);
        }
    }

    /**
     * 获取用药中的药品列表
     * @param date 比较日期
     * @param userId
     * @return
     */
    public List<TreatDrug> getActiveDrugList(Date date,Long userId) {
        try {
            String hql = "from TreatDrug where userId=?1 and beginDate<=?2 and endDate>=?3 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,TreatDrug.class,userId,date,date);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用药中的药品列表异常", e);
        }
    }

    /**
     * 获取用药详情列表
     * @param date 比较日期
     * @param userId
     * @return
     */
    public List<TreatDrugDetail> getDrugDetailList(Date date,Long userId,Long treatDrugId) {
        try {
            Date end = DateUtil.tillMiddleNight(date);
            String hql = "from TreatDrugDetail where userId=?1 and occurTime>=?2 and occurTime<=?3 and drug.drugId=?4 order by occurTime";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,TreatDrugDetail.class,userId,date,end,treatDrugId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用药详情列表异常", e);
        }
    }

    /**
     * 获取看病记录的总体统计
     *
     * @param sf
     * @return
     */
    public List<TreatOverallStat> getOverallStat(TreatOverallStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(true);
            DateGroupType dateGroupType = sf.getDateGroupType();
            String feeField = sf.getFeeField();
            String sql= """
                    select indexValue,name,count(0) as totalCount,sum({fee_field}) as totalFee
                    from (select {date_group_field} as indexValue,{group_field} as name,{fee_field}
                    from treat
                    {query_para}
                    ) as res group by name,indexValue order by indexValue
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString())
                     .replace("{fee_field}",feeField)
                     .replace("{date_group_field}",MysqlUtil.dateTypeMethod("treat_time", dateGroupType));
            List<TreatOverallStat> list = this.getEntityListSI(sql, pr.getPage(), pr.getPageSize(), TreatOverallStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取看病记录的总体统计异常", e);
        }
    }

    /**
     * 获取看病列表
     * @param tags
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    public List<TreatUnionDTO> getTreatList(String tags,Long userId,Date startDate,Date endDate) {
        try {
            int index =0;
            List args = new ArrayList();
            String sql= """
                    select tr.hospital,tr.department,tr.organ,tr.disease,tr.confirm_disease as confirmDisease,td.drug_name as drugName,op.operation_name as operationName
                    from treat tr
                    left join treat_drug td on tr.treat_id = td.treat_id
                    left join treat_operation op on tr.treat_id = op.treat_id
                    """;
            sql+=" where tr.user_id= ?"+(index++);
            args.add(userId);
            if(StringUtil.isNotEmpty(tags)){
                sql+=" and tr.tags like ?"+(index++);
                args.add("%"+tags+"%");
            }
            if(startDate!=null){
                sql+=" and tr.treat_date >= ?"+(index++);
                args.add(startDate);
            }
            if(endDate!=null){
                sql+=" and tr.treat_date <= ?"+(index++);
                args.add(endDate);
            }
            List<TreatUnionDTO> list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE, TreatUnionDTO.class, args.toArray());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取看病列表异常", e);
        }
    }

    /**
     * 保存记录
     * @param treat
     * @param syncToConsume
     * @param us
     */
    public void saveTreat(Treat treat,boolean syncToConsume,UserSet us) {
        try {
            if(treat.getTreatId()==null){
                this.saveEntity(treat);
            }else{
                this.updateEntity(treat);
            }
            if(syncToConsume){
                //查询收入
                String hql = "from ConsumeRefer where referId=?1 and type=?2";
                ConsumeRefer refer = this.getEntity(hql,ConsumeRefer.class,treat.getTreatId(), BussType.TREAT);
                if (refer == null) {
                    addNewConsume(treat,us);
                } else {
                    Consume consume = this.getEntityById(Consume.class,refer.getConsumeId());
                    if (!NumberUtil.priceEquals(consume.getSoldPrice(), treat.getPdFee())) {
                        //价格有改变更新
                        consume.setPrice(treat.getPdFee());
                        consume.setTotalPrice(treat.getPdFee());
                        this.updateEntity(consume);
                    }
                }
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR, "保存记录异常", e);
        }
    }

    /**
     * 保持消费
     *
     * @param treat
     * @param us
     */
    private void addNewConsume(Treat treat,UserSet us) {
        try {
            if(us.getTreatGoodsTypeId()==null){
                return;
            }
            Consume consume = new Consume();
            consume.setGoodsName("看病:"+treat.getHospital()+","+treat.getDisease());
            GoodsType goodsType = this.getEntityById(GoodsType.class,us.getTreatGoodsTypeId());
            consume.setGoodsType(goodsType);
            ConsumeSource source = this.getEntityById(ConsumeSource.class,us.getTreatSourceId());
            consume.setSource(source);
            consume.setUserId(treat.getUserId());
            consume.setConsumeType(GoodsConsumeType.TREAT);
            consume.setShopName(treat.getHospital());
            consume.setTags(treat.getTags());
            consume.setPrice(treat.getPdFee());
            consume.setShipment(new BigDecimal(0));
            consume.setTotalPrice(treat.getPdFee());
            consume.setPayment(us.getPayment());
            consume.setBuyTime(treat.getTreatTime());
            consume.setConsumeTime(treat.getTreatTime());
            consume.setSecondhand(false);
            consume.setAmount(1);
            this.saveEntity(consume);
            //保存关联记录
            ConsumeRefer refer = new ConsumeRefer();
            refer.setConsumeId(consume.getConsumeId());
            refer.setReferId(treat.getTreatId());
            refer.setType(BussType.INCOME);
            this.saveEntity(refer);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保持消费异常", e);
        }
    }


    /**
     * 获取时间列表
     * @param sf
     * @return
     */
    public List<Date> getTreatDateList(TreatDateStatSH sf) {
        try {
            String sql = """
                    select treat_time from treat
                    {query_para}
                     order by treat_time
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
