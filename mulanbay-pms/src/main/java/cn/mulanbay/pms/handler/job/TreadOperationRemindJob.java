package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.persistent.domain.TreatOperation;
import cn.mulanbay.pms.persistent.domain.UserCalendar;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.service.TreatService;
import cn.mulanbay.pms.persistent.service.UserCalendarService;
import cn.mulanbay.schedule.para.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 用户手术复查提醒job
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class TreadOperationRemindJob extends AbstractBaseRemindJob {

    private static final Logger logger = LoggerFactory.getLogger(TreadOperationRemindJob.class);

    CacheHandler cacheHandler;

    TreatService treatService = null;

    NotifyHandler notifyHandler = null;

    TreadOperationRemindJobPara para;

    @Override
    public TaskResult doTask() {
        TaskResult taskResult = new TaskResult();
        cacheHandler = BeanFactoryUtil.getBean(CacheHandler.class);
        treatService = BeanFactoryUtil.getBean(TreatService.class);
        notifyHandler = BeanFactoryUtil.getBean(NotifyHandler.class);
        Date bussDay = this.getBussDay();
        Date endDate = DateUtil.getDate(para.getCheckDays(), bussDay);
        endDate = DateUtil.tillMiddleNight(endDate);
        List<TreatOperation> list = treatService.getNeedRemindOperation(bussDay, endDate, null);
        if (list.isEmpty()) {
            taskResult.setComment("没有需要提醒的手术");
        } else {
            //目前提醒时间统一由调度器设置
            Date remindNotifyTime = DateUtil.getDate(DateUtil.getFormatDate(new Date(), DateUtil.FormatDay1) + " " + para.getRemindTime() + ":00", DateUtil.Format24Datetime);
            int n = 0;
            for (TreatOperation to : list) {
                handleTreatOperation(to, remindNotifyTime);
                n++;
            }
            taskResult.setResult(JobResult.SUCCESS);
            taskResult.setComment("检查手术共" + n + "个");
        }
        return taskResult;
    }

    private void handleTreatOperation(TreatOperation to, Date remindNotifyTime) {
        try {
            //复查的手术是否已经做了
            TreatOperation vo = treatService.getOperation(to.getReviewDate(), to.getUserId(), to.getOperationName());
            if (vo != null) {
                logger.info("用户[" + to.getUserId() + "]手术[" + to.getOperationName() + "]已经检查过了");
                return;
            } else {
                String title = "手术[" + to.getOperationName() + "]复查提醒";
                String content = "手术[" + to.getOperationName() + "]需要复查,预定复查日期[" + DateUtil.getFormatDate(to.getReviewDate(), DateUtil.FormatDay1) + "].";
                Long messageId = notifyHandler.addMessage(PmsCode.USER_OPERATION_REMIND_STAT, title, content,
                        to.getUserId(), remindNotifyTime);
                //写用户日历
                this.addToUserCalendar(to, messageId, content);
            }
        } catch (Exception e) {
            logger.error("提醒用户手术异常", e);
        }
    }

    /**
     * 更新到用户日历
     *
     * @param to
     */
    private void addToUserCalendar(TreatOperation to, Long messageId, String content) {
        try {
            UserCalendarService userCalendarService = BeanFactoryUtil.getBean(UserCalendarService.class);
            String bussIdentityKey = "treat_operation_" + to.getOperationName();
            UserCalendar uc = userCalendarService.getUserCalendar(to.getUserId(), bussIdentityKey, new Date());
            if (uc != null) {
                userCalendarService.updateUserCalendarToDate(uc, new Date(), messageId);
            } else {
                uc = new UserCalendar();
                uc.setUserId(to.getUserId());
                uc.setTitle("手术复查");
                uc.setContent(content);
                uc.setDelays(0);
                uc.setBussDay(DateUtil.getDate(0));
                uc.setAllDay(true);
                uc.setExpireTime(DateUtil.getMonthLast(new Date()));
                uc.setBussIdentityKey(bussIdentityKey);
                uc.setSourceType(BussSource.TREAT_OPERATION);
                uc.setSourceId(to.getOperationId());
                uc.setMessageId(messageId);
                userCalendarService.addUserCalendarToDate(uc);
            }
        } catch (Exception e) {
            logger.error("添加到用户日历异常", e);
        }

    }

    @Override
    public ParaCheckResult checkTriggerPara() {
        para = this.getTriggerParaBean();
        if (para == null) {
            para = new TreadOperationRemindJobPara();
        }
        return DEFAULT_SUCCESS_PARA_CHECK;
    }

    @Override
    public Class getParaDefine() {
        return TreadOperationRemindJobPara.class;
    }
}
