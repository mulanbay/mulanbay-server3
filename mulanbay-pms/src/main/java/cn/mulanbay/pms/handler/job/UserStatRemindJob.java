package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.handler.RewardHandler;
import cn.mulanbay.pms.handler.UserStatHandler;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.dto.report.StatResultDTO;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.ResultType;
import cn.mulanbay.pms.persistent.enums.UserCalendarFinishType;
import cn.mulanbay.pms.persistent.service.StatService;
import cn.mulanbay.pms.persistent.service.UserCalendarService;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.enums.TriggerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 统计用户提醒的调度
 * 如果达到警告、告警值，往消息表写一条待发送记录
 * 一般为每天凌晨统计，根据用户配置的提醒时间设置expectSendTime值
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class UserStatRemindJob extends AbstractBaseRemindJob {

    private static final Logger logger = LoggerFactory.getLogger(UserStatRemindJob.class);

    private UserStatRemindJobPara para;

    StatService statService;

    CacheHandler cacheHandler;

    NotifyHandler notifyHandler = null;

    RewardHandler rewardHandler = null;

    UserStatHandler userStatHandler = null;

    UserCalendarService userCalendarService = null;

    BaseService baseService = null;

    @Override
    public TaskResult doTask() {
        TaskResult taskResult = new TaskResult();
        statService = BeanFactoryUtil.getBean(StatService.class);
        List<UserStat> list = statService.getNeedRemindUserStat();
        cacheHandler = BeanFactoryUtil.getBean(CacheHandler.class);
        notifyHandler = BeanFactoryUtil.getBean(NotifyHandler.class);
        rewardHandler = BeanFactoryUtil.getBean(RewardHandler.class);
        userStatHandler = BeanFactoryUtil.getBean(UserStatHandler.class);
        userCalendarService = BeanFactoryUtil.getBean(UserCalendarService.class);
        baseService = BeanFactoryUtil.getBean(BaseService.class);
        if (list.isEmpty()) {
            taskResult.setResult(JobResult.SUCCESS);
            taskResult.setComment("没有需要提醒的用户提醒");
        } else {
            int success = 0;
            int fail = 0;
            for (UserStat userNotify : list) {
                boolean b = handleUserStat(userNotify);
                if (b) {
                    success++;
                } else {
                    fail++;
                }
            }
            taskResult.setResult(JobResult.SUCCESS);
            taskResult.setComment("一共统计了" + list.size() + "个用户提醒,成功:" + success + "个,失败" + fail + "个");
        }
        return taskResult;
    }

    private boolean handleUserStat(UserStat us) {
        try {
            if (!us.getRemind()) {
                return true;
            }
            Long userId = us.getUserId();
            StatResultDTO resultDTO = null;
            if (para.getCacheResult()) {
                //如果设置需要缓存，那么第一步先去缓存结果
                resultDTO = statService.getStatResult(us);
                userStatHandler.cacheStatResult(resultDTO, para.getExpireSeconds());
            }
            if(resultDTO.getValue()==null||resultDTO.getValue()==0){
                logger.debug("用户ID=" + userId + "的提醒[" + us.getTitle() + "],id=" + us.getStatId() + "的统计值为空或者为零，不需要提醒");
                return true;
            }
            //step 1: 记录时间线日志
            this.addTimeline(resultDTO);
            //Step 2: 第一步先判断是否已经通知过
            String key = CacheKey.getKey(CacheKey.USER_NOTIFY, userId.toString(), us.getStatId().toString());
            String cs = cacheHandler.getForString(key);
            if (cs != null) {
                logger.debug("用户ID=" + userId + "的提醒[" + us.getTitle() + "],id=" + us.getStatId() + "已经提醒过了");
                return true;
            }
            //Step 3: 通知
            UserStatRemind unr = statService.getUserStatRemind(us.getStatId(), us.getUserId());

            //Step 3: 通知
            String title = null;
            String content = null;
            UserStat userStat = resultDTO.getUserStat();
            StatTemplate template = userStat.getTemplate();
            long overRate = resultDTO.getOverValue()*100/resultDTO.getValue();
            if(overRate>=unr.getOverRate()){
                title = "[" + userStat.getTitle() + "]报警";
                if (template.getResultType() == ResultType.DATE_NAME || template.getResultType() == ResultType.NUMBER_NAME) {
                    content = "[" + userStat.getTitle() + "][" + resultDTO.getNameValue() + "]超过报警比例[" + unr.getOverRate() + "%],实际值为["
                            + resultDTO.getOverValue() + "],计量单位:[" + template.getValueTypeName() + "]\n";
                } else {
                    content = "[" + userStat.getTitle() + "]超过报警比例[" + unr.getOverRate() + "%],实际值为["
                            + resultDTO.getOverValue() + "],计量单位:[" + template.getValueTypeName() + "]\n";
                }
                this.notifyMessage(title,content,unr);
            } else {
                logger.debug("用户ID=" + userId + "的提醒[" + userStat.getTitle() + "],id=" + userStat.getStatId() + "不需要提醒");
                rewardPoint(userStat, true, null);
            }

            return true;
        } catch (Exception e) {
            logger.error("处理用户提醒:" + us.getTitle() + "异常", e);
            return false;
        }
    }

    /**
     * 添加时间线日志
     * @param dto
     */
    private void addTimeline(StatResultDTO dto){
        try {
            UserStatTimeline timeline = new UserStatTimeline();
            timeline.setUserId(dto.getUserStat().getUserId());
            timeline.setStat(dto.getUserStat());
            timeline.setValue(dto.getValue());
            timeline.setNameValue(dto.getNameValue());
            timeline.setExpectValue(dto.getUserStat().getExpectValue());
            timeline.setUnit(dto.getUserStat().getUnit());
            baseService.saveObject(timeline);
        } catch (Exception e) {
            logger.error("添加时间线日志异常", e);
        }
    }

    private void notifyMessage(String title, String content, UserStatRemind remind) {
        content = content + "统计日期:" + DateUtil.getFormatDate(this.getBussDay(), DateUtil.FormatDay1);
        RemindTimeBean bean = this.calcRemindExpectTime(remind.getTriggerInterval(), remind.getTriggerType(), remind.getLastRemindTime(), remind.getRemindTime());
        //Step 1: 发送消息通知
        Long messageId = notifyHandler.addNotifyMessage(PmsCode.USER_NOTIFY_STAT, title, content,
                remind.getUserId(), bean.getNextRemindTime());
        //Step 2: 更新最后的提醒时间
        statService.updateLastRemindTime(remind.getRemindId(), new Date());
        /**
         * Step 3: 设置提醒的缓存，避免下一次重复提醒
         * 需要在这里设置上一次的提醒缓存，不能再handleUserPlanRemind方法里设置
         * 因为只有触发了提醒条件才能告知下一次是否能提醒
         */
        String key = CacheKey.getKey(CacheKey.USER_NOTIFY, remind.getUserId().toString(), remind.getStat().getStatId().toString());
        //失效时间为通知周期的秒数，-5为了保证第二次通知时间点job能执行
        cacheHandler.set(key, "123", bean.getDays() * 24 * 3600 - 5);
        //Step 4: 更新积分
        rewardPoint(remind.getStat(), false, messageId);
        //Step 5: 加入用户日历
        addToUserCalendar(remind, messageId);
    }

    /**
     * 更新积分，完成+，未达到要求减
     *
     * @param us
     */
    private void rewardPoint(UserStat us, boolean isComplete, Long messageId) {
        try {
            int radio = 1;
            int rewards = us.getTemplate().getRewards() * radio;
            String remark = "用户提醒配置[" + us.getTitle() + "]达到要求奖励";
            if (!isComplete) {
                rewards = -rewards;
                remark = "用户提醒配置[" + us.getTitle() + "]触发警报惩罚";
            }
            rewardHandler.rewardPoints(us.getUserId(), rewards, us.getStatId(), BussSource.STAT, remark, messageId);
            if (isComplete) {
                StatTemplate template = us.getTemplate();
                String bussIdentityKey = BussUtil.getCalendarBussIdentityKey(template.getBussKey(),us.getBindValues());
                userCalendarService.updateUserCalendarForFinish(us.getUserId(), bussIdentityKey, new Date(), UserCalendarFinishType.AUTO,us.getStatId(), BussSource.STAT, messageId);
            }
        } catch (Exception e) {
            logger.error("计划[" + us.getTitle() + "]积分奖励异常", e);
        }
    }

    /**
     * 更新到用户日历
     *
     * @param remind
     */
    private void addToUserCalendar(UserStatRemind remind, Long messageId) {
        try {
            UserStat us = remind.getStat();
            StatTemplate template = us.getTemplate();
            String bussIdentityKey = BussUtil.getCalendarBussIdentityKey(template.getBussKey(),us.getBindValues());
            UserCalendar uc = userCalendarService.getUserCalendar(us.getUserId(), bussIdentityKey, new Date());
            if (uc != null) {
                userCalendarService.updateUserCalendarToDate(uc, new Date(), messageId);
            } else {
                uc = new UserCalendar();
                uc.setUserId(us.getUserId());
                uc.setTitle(us.getCalendarTitle());
                uc.setContent(us.getCalendarTitle());
                uc.setDelays(0);
                String calendarTime = us.getCalendarTime();
                if (StringUtil.isEmpty(calendarTime)) {
                    uc.setBussDay(DateUtil.getDate(0));
                    uc.setAllDay(true);
                } else {
                    Date bussDay = DateUtil.addHourMinToDate(null, calendarTime);
                    uc.setBussDay(bussDay);
                    uc.setAllDay(false);
                }
                int rate = 1;
                if (remind.getTriggerType() == TriggerType.MONTH) {
                    rate = 30;
                } else if (remind.getTriggerType() == TriggerType.WEEK) {
                    rate = 7;
                }
                uc.setExpireTime(DateUtil.getDate(remind.getTriggerInterval() * rate));
                uc.setBussIdentityKey(bussIdentityKey);
                uc.setSourceType(BussSource.STAT);
                uc.setSourceId(us.getStatId());
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
            para = new UserStatRemindJobPara();
        }
        return DEFAULT_SUCCESS_PARA_CHECK;
    }

    @Override
    public Class getParaDefine() {
        return UserStatRemindJobPara.class;
    }
}
