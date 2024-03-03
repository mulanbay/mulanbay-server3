package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.dto.report.*;
import cn.mulanbay.pms.persistent.enums.*;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.pms.util.bean.PeriodDateBean;
import cn.mulanbay.pms.web.bean.req.report.plan.PlanReportAvgStatSH;
import cn.mulanbay.pms.web.bean.req.report.plan.PlanReportDataCleanForm;
import cn.mulanbay.pms.web.bean.req.report.plan.PlanReportPlanCommendForm;
import cn.mulanbay.pms.web.bean.req.report.plan.PlanReportResultGroupStatSH;
import cn.mulanbay.schedule.enums.TriggerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PlanService extends BaseReportService {

    private static final Logger logger = LoggerFactory.getLogger(PlanService.class);

    /**
     * 获取最大排序号
     *
     * @param bussType
     */
    public Short getTemplateMaxOrderIndex(BussType bussType) {
        try {
            String hql = "select max(orderIndex) from PlanTemplate where bussType=?1 ";
            Object o =  this.getEntity(hql,Object.class, bussType);
            return o==null? null : Short.parseShort(o.toString());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取最大排序号异常", e);
        }
    }

    /**
     * 删除计划报告数据
     *
     * @param sf
     * @return
     */
    public void deletePlanReportData(PlanReportDataCleanForm sf) {
        try {
            String hql = "delete from PlanReport ";
            PageRequest pr = sf.buildQuery();
            hql += pr.getParameterString();
            if (sf.getCleanType() == PlanReportDataCleanType.BOTH_ZERO) {
                hql += " and reportCountValue=0 and reportValue=0 ";
            } else if (sf.getCleanType() == PlanReportDataCleanType.ONCE_ZERO) {
                hql += " and (reportCountValue=0 or reportValue=0) ";
            }
            this.updateEntities(hql, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "删除计划报告数据异常", e);
        }
    }

    /**
     * 删除计划模版
     *
     * @param templateId
     * @return
     */
    public void deletePlanTemplate(Long templateId) {
        try {
            //删除配置绑定
            String sql = "delete from stat_bind_config where type=?1 and fid=?2";
            this.execSqlUpdate(sql, StatBussType.PLAN,templateId);

            //删除计划统计时间线
            String sql3 = "delete from plan_report_timeline where plan_id in (select plan_id from user_plan where template_id=?1) ";
            this.execSqlUpdate(sql3, templateId);

            //删除用户计划
            String sql2 = "delete from user_plan where template_id=?1 ";
            this.execSqlUpdate(sql2, templateId);

            //删除模版
            String sql4 = "delete from plan_template where template_id=?1 ";
            this.execSqlUpdate(sql4, templateId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除计划模版异常", e);
        }
    }

    /**
     * 计划推荐
     *
     * @param sf
     * @return
     */
    public PlanReportPlanCommendDTO planCommend(PlanReportPlanCommendForm sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select AVG(report_count_value) as reportCountValue,AVG(report_value) as reportValue
                    from plan_report
                    {query_para}
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<PlanReportPlanCommendDTO> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE, PlanReportPlanCommendDTO.class, pr.getParameterValue());
            return list.get(0);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "计划推荐异常", e);
        }
    }

    /**
     * 统计
     *
     * @param userPlan
     * @param bussKey
     */
    public void manualStat(UserPlan userPlan, String bussKey, ManualStatType statType,PlanValueDTO upc) {
        try {
            String sql = "select count(0) from plan_report where plan_id=?1 and user_id=?2 and buss_key=?3";
            if (statType == ManualStatType.STAT_MISS) {
                //检查数据库中是否存在
                Long count = this.getEntitySQL(sql,Long.class, userPlan.getPlanId(), userPlan.getUserId(), bussKey);
                if (count > 0) {
                    //说明数据库中已经有数据，跳过
                    logger.debug(userPlan.getTitle() + "在" + bussKey + "已经有数据，跳过.");
                } else {
                    statAndSavePlanReport(userPlan, bussKey, upc);
                }
            } else {
                statAndSavePlanReport(userPlan, bussKey, upc);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "手动统计计划报告异常", e);
        }
    }


    /**
     * 重新统计计划报告
     *
     * @param reportId
     */
    public void reStatPlanReport(Long reportId, PlanValueCompareType type) {
        try {
            PlanReport planReport = this.getEntityById(PlanReport.class, reportId);
            PlanValueDTO upc = this.getPlanValue(type,planReport.getBussKey(),planReport.getPlan().getPlanId());
            statAndSavePlanReport(planReport.getPlan(), planReport.getBussKey(), upc);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "重新统计计划报告异常", e);
        }
    }

    /**
     * 统计保存计划报告
     *
     * @param userPlan
     * @param bussKey
     * @param upc
     */
    private void statAndSavePlanReport(UserPlan userPlan, String bussKey, PlanValueDTO upc) {
        try {
            Long userId = userPlan.getUserId();
            // 删除数据库里数据
            String deleteHql = "delete from PlanReport where plan.planId=?1 and userId=?2 and bussKey=?3";
            this.updateEntities(deleteHql, userPlan.getPlanId(), userId, bussKey);
            PlanReport pr = this.statPlanReport(userPlan, bussKey, upc);
            if (pr != null) {
                this.saveEntity(pr);
            } else {
                logger.debug("计划[" + userPlan.getTitle() + "],用户[" + userId + "]在[" + bussKey + "]没有数据.");
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "更新或者保存计划报告异常", e);
        }
    }

    /**
     * 默认统计
     *
     * @param userPlan
     * @return
     */
    public PlanReport statPlanReport(UserPlan userPlan){
        PlanType planType = userPlan.getPlanType();
        String bussKey = BussUtil.getBussKey(planType.getPeriodType(),new Date());
        PlanValueDTO upc = this.getPlanValue(PlanValueCompareType.LATEST,bussKey,userPlan.getPlanId());
        return this.statPlanReport(userPlan,bussKey,upc);
    }

    /**
     * 默认统计
     *
     * @param userPlan
     * @param bussKey
     * @return
     */
    public PlanReport statPlanReport(UserPlan userPlan, String bussKey){
        PlanValueDTO upc = this.getPlanValue(PlanValueCompareType.LATEST,bussKey,userPlan.getPlanId());
        return this.statPlanReport(userPlan,bussKey,upc);
    }

    /**
     *
     * @param userPlan
     * @param bussKey
     * @param upc
     * @return
     */
    public PlanReport statPlanReport(UserPlan userPlan, String bussKey, PlanValueDTO upc){
        PeriodDateBean pdb = BussUtil.calPeriod(bussKey,userPlan.getPlanType().getPeriodType());
        return this.statPlanReport(userPlan,bussKey,pdb.getStartDate(),pdb.getEndDate(),upc);
    }

    /**
     * 统计出计划报表
     * 指定时间段
     * @param userPlan
     * @param start
     * @param end
     * @param upc
     * @return
     */
    public PlanReport statPlanReport(UserPlan userPlan,String bussKey, Date start,Date end, PlanValueDTO upc) {
        try {
            PlanTemplate template = userPlan.getTemplate();
            StatSQLDTO sqlDTO = this.assembleSQL(userPlan, start,end);
            List<Object[]> rr = null;
            if (template.getSqlType() == SqlType.HQL) {
                rr = this.getEntityListHI(sqlDTO.getSqlContent(),NO_PAGE,NO_PAGE_SIZE,Object[].class,sqlDTO.getArgArray());
            } else {
                rr = this.getEntityListSI(sqlDTO.getSqlContent(),NO_PAGE,NO_PAGE_SIZE,Object[].class,sqlDTO.getArgArray());
            }
            if (!rr.isEmpty()) {
                Object[] oo = rr.get(0);
                if (oo[0] == null && oo[1] == null) {
                    //可能出现全部没有数据
                    return this.createEmptyPlanReport(userPlan, bussKey, upc);
                }
                PlanReport pr = new PlanReport();
                pr.setUserId(userPlan.getUserId());
                pr.setBussKey(bussKey);
                pr.setBussDay(BussUtil.getBussDay(userPlan.getPlanType().getPeriodType(),bussKey));
                pr.setCreatedTime(new Date());
                pr.setReportName(userPlan.getTitle() + "(" + bussKey + ")");
                pr.setPlan(userPlan);
                pr.setReportCountValue(Long.valueOf(oo[0].toString()));
                if (upc != null) {
                    pr.setPlanCountValue(upc.getPlanCountValue());
                    pr.setPlanValue(upc.getPlanValue());
                    pr.setCompareBussKey(upc.getCompareBussKey());
                }else{
                    pr.setPlanCountValue(0L);
                    pr.setPlanValue(0L);
                }
                if (oo.length > 1) {
                    if (oo[1] == null) {
                        pr.setReportValue(0L);
                    } else {
                        pr.setReportValue(Long.valueOf(oo[1].toString()));
                    }
                }
                if (upc != null) {
                    CompareType compareType = userPlan.getCompareType();
                    //设置统计结果
                    if (compareType == CompareType.MORE) {
                        if (pr.getReportCountValue().longValue() >= pr.getPlanCountValue().longValue()) {
                            pr.setCountValueResult(PlanStatResult.ACHIEVED);
                        } else {
                            pr.setCountValueResult(PlanStatResult.UN_ACHIEVED);
                        }
                        if (pr.getPlanValue() == 0) {
                            pr.setValueResult(PlanStatResult.SKIP);
                        } else if (pr.getReportValue().longValue() >= pr.getPlanValue().longValue()) {
                            pr.setValueResult(PlanStatResult.ACHIEVED);
                        } else {
                            pr.setValueResult(PlanStatResult.UN_ACHIEVED);
                        }
                    } else {
                        if (pr.getReportCountValue().longValue() <= pr.getPlanCountValue().longValue()) {
                            pr.setCountValueResult(PlanStatResult.ACHIEVED);
                        } else {
                            pr.setCountValueResult(PlanStatResult.UN_ACHIEVED);
                        }
                        if (pr.getPlanValue() == 0) {
                            pr.setValueResult(PlanStatResult.SKIP);
                        } else if (pr.getReportValue().longValue() <= pr.getPlanValue().longValue()) {
                            pr.setValueResult(PlanStatResult.ACHIEVED);
                        } else {
                            pr.setValueResult(PlanStatResult.UN_ACHIEVED);
                        }
                    }
                }
                return pr;
            }
            return this.createEmptyPlanReport(userPlan, bussKey, upc);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "统计出计划报表异常", e);
        }
    }

    /**
     * 创建空报告
     *
     * @param userPlan
     * @param bussKey
     * @param upc
     * @return
     */
    private PlanReport createEmptyPlanReport(UserPlan userPlan, String bussKey, PlanValueDTO upc) {
        PlanReport pr = new PlanReport();
        pr.setUserId(userPlan.getUserId());
        pr.setBussKey(bussKey);
        pr.setBussDay(BussUtil.getBussDay(userPlan.getPlanType().getPeriodType(),bussKey));
        pr.setReportName(userPlan.getTitle() + "(" + bussKey + ")");
        pr.setPlan(userPlan);
        pr.setReportCountValue(0L);
        pr.setReportValue(0L);
        if (upc != null) {
            pr.setPlanCountValue(upc.getPlanCountValue());
            pr.setPlanValue(upc.getPlanValue());
            pr.setCompareBussKey(upc.getCompareBussKey());
        }else{
            pr.setPlanCountValue(0L);
            pr.setPlanValue(0L);
        }
        //设置统计结果
        CompareType compareType = userPlan.getCompareType();
        if (compareType == CompareType.MORE) {
            pr.setCountValueResult(PlanStatResult.UN_ACHIEVED);
            pr.setValueResult(PlanStatResult.UN_ACHIEVED);
        } else {
            pr.setCountValueResult(PlanStatResult.ACHIEVED);
            pr.setValueResult(PlanStatResult.ACHIEVED);
        }
        return pr;
    }

    /**
     * 计算封装SQL
     * @param userPlan
     * @param startTime
     * @param endTime
     * @return
     */
    private StatSQLDTO assembleSQL(UserPlan userPlan, Date startTime,Date endTime) {
        StatSQLDTO dto = new StatSQLDTO();
        PlanTemplate template = userPlan.getTemplate();
        dto.setSqlContent(template.getSqlContent());
        String bindValues = userPlan.getBindValues();
        if(StringUtil.isNotEmpty(bindValues)){
            List<StatValueClass> vcs = this.getBindValueClassList(template.getTemplateId(), StatBussType.PLAN);
            String[] bs = bindValues.split(",");
            int n = bs.length;
            for(int i=0;i<n;i++){
                dto.addArg(this.formatBindValue(vcs.get(i),bs[i]));
            }
        }
        //肯定绑定userId
        dto.addArg(userPlan.getUserId());
        //最后绑定时间
        dto.addArg(startTime);
        dto.addArg(endTime);
        return dto;
    }

    /**
     * 计划的配置值
     *
     * @param type
     * @param bussKey
     * @param userPlanId
     * @return
     */
    public PlanValueDTO getPlanValue(PlanValueCompareType type, String bussKey, Long userPlanId) {
        try {
            switch (type){
                case SPECIFY,ORIGINAL -> {
                    String hql = "select planCountValue,planValue,compareBussKey from PlanReport where plan.planId=?1 and bussKey=?2";
                    return this.getEntity(hql,PlanValueDTO.class,userPlanId,bussKey);
                }
                case LATEST -> {
                    //直接找本计划的
                    UserPlan plan = this.getEntityById(UserPlan.class,userPlanId);
                    PlanValueDTO dto = new PlanValueDTO();
                    dto.setPlanCountValue(plan.getPlanCountValue());
                    dto.setPlanValue(plan.getPlanValue());
                    dto.setCompareBussKey(bussKey);
                    return dto;
                }
                default ->  {
                    String hql = "select planCountValue,planValue,compareBussKey from PlanReport where plan.planId=?1 order by createdTime desc";
                    return this.getEntity(hql,PlanValueDTO.class,userPlanId);
                }
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取最近的用户计划配置值异常", e);
        }
    }

    /**
     * 获取结果的分类统计
     *
     * @param sf
     * @return
     */
    public List<PlanReportResultGroupStat> getPlanReportResultGroupStat(PlanReportResultGroupStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select count(0) as totalCount,{group_field} as resultType
                    from plan_report
                    {query_para}
                    group by {group_field}
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString())
                             .replace("{group_field}",sf.getGroupField());

            List<PlanReportResultGroupStat> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE, PlanReportResultGroupStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取结果的分类统计异常", e);
        }
    }

    /**
     * 获取平均值
     *
     * @param sf
     * @return
     */
    public List<PlanReportAvgStat> statPlanReportAvg(PlanReportAvgStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select plan_id as planId,AVG(report_count_value) as avgCountValue,AVG(report_value) as avgValue,
                    MIN(report_count_value) as minCountValue,MIN(report_value) as minValue,
                    MAX(report_count_value) as maxCountValue,MAX(report_value) as 'maxValue'
                    from plan_report
                    {query_para}
                    group by plan_id
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<PlanReportAvgStat> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE, PlanReportAvgStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取平均值异常", e);
        }
    }

    /**
     * 获取用户计划报告时间线
     *
     * @param planId
     * @return
     */
    public PlanReportTimeline getPlanReportTimeline(Long planId, Date bussDay) {
        try {
            String hql = "from PlanReportTimeline where plan.planId=?1 and bussDay=?2 ";
            return this.getEntity(hql,PlanReportTimeline.class, planId, bussDay);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户计划异常", e);
        }
    }

    /**
     * 获取用户计划报告时间线
     *
     * @return
     */
    public List<PlanReportTimeline> getTimelineList(Date startDate, Date endDate, Long planId) {
        try {
            String hql = "from PlanReportTimeline where bussDay>=?1 and bussDay<=?2 and plan.planId=?3 order by bussDay";
            List<PlanReportTimeline> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,PlanReportTimeline.class, startDate, endDate, planId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户计划报告时间线异常", e);
        }
    }

    /**
     * 获取计划配置
     * 需要根据用户级别判断
     *
     * @return
     */
    public PlanTemplate getPlanTemplate(Long templateId, Integer userLevel) {
        try {
            String hql = "from PlanTemplate where templateId=?1 and level<=?2 ";
            return this.getEntity(hql,PlanTemplate.class, templateId, userLevel);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取提醒配置列表异常", e);
        }
    }


    /**
     * 重新保存计划报告时间线
     */
    public void reSavePlanReportTimeline(List<PlanReportTimeline> datas, String bussKey, Long userId, Long planId) {
        try {
            String hql = "delete from PlanReportTimeline where bussKey=?1 and userId=?2 and plan.planId=?3";
            this.updateEntities(hql, bussKey, userId, planId);
            this.saveEntities(datas.toArray());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "重新保存计划报告时间线异常", e);
        }
    }

    /**
     * 保存计划配置模板
     *
     * @param bean
     * @param configList
     */
    public void savePlanTemplate(PlanTemplate bean, List<StatBindConfig> configList) {
        try {
            this.saveEntity(bean);
            if (StringUtil.isNotEmpty(configList)) {
                for (StatBindConfig c : configList) {
                    c.setFid(bean.getTemplateId());
                    c.setType(StatBussType.PLAN);
                }
                this.saveEntities(configList.toArray());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存计划配置模板异常", e);
        }
    }


    /**
     * 保存用户计划
     *
     * @param bean
     */
    public void saveOrUpdateUsePlan(UserPlan bean) {
        try {
            if(bean.getPlanId()==null){
                this.saveEntity(bean);
            }else{
                this.updateEntity(bean);
            }
            //检查提醒配置
            UserPlanRemind remind = this.getUserPlanRemind(bean.getPlanId());
            if (remind == null) {
                //生成默认
                remind = new UserPlanRemind();
                remind.setCreatedTime(new Date());
                remind.setFinishRemind(true);
                remind.setFromRate(50);
                remind.setRemark("由表单页面自动生成");
                remind.setRemindTime("08:30");
                remind.setTriggerInterval(1);
                remind.setTriggerType(TriggerType.DAY);
                remind.setUserId(bean.getUserId());
                remind.setPlan(bean);
                this.saveEntity(remind);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存用户计划异常", e);
        }
    }

    /**
     * 查找用户计划的提醒
     *
     * @param planId
     */
    public UserPlanRemind getUserPlanRemind(Long planId) {
        try {
            String hql = "from UserPlanRemind where plan.planId=?1 ";
            return this.getEntity(hql,UserPlanRemind.class, planId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "查找用户计划的提醒异常", e);
        }
    }

    /**
     * 删除用户计划
     *
     * @param planId
     */
    public void deleteUsePlan(Long planId) {
        try {
            String sql1 = "delete from user_plan_remind where plan_id=?1 ";
            this.execSqlUpdate(sql1, planId);
            //删除计划报告
            String sql2 = "delete from plan_report where plan_id=?1";
            this.execSqlUpdate(sql2, planId);

            //删除梦想绑定
            String sql3 = "delete from dream where plan_id=?1 ";
            this.execSqlUpdate(sql3, planId);

            //删除计划
            String sql4 = "delete from user_plan where plan_id=?1";
            this.execSqlUpdate(sql4, planId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除用户计划异常", e);
        }
    }

    /**
     * 更新最后提醒时间
     *
     * @param remindId
     */
    public void updateLastRemindTime(Long remindId, Date date) {
        try {
            String hql = "update UserPlanRemind set lastRemindTime=?1 where remindId=?2";
            this.updateEntities(hql, date, remindId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "更新最后提醒时间异常", e);
        }
    }

    /**
     * 获取需要提醒的用户计划列表
     *
     * @param planType
     * @return
     */
    public List<UserPlan> getNeedRemindUserPlan(PlanType planType) {
        try {
            String hql = "from UserPlan where planType=?1 and status=?2 and remind=?3 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,UserPlan.class, planType, CommonStatus.ENABLE, true);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取需要提醒的用户计划列表异常", e);
        }
    }


}
