package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.pms.persistent.service.UserCalendarService;
import cn.mulanbay.schedule.para.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;

import java.util.Date;

/**
 * 用户日历超时设置任务
 */
public class UserCalendarTimeoutJob extends AbstractBaseJob {

    @Override
    public TaskResult doTask() {
        TaskResult taskResult = new TaskResult();
        UserCalendarService userCalendarService = BeanFactoryUtil.getBean(UserCalendarService.class);
        int n = userCalendarService.updateUserCalendarForExpired(new Date());
        taskResult.setComment("更新了" + n + "个超时用户日历");
        taskResult.setResult(JobResult.SUCCESS);
        return taskResult;
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
