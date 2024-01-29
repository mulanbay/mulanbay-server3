package cn.mulanbay.pms.handler;

import cn.mulanbay.ai.ml.processor.BudgetConsumeMEvaluateProcessor;
import cn.mulanbay.ai.ml.processor.BudgetConsumeYEvaluateProcessor;
import cn.mulanbay.ai.ml.processor.bean.BudgetConsumeER;
import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.handler.bean.consume.ConsumeBean;
import cn.mulanbay.pms.handler.bean.fund.BudgetAmountBean;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.domain.BudgetLog;
import cn.mulanbay.pms.persistent.dto.consume.ConsumeBudgetStat;
import cn.mulanbay.pms.persistent.dto.consume.ConsumeConsumeTypeStat;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.GoodsConsumeType;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.service.BudgetService;
import cn.mulanbay.pms.persistent.service.ConsumeService;
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

    //年的时间格式化
    public static final String YEARLY_DATE_FORMAT = "yyyy";

    //月的时间格式化
    public static final String MONTHLY_DATE_FORMAT = "yyyyMM";

    @Autowired
    ConsumeService consumeService;

    @Autowired
    BudgetService budgetService;

    @Autowired
    UserScoreHandler userScoreHandler;

    @Autowired
    BudgetConsumeMEvaluateProcessor budgetConsumeMEvaluateProcessor;

    @Autowired
    BudgetConsumeYEvaluateProcessor budgetConsumeYEvaluateProcessor;

    public String createBussKey(PeriodType period, Date date) {
        String dateFormat = DateUtil.Format24Datetime2;
        if (period == PeriodType.YEARLY) {
            dateFormat = "yyyy";
        } else if (period == PeriodType.MONTHLY) {
            dateFormat = "yyyyMM";
        } else if (period == PeriodType.DAILY) {
            dateFormat = "yyyyMMdd";
        } else if (period == PeriodType.WEEKLY) {
            dateFormat = "yyyy";
        } else if (period == PeriodType.QUARTERLY) {
            dateFormat = "yyyyMM";
        }
        return DateUtil.getFormatDate(date, dateFormat);
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
            if (b.getPeriod() == PeriodType.WEEKLY) {
                bab.addYearBudget(b.getAmount().multiply(new BigDecimal(Constant.MAX_WEEK)));
                bab.addYearBudget(b);
            } else if (b.getPeriod() == PeriodType.MONTHLY) {
                bab.addYearBudget(b.getAmount().multiply(new BigDecimal(Constant.MAX_MONTH)));
                bab.addYearBudget(b);
                bab.addMonthBudget(b.getAmount());
                bab.addMonthBudget(b);
            } else if (b.getPeriod() == PeriodType.QUARTERLY) {
                bab.addYearBudget(b.getAmount().multiply(new BigDecimal(Constant.MAX_QUARTERLY)));
                bab.addYearBudget(b);
            } else if (b.getPeriod() == PeriodType.YEARLY) {
                bab.addYearBudget(b.getAmount());
                bab.addYearBudget(b);
                Date date = b.getExpectPaidTime();
                if (date != null) {
                    //月度预算
                    String m1 = DateUtil.getFormatDate(now, "MM");
                    String m2 = DateUtil.getFormatDate(date, "MM");
                    if (m1.equals(m2)) {
                        //同一个月
                        bab.addMonthBudget(b.getAmount());
                        bab.addMonthBudget(b);
                    }
                }
            } else if (b.getPeriod() == PeriodType.ONCE) {
                Date date = b.getExpectPaidTime();
                if (date != null) {
                    //年度预算
                    String y1 = DateUtil.getFormatDate(now, YEARLY_DATE_FORMAT);
                    String y2 = DateUtil.getFormatDate(date, YEARLY_DATE_FORMAT);
                    if (y1.equals(y2)) {
                        //同一年
                        bab.addYearBudget(b.getAmount());
                        bab.addYearBudget(b);
                    }
                    //月度预算
                    String m1 = DateUtil.getFormatDate(now, MONTHLY_DATE_FORMAT);
                    String m2 = DateUtil.getFormatDate(date, MONTHLY_DATE_FORMAT);
                    if (m1.equals(m2)) {
                        //同一个月
                        bab.addMonthBudget(b.getAmount());
                        bab.addMonthBudget(b);
                    }
                }

            } else if (b.getPeriod() == PeriodType.DAILY) {
                int ydays = DateUtil.getDays(Integer.parseInt(DateUtil.getFormatDate(now, YEARLY_DATE_FORMAT)));
                bab.addYearBudget(b.getAmount().multiply(new BigDecimal(ydays)));
                bab.addYearBudget(b);
                int mdays = DateUtil.getMonthDays(now);
                bab.addMonthBudget(b.getAmount().multiply(new BigDecimal(mdays)));
                bab.addMonthBudget(b);
            }
        }
        return bab;
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
        Date[] ds = this.getDateRange(budget.getPeriod(), bussDay, true);
        if (budget.getGoodsTypeId() != null) {
            v = consumeService.statConsumeAmount(ds[0], ds[1], budget.getUserId(), budget.getGoodsTypeId(), budget.getTags(),budget.getIcg());
        }
        return v;
    }

    /**
     * 获取时间区间
     *
     * @param period
     * @param bussDay
     * @param useLastDay endTime是否使用最后一天
     *                   true:月度类型则选择该月的最后一天，年度类型则选择该年的最后一天
     *                   false：endTime等于bussDay的23：59：59
     * @return
     */
    public Date[] getDateRange(PeriodType period, Date bussDay, boolean useLastDay) {
        Date startTime;
        Date endTime;
        if (period == PeriodType.MONTHLY) {
            //月的为当月第一天
            startTime = DateUtil.getFirstDayOfMonth(bussDay);
            if (useLastDay) {
                Date endDate = DateUtil.getLastDayOfMonth(bussDay);
                endTime = DateUtil.getTodayTillMiddleNightDate(endDate);
            } else {
                endTime = DateUtil.getTodayTillMiddleNightDate(bussDay);
            }
        } else {
            //年的为当年第一天
            startTime = DateUtil.getYearFirst(bussDay);
            if (useLastDay) {
                Date endDate = DateUtil.getLastDayOfYear(Integer.valueOf(DateUtil.getYear(bussDay)));
                endTime = DateUtil.getTodayTillMiddleNightDate(endDate);
            } else {
                endTime = DateUtil.getTodayTillMiddleNightDate(bussDay);
            }

        }
        //去掉时分秒
        startTime = DateUtil.getFromMiddleNightDate(startTime);
        return new Date[]{startTime, endTime};
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
     * 获取消费记录
     *
     * @param startTime
     * @param endTime
     * @param userId
     * @return
     */
    public ConsumeBean getConsume(Date startTime, Date endTime, Long userId) {
        ConsumeBean bean = new ConsumeBean();
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
        }

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
        bl.setNcAmount(cb.getNcAmount());
        bl.setBcAmount(cb.getBcAmount());
        bl.setTrAmount(cb.getTreatAmount());
        bl.setCreatedTime(new Date());
        bl.setOccurDate(startTime);
        bl.setUserId(userId);
        bl.setPeriod(period);
        bl.setRemark("调度自动生成");
        return bl;
    }

}
