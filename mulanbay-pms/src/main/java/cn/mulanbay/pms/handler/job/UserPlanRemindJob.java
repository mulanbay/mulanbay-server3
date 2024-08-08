package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.handler.ReportHandler;
import cn.mulanbay.pms.handler.RewardHandler;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.CompareType;
import cn.mulanbay.pms.persistent.enums.PlanType;
import cn.mulanbay.pms.persistent.enums.UserCalendarFinishType;
import cn.mulanbay.pms.persistent.service.PlanService;
import cn.mulanbay.pms.persistent.service.UserCalendarService;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.ScheduleCode;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 统计用户计划完成进度的调度
 * 如果进度达不到要求（和时间的进度想比），往消息表写一条待发送记录
 * 一般为每天凌晨统计，根据用户配置的提醒时间设置expectSendTime值
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class UserPlanRemindJob extends AbstractBaseRemindJob {

    private static final Logger logger = LoggerFactory.getLogger(UserPlanRemindJob.class);

    UserPlanRemindJobPara para;

    PlanService planService = null;

    NotifyHandler notifyHandler = null;

    CacheHandler cacheHandler = null;

    RewardHandler rewardHandler = null;

    ReportHandler reportHandler = null;

    UserCalendarService userCalendarService = null;

    BaseService baseService = null;

    @Override
    public TaskResult doTask() {
        TaskResult result = new TaskResult();
        if (para.getPlanType() == null) {
            result.setResult(JobResult.SKIP);
            result.setComment("计划类型为空，无法执行调度");
        }
        planService = BeanFactoryUtil.getBean(PlanService.class);
        notifyHandler = BeanFactoryUtil.getBean(NotifyHandler.class);
        cacheHandler = BeanFactoryUtil.getBean(CacheHandler.class);
        rewardHandler = BeanFactoryUtil.getBean(RewardHandler.class);
        reportHandler = BeanFactoryUtil.getBean(ReportHandler.class);
        userCalendarService = BeanFactoryUtil.getBean(UserCalendarService.class);
        baseService = BeanFactoryUtil.getBean(BaseService.class);
        List<UserPlan> list = planService.getNeedRemindUserPlan(para.getPlanType());
        if (list.isEmpty()) {
            result.setComment("没有需要提醒的用户计划");
        } else {
            Date bussDay = this.getBussDay();
            int success = 0;
            int fail = 0;
            for (UserPlan userPlan : list) {
                boolean b = handleUserPlanRemind(userPlan,bussDay);
                if (b) {
                    success++;
                } else {
                    fail++;
                }
            }
            result.setComment("一共统计了" + list.size() + "个用户计划,成功:" + success + "个,失败" + fail + "个");
            result.setResult(JobResult.SUCCESS);
        }
        return result;
    }

    private boolean handleUserPlanRemind(UserPlan userPlan, Date bussDay) {
        try {
            if (!userPlan.getRemind()) {
                return true;
            }
            Long userId = userPlan.getUserId();
            //Step 1:  第一步先判断是否已经通知过
            String key = CacheKey.getKey(CacheKey.USER_PLAN_NOTIFY, userId.toString(), userPlan.getPlanId().toString());
            String cs = cacheHandler.getForString(key);
            if (cs != null) {
                logger.debug("用户ID=" + userId + "的计划[" + userPlan.getTitle() + "],id=" + userPlan.getPlanId() + "已经提醒过了");
                return true;
            }
            CompareType compareType = userPlan.getCompareType();
            UserPlanRemind remind = planService.getUserPlanRemind(userPlan.getPlanId());
            if (remind == null) {
                return true;
            }
            //Step 2: 通知
            // 通过缓存查询上一次提醒时间
            //需要用运营日计算，比如2017-12-01号调度的，应该是用2017-11-30号计算
            int totalDays = this.getTotalDaysPlan(bussDay);
            int dayIndex = this.getDayOfPlan(bussDay);
            //已经过去几天
            double rate = NumberUtil.getPercent(dayIndex, totalDays, 0);
            String bussKey = BussUtil.getBussKey(userPlan.getPlanType().getPeriodType(),bussDay);
            if (rate >= remind.getFromRate().doubleValue()) {
                //统计
                PlanReport planReport = planService.statPlanReport(userPlan,bussKey);
                double planCountRate = NumberUtil.getPercent(planReport.getReportCountValue(), planReport.getPlanCountValue(), 2);
                double planValueRate = NumberUtil.getPercent(planReport.getReportValue(), planReport.getPlanValue(), 2);
                if (compareType == CompareType.MORE) {
                    //大于类型(即完成的值必须要大于这个)
                    if (planCountRate < rate || planValueRate < rate) {
                        //进度落后
                        //提醒
                        String title = "计划[" + userPlan.getTitle() + "]提醒";
                        String content = this.formatContent(planReport,"你的计划[" + userPlan.getTitle() + "]进度落后了,时间已经过去" + rate + "%,但是进度没有赶上。\n");
                        this.notifyMessage(title, content.toString(), remind, false);
                    } else {
                        //计划完成
                        String title = "计划[" + userPlan.getTitle() + "]完成";
                        String content = this.formatContent(planReport,"你的计划[" + userPlan.getTitle() + "]已经完成\n");
                        this.notifyMessage(title, content.toString(), remind, true);
                    }
                } else {
                    //小于类型
                    //超标
                    if (planReport.getReportCountValue() > planReport.getPlanCountValue() || planReport.getReportValue() > planReport.getPlanValue()) {
                        String title = "计划[" + userPlan.getTitle() + "]提醒";
                        String content = this.formatContent(planReport,"你的计划[" + userPlan.getTitle() + "]已经超出预期\n");
                        this.notifyMessage(title, content.toString(), remind, true);
                    } else if (dayIndex == totalDays) {
                        // 完成目标
                        String title = "计划[" + userPlan.getTitle() + "]完成";
                        String content = this.formatContent(planReport,"你的计划[" + userPlan.getTitle() + "]已经满足要求\n");
                        this.notifyMessage(title, content.toString(), remind, true);
                    }
                }
                return true;
            } else {
                logger.debug("当前时间进度是" + rate + ",配置的最小提醒时间进度为" + remind.getFromRate() + ",不提醒");
            }
            return true;
        } catch (Exception e) {
            logger.error("处理用户计划[" + userPlan.getTitle() + "]异常", e);
            return false;
        }
    }

    private String formatContent(PlanReport planReport,String finishDesc){
        String content = finishDesc+"\n"
                +"计划的次数已经完成["+planReport.getReportCountValue()+"次,期望"+planReport.getPlanCountValue()+"次 \n"
                +"计划值已经完成["+planReport.getReportValue()+"]"+planReport.getPlan().getUnit()+",期望["+planReport.getPlanValue()+"}]"+planReport.getPlan().getUnit()+" \n"
                +"统计时间:"+planReport.getBussKey();
        return content;
    }

    /**
     * 计划所在总的天数
     *
     * @param date
     * @return
     */
    private int getTotalDaysPlan(Date date) {
        if (para.getPlanType() == PlanType.MONTH) {
            return DateUtil.getMonthDays(date);
        } else if (para.getPlanType() == PlanType.YEAR) {
            return DateUtil.getYearDays(date);
        } else {
            return 0;
        }
    }

    /**
     * 计划所在总的天数
     *
     * @param date
     * @return
     */
    private int getDayOfPlan(Date date) {
        if (para.getPlanType() == PlanType.MONTH) {
            return DateUtil.getDayOfMonth(date);
        } else if (para.getPlanType() == PlanType.YEAR) {
            return DateUtil.getDayOfYear(date);
        } else {
            return 0;
        }
    }

    /**
     * 更新积分，完成+，未达到要求减
     *
     * @param userPlan
     * @param isComplete
     */
    private void rewardPoint(UserPlan userPlan, boolean isComplete, Long messageId) {
        try {
            int radio = 0;
            if (userPlan.getPlanType() == PlanType.YEAR) {
                radio = 30;
            } else if (userPlan.getPlanType() == PlanType.MONTH) {
                radio = 10;
            }
            int rewards = 0;
            String remark = null;
            if (isComplete) {
                rewards = userPlan.getTemplate().getRewards() * radio;
                remark = "计划[" + userPlan.getTitle() + "]完成奖励";
            } else {
                //未完成减去
                rewards = userPlan.getTemplate().getRewards() * (-1);
                remark = "计划[" + userPlan.getTitle() + "]进度未达到要求惩罚";
            }
            rewardHandler.reward(userPlan.getUserId(), rewards, userPlan.getPlanId(), BussSource.PLAN, remark, messageId);
            if (isComplete) {
                //删除日历
                PlanTemplate template = userPlan.getTemplate();
                String bindKey = reportHandler.createBindValueKey(userPlan.getBindValues());
                String bussIdentityKey = BussUtil.getCalendarBussIdentityKey(template.getSource(),bindKey);
                userCalendarService.updateUserCalendarForFinish(userPlan.getUserId(), bussIdentityKey, new Date(), UserCalendarFinishType.AUTO,userPlan.getPlanId(), BussSource.PLAN, messageId);
            } else {
                //添加到用户日历
                addToUserCalendar(userPlan, messageId);
            }
        } catch (Exception e) {
            logger.error("计划[" + userPlan.getTitle() + "]积分奖励异常", e);
        }
    }

    /**
     * 更新到用户日历
     *
     * @param userPlan
     */
    private void addToUserCalendar(UserPlan userPlan, Long messageId) {
        try {
            PlanTemplate template = userPlan.getTemplate();
            String bindKey = reportHandler.createBindValueKey(userPlan.getBindValues());
            String bussIdentityKey = BussUtil.getCalendarBussIdentityKey(template.getSource(),bindKey);
            UserCalendar uc = userCalendarService.getUserCalendar(userPlan.getUserId(), bussIdentityKey, new Date());
            if (uc != null) {
                userCalendarService.updateUserCalendarToDate(uc, new Date(), messageId);
            } else {
                uc = new UserCalendar();
                uc.setUserId(userPlan.getUserId());
                uc.setTitle(userPlan.getCalendarTitle());
                uc.setContent(userPlan.getCalendarTitle());
                uc.setDelays(0);
                String calendarTime = userPlan.getCalendarTime();
                if (StringUtil.isEmpty(calendarTime)) {
                    uc.setBussDay(DateUtil.getDate(0));
                    uc.setAllDay(true);
                } else {
                    Date bussDay = DateUtil.addHourMin(null, calendarTime);
                    uc.setBussDay(bussDay);
                    uc.setAllDay(false);
                }
                if (userPlan.getPlanType() == PlanType.MONTH) {
                    uc.setExpireTime(DateUtil.getMonthLast(uc.getBussDay()));
                } else {
                    uc.setExpireTime(DateUtil.getYearLast(DateUtil.getYear(uc.getBussDay())));
                }
                uc.setBussIdentityKey(bussIdentityKey);
                uc.setSourceType(BussSource.PLAN);
                uc.setSourceId(userPlan.getPlanId());
                uc.setMessageId(messageId);
                userCalendarService.addUserCalendarToDate(uc);
            }
        } catch (Exception e) {
            logger.error("添加到用户日历异常", e);
        }

    }

    /**
     * 消息提醒
     *
     * @param title
     * @param content
     * @param remind
     * @param isComplete
     */
    private void notifyMessage(String title, String content, UserPlanRemind remind, boolean isComplete) {
        UserPlan userPlan = remind.getPlan();
        RemindTimeBean bean = this.calcRemindExpectTime(remind.getTriggerInterval(), remind.getTriggerType(), remind.getLastRemindTime(), remind.getRemindTime());
        //Step 1: 发送消息通知
        Long messageId;
        if (!isComplete) {
            messageId = notifyHandler.addMessage(PmsCode.USER_PLAN_UN_COMPLETED_STAT, title, content,
                    userPlan.getUserId(), bean.getNextRemindTime());
        } else {
            messageId = notifyHandler.addMessage(PmsCode.USER_PLAN_COMPLETED_STAT, title, content,
                    userPlan.getUserId(), bean.getNextRemindTime());
        }
        //Step 2: 更新最后的提醒时间
        planService.updateLastRemindTime(remind.getRemindId(), new Date());
        Date nextRemindTime = bean.getNextRemindTime();
        if (isComplete) {
            //完成后，设置为最后一天
            PlanType planType = userPlan.getPlanType();
            if (planType == PlanType.MONTH) {
                nextRemindTime = DateUtil.getMonthLast(new Date());
            } else if (planType == PlanType.YEAR) {
                nextRemindTime = DateUtil.getYearLast(new Date());
            }
        }
        /**
         * Step 3: 设置提醒的缓存，避免下一次重复提醒
         * 需要在这里设置上一次的提醒缓存，不能再handleUserPlanRemind方法里设置
         * 因为只有触发了提醒条件才能告知下一次是否能提醒
         */
        String key = CacheKey.getKey(CacheKey.USER_PLAN_NOTIFY, userPlan.getUserId().toString(), userPlan.getPlanId().toString());
        int expireSeconds = 0;
        //失效时间为通知周期的秒数，-5为了保证第二次通知时间点job能执行
        if (isComplete) {
            //如果是完成类型那么，设置为最后一天，保证一个周期内只会提醒一次
            expireSeconds = (int) ((nextRemindTime.getTime() - System.currentTimeMillis()) / 1000);
        } else {
            expireSeconds = bean.getDays() * 24 * 3600;
        }
        logger.debug("user plan remind expireSeconds:"+expireSeconds);
        cacheHandler.set(key, "123", expireSeconds - 5);
        //Step 4:积分奖励
        rewardPoint(userPlan, isComplete, messageId);
    }

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
        return UserPlanRemindJobPara.class;
    }

}
