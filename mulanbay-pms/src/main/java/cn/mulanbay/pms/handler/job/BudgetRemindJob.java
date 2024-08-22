package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.BudgetHandler;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.handler.RewardHandler;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.domain.UserCalendar;
import cn.mulanbay.pms.persistent.dto.consume.ConsumeBudgetStat;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.service.UserCalendarService;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.pms.web.bean.req.fund.budget.BudgetSH;
import cn.mulanbay.schedule.para.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static cn.mulanbay.pms.common.Constant.SCALE;

/**
 * 预算提醒
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class BudgetRemindJob extends AbstractBaseJob {

    private static final Logger logger = LoggerFactory.getLogger(BudgetRemindJob.class);

    BudgetRemindJobPara para;

    @Override
    public TaskResult doTask() {
        TaskResult tr = new TaskResult();
        //step 1:获取需要提醒的预算列表
        BudgetSH sf = new BudgetSH();
        sf.setStatus(CommonStatus.ENABLE);
        sf.setRemind(true);
        BaseService baseService = BeanFactoryUtil.getBean(BaseService.class);
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(Budget.class);
        List<Budget> list = baseService.getBeanList(pr);
        BudgetHandler budgetHandler = BeanFactoryUtil.getBean(BudgetHandler.class);
        //step 2:根据日志查询是否已经完成
        Date bussDay = this.getBussDay();
        for (Budget bd : list) {
            ConsumeBudgetStat bs= budgetHandler.getActualAmount(bd,bussDay);
            if (bs.getTotalPrice() == null) {
                //step 3:发送提醒信息
                String bussKey = BussUtil.getBussKey(bd.getPeriod(), bussDay);
                handleNotifyBudget(bd, bussKey,bussDay);
            }
        }
        tr.setResult(JobResult.SUCCESS);
        tr.setComment("一共统计了" + list.size() + "个预算提醒");
        return tr;
    }

    private void handleNotifyBudget(Budget bd, String bussKey,Date bussDay) {
        try {
            BudgetHandler budgetHandler = BeanFactoryUtil.getBean(BudgetHandler.class);
            Integer n = budgetHandler.getLeftDays(bd, bussDay);
            if (n != null && n > para.getMinDays()) {
                logger.debug("预算[" + bd.getBudgetName() + "]还未到提醒时间。");
                return;
            }
            String title = "预算[" + bd.getBudgetName() + "]未完成";
            StringBuffer content = new StringBuffer();

            content.append("预算[" + bd.getBudgetName() + "]在" + bussKey + "未完成。");
            content.append("预算金额:" + NumberUtil.getValue( bd.getAmount(),SCALE) + "元。");
            content.append("请及时完成且记录日志。");
            NotifyHandler notifyHandler = BeanFactoryUtil.getBean(NotifyHandler.class);
            //发送消息
            String cc = content.toString();
            Long messageId = notifyHandler.addMessage(PmsCode.BUDGET_UN_COMPLETED, title, cc,
                    bd.getUserId(), null);
            //增加积分
            RewardHandler rewardHandler = BeanFactoryUtil.getBean(RewardHandler.class);
            //TODO 目前预算与预算日志无法区分，要么BussSource区分不同的
            rewardHandler.reward(bd.getUserId(), para.getScore(), bd.getBudgetId(), BussSource.BUDGET, null, messageId);
            this.addToUserCalendar(bd, bussKey,bussDay, messageId, cc);
        } catch (Exception e) {
            logger.error("处理预算统计消息提醒异常");
        }

    }

    /**
     * 更新到用户日历
     *
     * @param bd
     */
    private void addToUserCalendar(Budget bd, String bussKey,Date bussDay, Long messageId, String content) {
        try {
            UserCalendarService userCalendarService = BeanFactoryUtil.getBean(UserCalendarService.class);
            String bussIdentityKey = BussUtil.getCalendarBussIdentityKey(BussSource.BUDGET,bd.getBudgetId().toString());
            UserCalendar uc = userCalendarService.getUserCalendar(bd.getUserId(), bussIdentityKey, new Date());
            if (uc != null) {
                userCalendarService.updateUserCalendarToDate(uc, new Date(), messageId);
            } else {
                uc = new UserCalendar();
                uc.setUserId(bd.getUserId());
                uc.setTitle("及时完成预算");
                uc.setContent(content);
                uc.setDelays(0);
                uc.setBussDay(bussDay);
                uc.setExpireTime(DateUtil.getMonthLast(bussDay));
                uc.setBussIdentityKey(bussIdentityKey);
                uc.setSourceType(BussSource.BUDGET);
                uc.setSourceId(bd.getBudgetId());
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
            para = new BudgetRemindJobPara();
        }
        return DEFAULT_SUCCESS_PARA_CHECK;
    }

    @Override
    public Class getParaDefine() {
        return BudgetRemindJobPara.class;
    }
}
