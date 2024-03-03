package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.domain.PlanReport;
import cn.mulanbay.pms.persistent.domain.PlanReportTimeline;
import cn.mulanbay.pms.persistent.domain.UserPlan;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.service.PlanService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.pms.web.bean.req.report.plan.UserPlanSH;
import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.ScheduleCode;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

import java.util.Date;
import java.util.List;

import static cn.mulanbay.persistent.query.PageRequest.NO_PAGE;

/**
 * 用户计划统计job
 * 月计划每月一次，年计划每年一次（不同类型的计划需要开不同的调度任务，配置不同的调度周期）
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class PlanReportStatJob extends AbstractBaseJob {

    private static final Logger logger = LoggerFactory.getLogger(PlanReportStatJob.class);

    PlanReportStatJobPara para;

    PlanService planService = null;

    BaseService baseService = null;

    @Override
    public TaskResult doTask() {
        TaskResult result = new TaskResult();
        planService = BeanFactoryUtil.getBean(PlanService.class);
        baseService = BeanFactoryUtil.getBean(BaseService.class);
        UserPlanSH sf = new UserPlanSH();
        sf.setStatus(CommonStatus.ENABLE);
        sf.setPlanType(para.getPlanType());
        sf.setPage(NO_PAGE);
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(UserPlan.class);
        List<UserPlan> list = baseService.getBeanList(pr);
        if (list.isEmpty()) {
            result.setComment("可用用户计划为空");
        } else {
            Date bussDay = this.getBussDay();
            int success = 0;
            int fail = 0;
            for (UserPlan userPlan : list) {
                if(userPlan.getUserId()== Constant.ADMIN_USER_ID){
                    //默认的管理员用户不参与统计，只做模板使用
                    continue;
                }
                boolean b = statAndSaveUserPlan(userPlan, bussDay);
                if (b) {
                    success++;
                } else {
                    fail++;
                }
            }
            result.setResult(JobResult.SUCCESS);
            result.setComment("一共统计了" + list.size() + "个用户计划,成功:" + success + "个,失败" + fail + "个");
        }
        return result;
    }

    private boolean statAndSaveUserPlan(UserPlan userPlan, Date bussDay) {
        try {
            PlanReport planReport = null;
            //先去用户计划时间线查询，如果有的话，直接复制拷贝
            PlanReportTimeline timeline = planService.getPlanReportTimeline(userPlan.getPlanId(), bussDay);
            if (timeline != null) {
                planReport = new PlanReport();
                BeanCopy.copy(timeline, planReport);
                planReport.setBussDay(bussDay);
            } else {
                String bussKey = BussUtil.getBussKey(userPlan.getPlanType().getPeriodType(),bussDay);
                planReport = planService.statPlanReport(userPlan, bussKey);
            }
            if (planReport != null) {
                baseService.saveObject(planReport);
            } else {
                logger.warn("用户计划[" + userPlan.getTitle() + "],id=" + userPlan.getPlanId() + "在[" + bussDay + "]没有数据");
            }
            return true;
        } catch (BeansException e) {
            logger.error("处理用户计划统计一次", e);
            return false;
        }

    }

    /**
     * 参数为调度周期类型
     *
     * @return
     */
    @Override
    public ParaCheckResult checkTriggerPara() {
        ParaCheckResult result = new ParaCheckResult();
        para = this.getTriggerParaBean();
        if (para == null) {
            result.setErrorCode(ScheduleCode.TRIGGER_PARA_NULL);
            result.setMessage("调度参数检查失败，参数为空");
        }
        return result;
    }

    @Override
    public Class getParaDefine() {
        return PlanReportStatJobPara.class;
    }

}
