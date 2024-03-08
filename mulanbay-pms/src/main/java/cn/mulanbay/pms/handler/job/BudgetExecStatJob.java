package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.BudgetHandler;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.handler.RewardHandler;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.domain.BudgetLog;
import cn.mulanbay.pms.persistent.domain.UserCalendar;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.service.BudgetService;
import cn.mulanbay.pms.persistent.service.UserCalendarService;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.mulanbay.pms.common.Constant.SCALE;

/**
 * 预算执行统计
 * 统计月度预算的执行情况
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class BudgetExecStatJob extends AbstractBaseJob {

    private static final Logger logger = LoggerFactory.getLogger(BudgetExecStatJob.class);

    private BudgetExecStatJobPara para;

    BudgetService budgetService;

    BudgetHandler budgetHandler;

    RewardHandler rewardHandler;

    @Override
    public TaskResult doTask() {
        TaskResult tr = new TaskResult();
        //step 1:查询预算
        budgetService = BeanFactoryUtil.getBean(BudgetService.class);
        budgetHandler = BeanFactoryUtil.getBean(BudgetHandler.class);
        rewardHandler = BeanFactoryUtil.getBean(RewardHandler.class);
        List<Budget> list = budgetService.getActiveUserBudget(null, null);
        if (list.isEmpty()) {
            tr.setResult(JobResult.SKIP);
            tr.setComment("没有需要统计的预算");
            return tr;
        }
        Date bussDay = this.getBussDay();
        String bussKey = BussUtil.getBussKey(para.getPeriod(), bussDay);
        boolean isRedo = this.isRedo();
        List<Budget> usList = new ArrayList<>();
        long cuserId = list.get(0).getUserId();
        int n = list.size();
        for (int i = 0; i < n; i++) {
            Budget ubs = list.get(i);
            if (ubs.getUserId().longValue() == cuserId) {
                usList.add(ubs);
            } else {
                //当前用户结束
                handle(usList, bussKey, isRedo, bussDay);
                usList = new ArrayList<>();
                usList.add(ubs);
                cuserId = ubs.getUserId();
            }
            if (i == n - 1) {
                handle(usList, bussKey, isRedo, bussDay);
            }
        }
        tr.setResult(JobResult.SUCCESS);
        tr.setComment("一共统计了" + list.size() + "个用户的预算执行情况");
        return tr;
    }

    private void handle(List<Budget> usList, String bussKey, boolean isRedo, Date bussDay) {
        try {
            Long userId = usList.get(0).getUserId();
            boolean b = budgetService.isBudgetLogExit(bussKey, userId, null, null);
            //判断是否超期,如果是新的
            if (!b) {
                BudgetLog bl = budgetHandler.statAndSaveBudgetLog(usList, userId, bussDay, bussKey, isRedo, para.getPeriod(), false);
                handleConsumeCheck(bl,bussDay);
            } else {
                logger.debug("用户[" + userId + "]的bussKey=" + bussKey + "已经存在，无需再统计");
            }
        } catch (Exception e) {
            logger.error("处理bussKey=" + bussKey + "的预算异常", e);
        }
    }

    private void handleConsumeCheck(BudgetLog bl, Date bussDay) {
        try {
            //算积分及加入用户任务
            String title = "[" + bl.getStatPeriod().getName() + "]预算统计：" + bl.getBussKey();
            StringBuffer content = new StringBuffer();
            BigDecimal totalConsume = bl.getBcAmount().add(bl.getNcAmount()).add(bl.getTrAmount());
            int code;
            int rewards;
            if (totalConsume.compareTo(bl.getBudgetAmount())>0) {
                //超出预算
                content.append(bl.getBussKey() + "预算没控制,实际消费超出预算。\n");
                code = PmsCode.BUDGET_CHECK_OVER;
                rewards = para.getOverBudgetScore();
            } else {
                content.append("实际消费在预算之内。\n");
                code = PmsCode.BUDGET_CHECK_LESS;
                rewards = para.getLessBudgetScore();
            }
            content.append("预算金额:" + NumberUtil.getValue(bl.getBudgetAmount(),SCALE) + "元,");
            content.append("实际消费:" + NumberUtil.getValue(totalConsume,SCALE) + "元。\n");
            content.append("其中普通消费:" + NumberUtil.getValue(bl.getNcAmount(),SCALE) + "元,");
            content.append("突发消费:" + NumberUtil.getValue(bl.getBcAmount(),SCALE) + "元,");
            content.append("看病消费:" + NumberUtil.getValue(bl.getTrAmount(),SCALE) + "元。");
            NotifyHandler notifyHandler = BeanFactoryUtil.getBean(NotifyHandler.class);
            //发送消息
            String cc = content.toString();
            Long messageId = notifyHandler.addNotifyMessage(code, title, cc,
                    bl.getUserId(), null);
            //增加积分
            rewardHandler.rewardPoints(bl.getUserId(), rewards, bl.getLogId(), BussSource.BUDGET_LOG, null, messageId);
            if (code == PmsCode.BUDGET_CHECK_OVER) {
                this.addToUserCalendar(bl, messageId, cc,bussDay);
            }
        } catch (Exception e) {
            logger.error("处理预算统计消息提醒异常");
        }

    }

    /**
     * 更新到用户日历
     *
     * @param bl
     */
    private void addToUserCalendar(BudgetLog bl, Long messageId, String content, Date bussDay) {
        try {
            UserCalendarService userCalendarService = BeanFactoryUtil.getBean(UserCalendarService.class);
            String bussIdentityKey = BussUtil.getCalendarBussIdentityKey(BussSource.BUDGET_LOG,bl.getBussKey());
            UserCalendar uc = userCalendarService.getUserCalendar(bl.getUserId(), bussIdentityKey, new Date());
            if (uc != null) {
                userCalendarService.updateUserCalendarToDate(uc, new Date(), messageId);
            } else {
                uc = new UserCalendar();
                uc.setUserId(bl.getUserId());
                uc.setTitle("注意花费");
                uc.setContent(content);
                uc.setDelays(0);
                uc.setBussDay(bussDay);
                uc.setExpireTime(DateUtil.getMonthLast(bussDay));
                uc.setBussIdentityKey(bussIdentityKey);
                uc.setSourceType(BussSource.BUDGET_LOG);
                uc.setSourceId(bl.getLogId());
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
        return DEFAULT_SUCCESS_PARA_CHECK;
    }

    @Override
    public Class getParaDefine() {
        return BudgetExecStatJobPara.class;
    }
}
