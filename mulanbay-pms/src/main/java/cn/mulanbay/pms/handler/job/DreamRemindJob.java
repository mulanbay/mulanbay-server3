package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.handler.RewardHandler;
import cn.mulanbay.pms.persistent.domain.Dream;
import cn.mulanbay.pms.persistent.domain.DreamRemind;
import cn.mulanbay.pms.persistent.domain.UserCalendar;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.service.DreamService;
import cn.mulanbay.pms.persistent.service.UserCalendarService;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 统计用户梦想完成进度的调度
 * 如果进度达不到要求（和时间的进度想比），往消息表写一条待发送记录
 * 一般为每天凌晨统计，根据用户配置的提醒时间设置expectSendTime值
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class DreamRemindJob extends AbstractBaseRemindJob {

    private static final Logger logger = LoggerFactory.getLogger(DreamRemindJob.class);

    DreamService dreamService = null;

    NotifyHandler notifyHandler = null;

    CacheHandler cacheHandler = null;

    RewardHandler rewardHandler = null;

    UserCalendarService userCalendarService = null;

    @Override
    public TaskResult doTask() {
        TaskResult result = new TaskResult();
        dreamService = BeanFactoryUtil.getBean(DreamService.class);
        notifyHandler = BeanFactoryUtil.getBean(NotifyHandler.class);
        cacheHandler = BeanFactoryUtil.getBean(CacheHandler.class);
        rewardHandler = BeanFactoryUtil.getBean(RewardHandler.class);
        userCalendarService = BeanFactoryUtil.getBean(UserCalendarService.class);
        List<Dream> list = dreamService.getNeedRemindDream();
        if (list.isEmpty()) {
            result.setComment("没有需要提醒的用户计划");
        } else {
            for (Dream dream : list) {
                handleDreamRemind(dream);
            }
            result.setResult(JobResult.SUCCESS);
        }
        return result;
    }

    private void handleDreamRemind(Dream dream) {
        try {
            if (!dream.getRemind()) {
                return;
            }
            Long userId = dream.getUserId();
            // 第一步先判断是否已经通知过
            String cs = cacheHandler.getForString("dreamNotify:" + userId + ":" + dream.getDreamId());
            if (cs != null) {
                logger.debug("用户ID=" + userId + "的梦想[" + dream.getDreamName() + "],id=" + dream.getDreamId() + "已经提醒过了");
                return;
            }
            DreamRemind remind = dreamService.getRemind(dream.getDreamId());
            // 通过缓存查询上一次提醒时间
            //需要用运营日计算，比如2017-12-01号调度的，应该是用2017-11-30号计算
            Date date = this.getBussDay();
            if (dream.getRate() >= remind.getFromRate()) {
                logger.debug("用户ID=" + userId + "的梦想[" + dream.getDreamName() + "],id=" + dream.getDreamId() + "进度已经达到要求");
                return;
            }
            int fpd = DateUtil.getIntervalDays(date, dream.getExpectDate());
            if (fpd >= remind.getFromExpectDays()) {
                logger.debug("用户ID=" + userId + "的梦想[" + dream.getDreamName() + "],id=" + dream.getDreamId() + "还未达到预期提醒时间");
                return;
            }
            String title = "梦想[" + dream.getDreamName() + "]提醒";
            StringBuffer content = new StringBuffer();
            content.append("你的梦想[" + dream.getDreamName() + "]进度落后了,\n");
            content.append("当前进度[" + dream.getRate() + "]%,设置提醒进度[" + remind.getFromRate() + "]%,\n");
            content.append("距离期望完成日期[" + DateUtil.getFormatDate(dream.getExpectDate(), DateUtil.FormatDay1) + "]还剩[" + fpd + "]天\n");
            this.notifyMessage(title, content.toString(), remind, false);

        } catch (Exception e) {
            logger.error("处理用户梦想[" + dream.getDreamName() + "]异常", e);
        }
    }

    /**
     * 更新积分，完成+，未达到要求减
     *
     * @param dream
     * @param isComplete
     */
    private void rewardPoint(Dream dream, boolean isComplete, Long messageId) {
        try {
            int radio = 0;
            //未完成减去
            int rewards = dream.getRewards() * (-1);
            String remark = "梦想[" + dream.getDreamName() + "]进度未达到要求惩罚";
            rewardHandler.reward(dream.getUserId(), rewards, dream.getDreamId(), BussSource.DREAM, remark, messageId);
            //添加到用户日历
            addToUserCalendar(dream, messageId);
        } catch (Exception e) {
            logger.error("梦想[" + dream.getDreamName() + "]积分奖励异常", e);
        }
    }

    /**
     * 更新到用户日历
     *
     * @param dream
     */
    private void addToUserCalendar(Dream dream, Long messageId) {
        try {
            String bussIdentityKey = BussUtil.getCalendarBussIdentityKey(BussSource.DREAM,dream.getDreamId().toString());
            UserCalendar uc = userCalendarService.getUserCalendar(dream.getUserId(), bussIdentityKey, new Date());
            if (uc != null) {
                userCalendarService.updateUserCalendarToDate(uc, new Date(), messageId);
            } else {
                uc = new UserCalendar();
                uc.setUserId(dream.getUserId());
                uc.setTitle("梦想进度落后");
                uc.setContent("梦想[" + dream.getDreamName() + "]进度落后");
                uc.setDelays(0);
                uc.setBussDay(DateUtil.getDate(0));
                uc.setBussIdentityKey(bussIdentityKey);
                uc.setSourceType(BussSource.DREAM);
                uc.setSourceId(dream.getDreamId());
                uc.setMessageId(messageId);
                uc.setExpireTime(DateUtil.getMonthLast(uc.getBussDay()));
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
    private void notifyMessage(String title, String content, DreamRemind remind, boolean isComplete) {
        Dream dream = remind.getDream();
        RemindTimeBean bean = this.calcRemindExpectTime(remind.getTriggerInterval(), remind.getTriggerType(), remind.getLastRemindTime(), remind.getRemindTime());
        Long messageId;
        if (!isComplete) {
            messageId = notifyHandler.addMessage(PmsCode.USER_DREAM_UN_COMPLETED_STAT, title, content,
                    dream.getUserId(), bean.getNextRemindTime());
        } else {
            messageId = notifyHandler.addMessage(PmsCode.USER_DREAM_COMPLETED_STAT, title, content,
                    dream.getUserId(), bean.getNextRemindTime());
        }
        // 更新最后的提醒时间
        dreamService.updateLastRemindTime(remind.getRemindId(), new Date());
        Date nextRemindTime = bean.getNextRemindTime();
        // 设置提醒的缓存
        long expiredSecond = (nextRemindTime.getTime() - System.currentTimeMillis()) / 1000 + bean.getDays() * 24 * 3600;
        if (expiredSecond > 0) {
            cacheHandler.set("dreamNotify:" + remind.getUserId() + ":" + dream.getDreamId(), "123", (int) (expiredSecond));
        } else {
            logger.error("不合法的缓存失效时间:" + expiredSecond);
        }
        //积分奖励
        rewardPoint(dream, isComplete, messageId);
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
