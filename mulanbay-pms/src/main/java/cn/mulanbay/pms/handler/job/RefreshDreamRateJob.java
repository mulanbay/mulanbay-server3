package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.persistent.domain.Dream;
import cn.mulanbay.pms.persistent.domain.PlanReport;
import cn.mulanbay.pms.persistent.domain.UserPlan;
import cn.mulanbay.pms.persistent.enums.DreamStatus;
import cn.mulanbay.pms.persistent.service.DreamService;
import cn.mulanbay.pms.persistent.service.PlanService;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 刷新梦想进度
 * 只有那些关联了用户计划的梦想才能被刷新进度
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class RefreshDreamRateJob extends AbstractBaseJob {

    private static final Logger logger = LoggerFactory.getLogger(RefreshDreamRateJob.class);

    DreamService dreamService;

    PlanService planService;

    BaseService baseService;

    @Override
    public TaskResult doTask() {
        TaskResult taskResult = new TaskResult();
        dreamService = BeanFactoryUtil.getBean(DreamService.class);
        planService = BeanFactoryUtil.getBean(PlanService.class);
        baseService = BeanFactoryUtil.getBean(BaseService.class);
        List<Dream> dreamList = dreamService.getNeedRefreshRateDream(null);
        if (dreamList.isEmpty()) {
            taskResult.setComment("没有需要刷新进度的梦想");
        } else {
            Date bussDay = this.getBussDay();
            int successes = 0;
            int fails = 0;
            for (Dream d : dreamList) {
                boolean b = refreshRate(d,bussDay);
                if (b) {
                    successes++;
                } else {
                    fails++;
                }
            }
            taskResult.setResult(JobResult.SUCCESS);
            taskResult.setComment("本次刷新梦想进度成功" + successes + "个,失败" + fails + "个");
        }
        return taskResult;
    }

    private boolean refreshRate(Dream d,Date bussDay) {
        try {
            UserPlan pc = baseService.getObject(UserPlan.class,d.getPlanId());
            String bussKey = BussUtil.getBussKey(pc.getPlanType().getPeriodType(),bussDay);
            PlanReport report = planService.statPlanReport(pc, bussKey);
            long v = report.getReportValue();
            double rate = NumberUtil.getPercent(v, d.getPlanValue(), 0);
            d.setRate((int) rate);
            if (rate == 0) {
                d.setStatus(DreamStatus.CREATED);
            } else if (rate < 100) {
                d.setStatus(DreamStatus.STARTED);
            } else {
                d.setStatus(DreamStatus.FINISHED);
            }
            baseService.updateObject(d);
            return true;
        } catch (Exception e) {
            logger.error("刷新梦想，id=" + d.getDreamId() + "异常", e);
            return false;
        }
    }

    @Override
    public ParaCheckResult checkTriggerPara() {
        return DEFAULT_SUCCESS_PARA_CHECK;
    }

    @Override
    public Class getParaDefine() {
        return null;
    }
}
