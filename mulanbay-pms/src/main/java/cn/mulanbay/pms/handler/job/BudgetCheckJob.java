package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.BudgetHandler;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.handler.bean.fund.BudgetAmountBean;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.domain.BudgetLog;
import cn.mulanbay.pms.persistent.domain.BudgetTimeline;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.service.BudgetService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.pms.util.bean.PeriodDateBean;
import cn.mulanbay.schedule.para.ParaCheckResult;
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
 * 每天定时检查月度、年度预算
 * 确保预算在可控范围内
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class BudgetCheckJob extends AbstractBaseJob {

    private static final Logger logger = LoggerFactory.getLogger(BudgetCheckJob.class);

    private BudgetCheckJobPara para;

    BudgetService budgetService;

    BudgetHandler budgetHandler;

    NotifyHandler notifyHandler;

    @Override
    public TaskResult doTask() {
        TaskResult tr = new TaskResult();
        budgetService = BeanFactoryUtil.getBean(BudgetService.class);
        budgetHandler = BeanFactoryUtil.getBean(BudgetHandler.class);
        notifyHandler = BeanFactoryUtil.getBean(NotifyHandler.class);

        Date bussDay = this.getBussDay();
        //总天数
        int tds;
        //已经过去天数
        int pds;
        if (para.getPeriod() == PeriodType.MONTHLY) {
            //当月总天数
            tds = DateUtil.getMonthDays(bussDay);
            //当月已经过去几天
            pds = DateUtil.getDayOfMonth(bussDay);
        } else {
            //当年总天数
            tds = DateUtil.getDays(DateUtil.getYear(bussDay));
            //当年已经过去几天
            pds = DateUtil.getDayOfYear(bussDay);
        }
        boolean notifyMessge = true;

        if (pds * 100 / tds < para.getCheckFromRate()) {
            notifyMessge = false;
        }

        List<Budget> list = budgetService.getActiveUserBudget(null, null);
        if (list.isEmpty()) {
            tr.setResult(JobResult.SKIP);
            tr.setComment("没有需要检查的预算");
            return tr;
        }
        List<Budget> usList = new ArrayList<>();
        long cuserId = list.get(0).getUserId();
        int n = list.size();
        int users = 0;
        for (int i = 0; i < n; i++) {
            Budget ubs = list.get(i);
            if (ubs.getUserId().longValue() == cuserId) {
                usList.add(ubs);
            } else {
                //当前用户结束
                handle(usList, bussDay, tds, pds, notifyMessge);
                usList = new ArrayList<>();
                usList.add(ubs);
                cuserId = ubs.getUserId();
                users++;
            }
            if (i == n - 1) {
                handle(usList, bussDay, tds, pds, notifyMessge);
            }
        }
        tr.setResult(JobResult.SUCCESS);
        tr.setComment("一共检查了" + (users + 1) + "个用户的预算");
        return tr;
    }

    private void handle(List<Budget> usList, Date bussDay, int tds, int pds, boolean notifyMessge) {
        if (para.getPeriod() == PeriodType.MONTHLY) {
            handleMonth(usList, bussDay, tds, pds, notifyMessge);
        } else {
            handleYear(usList, bussDay, tds, pds, notifyMessge);
        }
    }

    /**
     * 月预算
     * @param usList
     * @param bussDay
     * @param tds
     * @param pds
     * @param notifyMessge
     */
    private void handleMonth(List<Budget> usList, Date bussDay, int tds, int pds, boolean notifyMessge) {
        try {
            boolean redo = this.isRedo();
            Long userId = usList.get(0).getUserId();
            BudgetAmountBean bab = budgetHandler.calcBudgetAmount(usList, bussDay);
            BigDecimal budgetAmount = bab.getMonthBudget();
            PeriodType period = PeriodType.MONTHLY;
            PeriodDateBean pdb = BussUtil.calPeriod(bussDay,period);
            Date startDate = pdb.getStartDate();
            Date endDate = pdb.getEndDate();
            if(redo){
                //如果是重做，那么需要设置为当前运营日的时间
                endDate = bussDay;
            }
            BudgetLog bl = budgetHandler.statBudget(userId, budgetAmount, startDate, endDate, pdb.getBussKey(), redo, period);
            //增加时间线流水
            BudgetTimeline timeline = new BudgetTimeline();
            BeanCopy.copy(bl, timeline);
            timeline.setCreatedTime(null);
            timeline.setModifyTime(null);
            timeline.setBussDay(bussDay);
            timeline.setTotalDays(tds);
            timeline.setPassDays(pds);
            String bussKey = pdb.getBussKey();
            timeline.setBussKey(bussKey);
            timeline.setTimelineId(null);
            budgetService.saveBudgetTimeline(timeline, redo);

            if (!notifyMessge) {
                return;
            }
            //发送提醒
            BigDecimal totalConsumeAmount = bl.getBcAmount().add(bl.getNcAmount()).add(bl.getTrAmount());
            String title = "[" + pdb.getBussKey() + "]";
            String content = "月度消费预算" + NumberUtil.getValue(budgetAmount,SCALE) + "元，";
            content += "截止[" + DateUtil.getFormatDate(endDate, DateUtil.FormatDay1) + "]已经花费" + NumberUtil.getValue(totalConsumeAmount,SCALE) + "元，";
            if (totalConsumeAmount.compareTo(bl.getBudgetAmount())<=0) {
                title += "月度消费预测";
            } else {
                title += "月度消费已超预算";
            }
            if(para.isPredict()){
                //预测
                int month = DateUtil.getMonth(bussDay);
                Double monthRate = budgetHandler.predictMonthRate(userId,month,null,tds,false);
                BigDecimal predictAmount = monthRate==null ? null : budgetAmount.multiply(new BigDecimal(monthRate));
                content += "预计本月总消费" + NumberUtil.getValue(predictAmount, SCALE) + "元。";
            }
            notifyHandler.addMessage(PmsCode.BUDGET_CHECK, title, content,
                    bl.getUserId(), null);
        } catch (Exception e) {
            logger.error("处理bussDay=" + bussDay + "的预算异常", e);
        }
    }

    private void handleYear(List<Budget> usList, Date bussDay, int tds, int pds, boolean notifyMessge) {
        try {
            boolean redo = this.isRedo();
            Long userId = usList.get(0).getUserId();
            BudgetAmountBean bab = budgetHandler.calcBudgetAmount(usList, bussDay);
            BigDecimal budgetAmount = bab.getYearBudget();
            PeriodType period = PeriodType.YEARLY;
            PeriodDateBean pdb = BussUtil.calPeriod(bussDay,period);
            Date startDate = pdb.getStartDate();
            Date endDate = pdb.getEndDate();
            if(redo){
                //如果是重做，那么需要设置为当前运营日的时间
                endDate = bussDay;
            }
            BudgetLog bl = budgetHandler.statBudget(userId, budgetAmount, startDate, endDate, pdb.getBussKey(), redo, period);
            //增加时间线流水
            BudgetTimeline timeline = new BudgetTimeline();
            BeanCopy.copy(bl, timeline);
            timeline.setCreatedTime(null);
            timeline.setModifyTime(null);
            timeline.setBussDay(bussDay);
            timeline.setTotalDays(tds);
            timeline.setPassDays(pds);
            timeline.setBussKey(pdb.getBussKey());
            timeline.setTimelineId(null);
            budgetService.saveBudgetTimeline(timeline, this.isRedo());

            if (!notifyMessge) {
                return;
            }
            //发送提醒
            BigDecimal totalConsumeAmount = bl.getBcAmount().add(bl.getNcAmount()).add(bl.getTrAmount());
            String title = "[" + pdb.getBussKey() + "]";
            String content = "年度消费预算" + NumberUtil.getValue(budgetAmount,SCALE) + "元，";
            content += "截止[" + DateUtil.getFormatDate(endDate, DateUtil.FormatDay1) + "]已经花费" + NumberUtil.getValue(totalConsumeAmount,SCALE) + "元，";
            if (totalConsumeAmount.compareTo(bl.getBudgetAmount())<=0) {
                title += "年度消费预测";
            } else {
                title += "年度消费已超预算";
            }
            if(para.isPredict()){
                //预测
                Double yearRate = budgetHandler.predictYearRate(userId,null,tds,false);
                BigDecimal predictAmount = yearRate==null ? null : budgetAmount.multiply(new BigDecimal(yearRate));
                content += "预计年月总消费" + NumberUtil.getValue(predictAmount, SCALE) + "元。";
            }
            notifyHandler.addMessage(PmsCode.BUDGET_CHECK, title, content,
                    bl.getUserId(), null);
        } catch (Exception e) {
            logger.error("处理bussDay=" + bussDay + "的预算异常", e);
        }
    }


    @Override
    public ParaCheckResult checkTriggerPara() {
        para = this.getTriggerParaBean();
        return DEFAULT_SUCCESS_PARA_CHECK;
    }

    @Override
    public Class getParaDefine() {
        return BudgetCheckJobPara.class;
    }
}
