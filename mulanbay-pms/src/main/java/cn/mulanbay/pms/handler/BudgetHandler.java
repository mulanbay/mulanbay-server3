package cn.mulanbay.pms.handler;

import cn.mulanbay.ai.ml.processor.BudgetConsumeMEvaluateProcessor;
import cn.mulanbay.ai.ml.processor.BudgetConsumeYEvaluateProcessor;
import cn.mulanbay.ai.ml.processor.bean.BudgetConsumeER;
import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.pms.handler.bean.consume.ConsumeBean;
import cn.mulanbay.pms.handler.bean.fund.BudgetAmountBean;
import cn.mulanbay.pms.handler.bean.fund.FundStatBean;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.domain.BudgetLog;
import cn.mulanbay.pms.persistent.dto.consume.ConsumeBudgetStat;
import cn.mulanbay.pms.persistent.dto.consume.ConsumeConsumeTypeStat;
import cn.mulanbay.pms.persistent.dto.fund.IncomeSummaryStat;
import cn.mulanbay.pms.persistent.enums.BudgetLogSource;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.GoodsConsumeType;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.service.BudgetService;
import cn.mulanbay.pms.persistent.service.ConsumeService;
import cn.mulanbay.pms.persistent.service.IncomeService;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.pms.util.bean.PeriodDateBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 预算处理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class BudgetHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(BudgetHandler.class);

    @Autowired
    ConsumeService consumeService;

    @Autowired
    BudgetService budgetService;

    @Autowired
    IncomeService incomeService;

    @Autowired
    UserScoreHandler userScoreHandler;

    @Autowired
    BudgetConsumeMEvaluateProcessor budgetConsumeMEvaluateProcessor;

    @Autowired
    BudgetConsumeYEvaluateProcessor budgetConsumeYEvaluateProcessor;

    public BudgetHandler() {
        super("预算处理");
    }

    /**
     * 计算预算
     *
     * @param budgetList
     * @return
     */
    public BudgetAmountBean calcBudgetAmount(List<Budget> budgetList, Date now) {
        BudgetAmountBean bab = new BudgetAmountBean();
        for (Budget b : budgetList) {
            if (b.getStatus() == CommonStatus.DISABLE) {
                continue;
            }
            int monthFactor = BussUtil.getFactor(PeriodType.MONTHLY,now,b);
            int yearFactor = BussUtil.getFactor(PeriodType.YEARLY,now,b);
            if(monthFactor>0){
                bab.addMonthBudget(b.getAmount().multiply(new BigDecimal(monthFactor)));
                bab.addMonthBudget(b);
            }
            if(yearFactor>0){
                bab.addYearBudget(b.getAmount().multiply(new BigDecimal(yearFactor)));
                bab.addYearBudget(b);
            }
        }
        return bab;
    }

    /**
     * 获取剩余时间
     *
     * @param bg
     * @param now
     * @return
     */
    public Integer getLeftDays(Budget bg, Date now) {
        Date nextPayTime = this.getNextPayTime(bg, now);
        if (nextPayTime == null) {
            return null;
        } else {
            return DateUtil.getIntervalDays(now, nextPayTime);
        }
    }

    /**
     * 预测月度消费比例
     * @param userId
     * @param month
     * @param dayIndex
     * @return
     */
    public Double predictMonthRate(Long userId,int month,Integer score,int dayIndex,Boolean needOutBurst){
        try {
            if(score==null){
                score = userScoreHandler.getLatestScore(userId);
            }
            BudgetConsumeER er = budgetConsumeMEvaluateProcessor.evaluateMulti(month,score,dayIndex);
            Double v = null;
            if(needOutBurst){
                v = er.getBcRate();
            }else{
                v = er.getNcRate();
            }
            return v;
        } catch (Exception e) {
            logger.error("预测月度消费比例异常",e);
            return null;
        }
    }

    /**
     * 预测年度消费
     * @param userId
     * @param dayIndex
     * @return
     */
    public Double predictYearRate(Long userId,Integer score,int dayIndex,Boolean needOutBurst){
        try {
            if(score==null){
                score = userScoreHandler.getLatestScore(userId);
            }
            BudgetConsumeER er = budgetConsumeYEvaluateProcessor.evaluateMulti(score,dayIndex);
            Double v = null;
            if(needOutBurst){
                v = er.getBcRate();
            }else{
                v = er.getNcRate();
            }
            return v;
        } catch (Exception e) {
            logger.error("预测年度消费比例异常",e);
            return null;
        }
    }

    /**
     * 实际支付金额
     *
     * @param budget
     * @return
     */
    public ConsumeBudgetStat getActualAmount(Budget budget, Date bussDay) {
        ConsumeBudgetStat v = null;
        PeriodDateBean pdb = BussUtil.calPeriod(bussDay,budget.getPeriod());
        if (budget.getGoodsTypeId() != null) {
            v = consumeService.statConsumeAmount(pdb.getStartDate(), pdb.getEndDate(), budget.getUserId(), budget.getGoodsTypeId(), budget.getTags(),budget.getIcg());
        }
        return v;
    }

    /**
     * 统计资金
     * @param startTime
     * @param endTime
     * @param userId
     * @return
     */
    public FundStatBean statConsumeIncome(Date startTime,Date endTime, Long userId){
        FundStatBean vo = new FundStatBean();
        ConsumeBean consumeBean = this.getConsume(startTime,endTime,userId);
        vo.setNcAmount(consumeBean.getNcAmount());
        vo.setBcAmount(consumeBean.getBcAmount());
        vo.setTrAmount(consumeBean.getBcAmount());
        vo.setTotalConsume(consumeBean.getTotalAmount());
        vo.setConsumeCount(consumeBean.getTotalCount());
        //收入
        IncomeSummaryStat iss = incomeService.incomeSummaryStat(userId, startTime, endTime);
        vo.setIncome(iss.getTotalAmount() == null ? new BigDecimal(0) : iss.getTotalAmount());

        return vo;
    }

    /**
     * 获取下一次支付时间
     *
     * @param bg
     * @param now
     * @return
     */
    public Date getNextPayTime(Budget bg, Date now) {
        if (bg.getExpectPaidTime() == null) {
            return null;
        }
        PeriodType period = bg.getPeriod();
        if (period == PeriodType.ONCE) {
            return bg.getExpectPaidTime();
        } else if (period == PeriodType.DAILY) {
            String date = DateUtil.getFormatDate(bg.getExpectPaidTime(), "yyyy-MM-99 HH:mm:ss");
            String dd = DateUtil.getFormatDate(now, "dd");
            String newDate = date.replaceAll("99", dd);
            return DateUtil.getDate(newDate, DateUtil.Format24Datetime);
        } else if (period == PeriodType.MONTHLY) {
            //取几号
            String date = DateUtil.getFormatDate(bg.getExpectPaidTime(), "9999-99-dd HH:mm:ss");
            String dd = DateUtil.getFormatDate(now, "yyyy-MM");
            String newDate = date.replaceAll("9999-99", dd);
            return DateUtil.getDate(newDate, DateUtil.Format24Datetime);
        } else if (period == PeriodType.YEARLY) {
            //取几月几号
            String date = DateUtil.getFormatDate(bg.getExpectPaidTime(), "9999-MM-dd HH:mm:ss");
            String dd = DateUtil.getFormatDate(now, "yyyy");
            String newDate = date.replaceAll("9999", dd);
            return DateUtil.getDate(newDate, DateUtil.Format24Datetime);
        }
        return null;
    }

    /**
     * 获取消费记录
     *
     * @param startTime
     * @param endTime
     * @param userId
     * @return
     */
    public ConsumeBean getConsume(Date startTime, Date endTime, Long userId) {
        ConsumeBean bean = new ConsumeBean();
        long totalCount=0L;
        //总消费
        List<ConsumeConsumeTypeStat> brcList = consumeService.getConsumeTypeAmountStat(startTime, endTime, userId);
        for (ConsumeConsumeTypeStat brc : brcList) {
            if (brc.getConsumeType().intValue() == GoodsConsumeType.NORMAL.getValue()) {
                bean.setNcAmount(brc.getTotalPrice());
            } else if (brc.getConsumeType().intValue() == GoodsConsumeType.OUTBURST.getValue()) {
                bean.setBcAmount(brc.getTotalPrice());
            } else if (brc.getConsumeType().intValue() == GoodsConsumeType.TREAT.getValue()) {
                bean.setTreatAmount(brc.getTotalPrice());
            }
            totalCount+= brc.getTotalCount();
        }
        bean.setTotalCount(totalCount);
        return bean;
    }

    /**
     * 统计调度
     *
     * @param userId
     * @param budgetAmount
     * @param startTime
     * @param endTime
     * @param bussKey
     * @param isRedo
     * @param period
     * @return
     */
    public BudgetLog statBudget(Long userId, BigDecimal budgetAmount, Date startTime, Date endTime, String bussKey, boolean isRedo, PeriodType period) {
        //step 2:查询实际的消费情况
        ConsumeBean cb = this.getConsume(startTime, endTime, userId);
        //step 3:保存记录
        BudgetLog bl = new BudgetLog();
        bl.setBussKey(bussKey);
        if (isRedo) {
            //查找原来的记录
            BudgetLog ori = budgetService.selectBudgetLog(bussKey, userId, null, null);
            if (ori != null) {
                bl.setBudgetAmount(ori.getBudgetAmount());
            } else {
                bl.setBudgetAmount(budgetAmount);
            }
        } else {
            bl.setBudgetAmount(budgetAmount);
        }
        bl.setNcAmount(cb.getNcAmount()==null? new BigDecimal(0):cb.getNcAmount());
        bl.setBcAmount(cb.getBcAmount()==null? new BigDecimal(0):cb.getBcAmount());
        bl.setTrAmount(cb.getTreatAmount()==null? new BigDecimal(0):cb.getTreatAmount());
        bl.setBussDay(startTime);
        bl.setUserId(userId);
        bl.setStatPeriod(period);
        bl.setTotalAmount(NumberUtil.sum(bl.getNcAmount(),bl.getBcAmount(),bl.getTrAmount()));
        bl.setRemark("调度自动生成");
        return bl;
    }


    /**
     * 统计及保存预算日志
     *
     * @param usList
     * @param userId
     * @param bussDay
     * @param bussKey
     * @param isRedo
     * @param period
     * @param useLastDay
     * @return
     */
    public BudgetLog statAndSaveBudgetLog(List<Budget> usList, Long userId, Date bussDay, String bussKey, boolean isRedo, PeriodType period, boolean useLastDay) {
        BudgetAmountBean bab = this.calcBudgetAmount(usList, bussDay);
        BigDecimal budgetAmount = new BigDecimal(0);
        List<Budget> ccList;
        if (period == PeriodType.MONTHLY) {
            budgetAmount = bab.getMonthBudget();
            ccList = bab.getMonthBudgetList();
        } else {
            budgetAmount = bab.getYearBudget();
            ccList = bab.getYearBudgetList();
        }
        PeriodDateBean pdb = BussUtil.calPeriod(bussDay,period);
        if(!useLastDay){
            //使用运营日
            pdb.updateEndDate(DateUtil.tillMiddleNight(bussDay));
        }
        BudgetLog bl = this.statBudget(userId, budgetAmount, pdb.getStartDate(),pdb.getEndDate(), bussKey, isRedo, period);
        //自动计算
        bl.setSource(BudgetLogSource.AUTO);
        //计算收入
        IncomeSummaryStat iss = incomeService.incomeSummaryStat(userId, pdb.getStartDate(),pdb.getEndDate());
        BigDecimal totalAmount = iss.getTotalAmount();
        bl.setIncomeAmount(totalAmount == null ? new BigDecimal(0) : iss.getTotalAmount());
        budgetService.saveStatBudgetLog(ccList, bl, isRedo);
        return bl;
    }



}
