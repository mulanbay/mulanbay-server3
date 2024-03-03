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
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

import java.util.Date;
import java.util.List;

/**
 * 用户计划时间线统计job,一般为每天统计一次,统计昨天的
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class PlanReportTimelineStatJob extends AbstractBaseJob {

    private static final Logger logger = LoggerFactory.getLogger(PlanReportTimelineStatJob.class);

    PlanService planService = null;

    BaseService baseService = null;

    @Override
    public TaskResult doTask() {
        TaskResult result = new TaskResult();
        planService = BeanFactoryUtil.getBean(PlanService.class);
        baseService = BeanFactoryUtil.getBean(BaseService.class);
        UserPlanSH sf = new UserPlanSH();
        sf.setStatus(CommonStatus.ENABLE);
        sf.setPage(PageRequest.NO_PAGE);
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

    @Override
    public Class getParaDefine() {
        return null;
    }

    private boolean statAndSaveUserPlan(UserPlan userPlan, Date bussDay) {
        try {
            String bussKey = BussUtil.getBussKey(userPlan.getPlanType().getPeriodType(),bussDay);
            PlanReport planReport = planService.statPlanReport(userPlan, bussKey);
            if (planReport == null) {
                logger.warn("用户计划[" + userPlan.getTitle() + "],id=" + userPlan.getPlanId() + "在[" + bussKey + "]没有数据");
                return true;
            }
            PlanReportTimeline timeline = new PlanReportTimeline();
            BeanCopy.copy(planReport, timeline);
            timeline.setTimelineName(planReport.getReportName()+"["+bussKey+"]");
            //需要设置为该天的
            timeline.setBussDay(bussDay);
            baseService.saveObject(timeline);
            return true;
        } catch (BeansException e) {
            logger.error("处理用户计划统计一次", e);
            return false;
        }

    }

    @Override
    public ParaCheckResult checkTriggerPara() {
        return DEFAULT_SUCCESS_PARA_CHECK;
    }
}
