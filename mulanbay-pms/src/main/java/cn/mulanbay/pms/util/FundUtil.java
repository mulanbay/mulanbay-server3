package cn.mulanbay.pms.util;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.util.bean.PeriodDateBean;

import java.util.Date;

import static cn.mulanbay.pms.handler.BudgetHandler.MONTHLY_DATE_FORMAT;
import static cn.mulanbay.pms.handler.BudgetHandler.YEARLY_DATE_FORMAT;

/**
 * 资金处理类
 *
 * @author fenghong
 * @create 2018-02-17
 */
public class FundUtil {

    /**
     * 计算周期
     *
     * @param bussDay
     * @param period
     * @return
     */
    public static PeriodDateBean calPeriod(Date bussDay,PeriodType period){
        PeriodDateBean bean = new PeriodDateBean();
        String dateFormat = "yyyy-MM";
        int totalDays =0;
        Date startDate;
        Date endDate;
        if (PeriodType.YEARLY == period) {
            dateFormat = "yyyy";
            totalDays = DateUtil.getYearDays(bussDay);
            startDate = DateUtil.getYearFirst(bussDay);
            endDate = DateUtil.getYearLast(bussDay);
        }else{
            totalDays = DateUtil.getMonthDays(bussDay);
            startDate = DateUtil.getMonthFirst(bussDay);
            endDate = DateUtil.getMonthLast(bussDay);
        }
        bean.setPeriod(period);
        bean.setDateFormat(dateFormat);
        bean.setStartDate(startDate);
        bean.setEndDate(endDate);
        bean.setTotalDays(totalDays);
        return bean;
    }


    /**
     * 获取预算统计计算的系数
     * @param statType 统计类型
     * @param b
     * @return
     */
    public static int getFactor(PeriodType statType, Date statDate, Budget b){
        PeriodType myType = b.getPeriod();
        Date myDate = b.getExpectPaidTime();
        switch (myType){
            case ONCE -> {
                if (myDate != null) {
                    if(statType==PeriodType.YEARLY){
                        //年度预算
                        String y1 = DateUtil.getFormatDate(statDate, YEARLY_DATE_FORMAT);
                        String y2 = DateUtil.getFormatDate(myDate, YEARLY_DATE_FORMAT);
                        if (y1.equals(y2)) {
                            return 1;
                        }
                    }else if(statType==PeriodType.MONTHLY){
                        //月度预算
                        String m1 = DateUtil.getFormatDate(statDate, MONTHLY_DATE_FORMAT);
                        String m2 = DateUtil.getFormatDate(myDate, MONTHLY_DATE_FORMAT);
                        if (m1.equals(m2)) {
                            return 1;
                        }
                    }
                }
            }
            case DAILY -> {
                if(statType==PeriodType.YEARLY){
                    int ydays = DateUtil.getDays(Integer.parseInt(DateUtil.getFormatDate(statDate, YEARLY_DATE_FORMAT)));
                    return ydays;
                }else if(statType==PeriodType.MONTHLY){
                    int mdays = DateUtil.getMonthDays(statDate);
                    return mdays;
                }
            }
            case WEEKLY -> {
                if(statType==PeriodType.YEARLY){
                    return Constant.MAX_WEEK;
                }else if(statType==PeriodType.MONTHLY){
                    return Constant.MAX_MONTH_WEEK;
                }
            }
            case MONTHLY -> {
                if(statType==PeriodType.YEARLY){
                    return Constant.MAX_MONTH;
                }else if(statType==PeriodType.MONTHLY){
                    return 1;
                }
            }
            case QUARTERLY -> {
                if(statType==PeriodType.YEARLY){
                    return Constant.MAX_QUARTERLY;
                }else if(statType==PeriodType.MONTHLY){
                    return 0;
                }
            }
            case YEARLY -> {
                if(statType==PeriodType.YEARLY){
                    return 1;
                }else if(statType==PeriodType.MONTHLY){
                    return 0;
                }
            }
        }
        return 0;
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
    public static Date[] getDateRange(PeriodType period, Date bussDay, boolean useLastDay) {
        Date startTime;
        Date endTime;
        if (period == PeriodType.MONTHLY) {
            //月的为当月第一天
            startTime = DateUtil.getMonthFirst(bussDay);
            if (useLastDay) {
                Date endDate = DateUtil.getMonthLast(bussDay);
                endTime = DateUtil.tillMiddleNight(endDate);
            } else {
                endTime = DateUtil.tillMiddleNight(bussDay);
            }
        } else {
            //年的为当年第一天
            startTime = DateUtil.getYearFirst(bussDay);
            if (useLastDay) {
                Date endDate = DateUtil.getYearLast(Integer.valueOf(DateUtil.getYear(bussDay)));
                endTime = DateUtil.tillMiddleNight(endDate);
            } else {
                endTime = DateUtil.tillMiddleNight(bussDay);
            }

        }
        //去掉时分秒
        startTime = DateUtil.fromMiddleNight(startTime);
        return new Date[]{startTime, endTime};
    }

    /**
     * 通过bussKey获取BussDay
     *
     * @param bussKey
     * @return
     */
    public static Date getBussDay(String bussKey) {
        if (bussKey.length() == 4) {
            return DateUtil.getDate(bussKey + "0101", "yyyyMMdd");
        } else {
            return DateUtil.getDate(bussKey + "01", "yyyyMMdd");
        }
    }

}
