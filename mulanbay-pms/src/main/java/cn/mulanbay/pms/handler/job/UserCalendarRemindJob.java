package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.persistent.domain.UserCalendar;
import cn.mulanbay.pms.persistent.service.UserCalendarService;
import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 用户日历提醒
 *
 * @author fenghong
 * @create 2018-03-13 21:12
 */
public class UserCalendarRemindJob extends AbstractBaseJob {

    private static final Logger logger = LoggerFactory.getLogger(UserCalendarRemindJob.class);

    private UserCalendarRemindJobPara para;

    @Override
    public TaskResult doTask() {
        TaskResult taskResult = new TaskResult();
        UserCalendarService userCalendarService = BeanFactoryUtil.getBean(UserCalendarService.class);
        List<UserCalendar> list = userCalendarService.getCurrentUserCalendarList(null);
        if (list.isEmpty()) {
            taskResult.setComment("没有用户日历数据");
        } else {
            taskResult.setResult(JobResult.SUCCESS);
            NotifyHandler notifyHandler = BeanFactoryUtil.getBean(NotifyHandler.class);
            int n = list.size();
            long currentUserId = list.get(0).getUserId();
            StringBuffer sb = new StringBuffer();
            int aa = 0;
            int tmpIndex = 1;
            for (int i = 0; i < n; i++) {
                UserCalendar uc = list.get(i);
                if (uc.getUserId() != currentUserId) {
                    notifyHandler.addNotifyMessage(PmsCode.USER_DAILY_TASK_STAT, "今日任务", sb.toString(),
                            currentUserId, null);
                    //setCacheCounts(currentUserId, tmpIndex);
                    aa++;
                    currentUserId = uc.getUserId();
                    tmpIndex = 1;
                    sb = new StringBuffer();
                    sb.append((tmpIndex++) + "." + uc.getTitle() + "\n");
                } else {
                    if (i == (n - 1)) {
                        //最后一次
                        sb.append((tmpIndex++) + "." + uc.getTitle() + "\n");
                        notifyHandler.addNotifyMessage(PmsCode.USER_DAILY_TASK_STAT, "今日任务", sb.toString(),
                                currentUserId, null);
                        //setCacheCounts(currentUserId, tmpIndex);
                        aa++;
                    } else {
                        sb.append((tmpIndex++) + "." + uc.getTitle() + "\n");
                    }
                }

            }
            taskResult.setComment("共发送" + aa + "条用户日历数据");
        }
        return taskResult;
    }

    @Override
    public ParaCheckResult checkTriggerPara() {
        para = this.getTriggerParaBean();
        if (para == null) {
            para = new UserCalendarRemindJobPara();
        }
        return DEFAULT_SUCCESS_PARA_CHECK;
    }

    @Override
    public Class getParaDefine() {
        return null;
    }
}
