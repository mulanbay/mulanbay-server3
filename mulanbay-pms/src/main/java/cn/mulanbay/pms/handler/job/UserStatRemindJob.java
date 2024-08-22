package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.handler.ReportHandler;
import cn.mulanbay.pms.handler.RewardHandler;
import cn.mulanbay.pms.handler.UserStatHandler;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.dto.report.StatResultDTO;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.CompareType;
import cn.mulanbay.pms.persistent.enums.ResultType;
import cn.mulanbay.pms.persistent.enums.UserCalendarFinishType;
import cn.mulanbay.pms.persistent.service.StatService;
import cn.mulanbay.pms.persistent.service.UserCalendarService;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.schedule.para.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.enums.TriggerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static cn.mulanbay.pms.common.Constant.*;

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

    ReportHandler reportHandler = null;

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
        reportHandler = BeanFactoryUtil.getBean(ReportHandler.class);
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
            //Step 1: 查询是否要通知及是否已经达到时间比例
            if (!us.getRemind()) {
                return true;
            }
            Long userId = us.getUserId();

            //Step 2: 第一步先判断是否已经通知过
            String key = CacheKey.getKey(CacheKey.USER_STAT_NOTIFY, userId.toString(), us.getStatId().toString());
            String cs = cacheHandler.getForString(key);
            if (cs != null) {
                logger.debug("用户ID=" + userId + "的统计[" + us.getTitle() + "],id=" + us.getStatId() + "已经提醒过了");
                return true;
            }
            StatResultDTO resultDTO = statService.getStatResult(us);;
            if (para.getCacheResult()) {
                userStatHandler.cacheStatResult(resultDTO, para.getExpireSeconds());
            }

            //step 3: 记录时间线日志
            this.addTimeline(resultDTO);

            // 没有数据,无法计算比例
            if(resultDTO.getValue()==null||resultDTO.getValue()==0){
                logger.debug("用户ID=" + userId + "的统计[" + us.getTitle() + "],id=" + us.getStatId() + "的统计值为空或者为零，不需要提醒");
                return true;
            }

            //step 4: 比较，通知
            UserStatRemind unr = statService.getUserStatRemind(us.getStatId(),userId);
            String title = null;
            UserStat userStat = resultDTO.getUserStat();
            StatTemplate template = userStat.getTemplate();
            String unit = template.getValueTypeName();
            String content = null;
            if (template.getResultType() == ResultType.DATE_NAME || template.getResultType() == ResultType.NUMBER_NAME) {
                content = "[" + userStat.getTitle() + "][" + resultDTO.getNameValue() + "]期望值[" + us.getExpectValue() + "],实际值为["
                        + resultDTO.getStatValue() + "],计量单位:[" + unit + "]\n";
            } else {
                content = "[" + userStat.getTitle() + "]期望值[" + us.getExpectValue() + "],实际值为["
                        + resultDTO.getStatValue() + "],计量单位:[" + unit + "]\n";
            }
            content +=",期望类型:"+us.getCompareType().getName();
            boolean complete = true;
            if(us.getCompareType()==CompareType.MORE &&resultDTO.getStatValue()<us.getExpectValue()){
                //需要大于且没有达到期望
                title = "[" + userStat.getTitle() + "]未达到报警";
                complete = false;
            }
            if(us.getCompareType()==CompareType.LESS &&resultDTO.getStatValue()>us.getExpectValue()){
                //需要小于且超出期望
                title = "[" + userStat.getTitle() + "]超出报警";
                complete = false;
            }
            if(!complete){
                this.notifyMessage(title,content,unr);
            }else{
                logger.debug("用户ID=" + userId + "的统计[" + userStat.getTitle() + "],id=" + userStat.getStatId() + "不需要提醒");
                rewardPoint(userStat, true, null,content);
            }
            return true;
        } catch (Exception e) {
            logger.error("处理用户统计配置:" + us.getTitle() + "异常", e);
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

    /**
     * 未达到
     *
     * @param title
     * @param content
     * @param remind
     * @return
     */
    private Long notifyMessage(String title, String content, UserStatRemind remind) {
        content = content + "统计日期:" + DateUtil.getFormatDate(this.getBussDay(), DateUtil.FormatDay1);
        RemindTimeBean bean = this.calcRemindExpectTime(remind.getTriggerInterval(), remind.getTriggerType(), remind.getLastRemindTime(), remind.getRemindTime());
        //Step 1: 发送消息通知
        Long messageId = notifyHandler.addMessage(PmsCode.USER_STAT, title, content,
                remind.getUserId(), bean.getNextRemindTime());
        //Step 2: 更新最后的提醒时间
        statService.updateLastRemindTime(remind.getRemindId(), new Date());
        /**
         * Step 3: 设置提醒的缓存，避免下一次重复提醒
         * 需要在这里设置上一次的提醒缓存，不能再handleUserPlanRemind方法里设置
         * 因为只有触发了提醒条件才能告知下一次是否能提醒
         */
        String key = CacheKey.getKey(CacheKey.USER_STAT_NOTIFY, remind.getUserId().toString(), remind.getStat().getStatId().toString());
        //失效时间为通知周期的秒数，-5为了保证第二次通知时间点job能执行
        cacheHandler.set(key, "123",bean.getDays() * DAY_SECONDS - 5);
        //Step 4: 更新积分
        rewardPoint(remind.getStat(), false, messageId,content);
        //Step 5: 加入用户日历
        addToUserCalendar(remind, messageId);
        return messageId;
    }

    /**
     * 更新积分，完成+，未达到要求减
     *
     * @param us
     */
    private void rewardPoint(UserStat us, boolean isComplete, Long messageId,String content) {
        try {
            int radio = 1;
            int rewards = us.getTemplate().getRewards() * radio;
            String remark = "用户统计配置[" + us.getTitle() + "]达到要求奖励,"+content;
            if (!isComplete) {
                rewards = -rewards;
                remark = "用户统计配置[" + us.getTitle() + "]触发警报惩罚,"+content;
            }
            rewardHandler.reward(us.getUserId(), rewards, us.getStatId(), BussSource.STAT, remark, messageId);
            if (isComplete) {
                StatTemplate template = us.getTemplate();
                String bindKey = reportHandler.createBindValueKey(us.getBindValues());
                String bussIdentityKey = BussUtil.getCalendarBussIdentityKey(template.getSource(),bindKey);
                userCalendarService.updateUserCalendarForFinish(us.getUserId(), bussIdentityKey, new Date(), UserCalendarFinishType.AUTO,us.getStatId(), BussSource.STAT, messageId);
            }
        } catch (Exception e) {
            logger.error("用户统计配置[" + us.getTitle() + "]积分奖励异常", e);
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
            String bindKey = reportHandler.createBindValueKey(us.getBindValues());
            String bussIdentityKey = BussUtil.getCalendarBussIdentityKey(template.getSource(),bindKey);
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
                    Date bussDay = DateUtil.addHourMin(null, calendarTime);
                    uc.setBussDay(bussDay);
                    uc.setAllDay(false);
                }
                int rate = 1;
                if (remind.getTriggerType() == TriggerType.MONTH) {
                    rate = MONTH_DAY;
                } else if (remind.getTriggerType() == TriggerType.WEEK) {
                    rate = WEEK_DAY;
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
