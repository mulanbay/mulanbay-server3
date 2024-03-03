package cn.mulanbay.pms.util;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.util.bean.PeriodDateBean;

import java.util.Date;

/**
 * 业务时间类处理
 *
 * @author fenghong
 * @create 2018-02-17
 */
public class BussUtil {

    private static final String YEARLY_DATE_FORMAT="yyyy";

    private static final String MONTHLY_DATE_FORMAT="yyyyMM";

    private static final String DAILY_DATE_FORMAT="yyyyMMdd";


    /**
     * 计算周期
     *
     * @param bussDay 周期的第一天
     * @param period
     * @param amn 是否添加午夜
     * @return
     */
    public static PeriodDateBean calPeriod(Date bussDay,PeriodType period,boolean amn){
        PeriodDateBean bean = new PeriodDateBean();
        Date startDate =null;
        Date endDate =null;;
        String bussKey = BussUtil.getBussKey(period,bussDay);
        switch (period){
            case YEARLY -> {
                startDate = bussDay;
                endDate = DateUtil.getYearLast(startDate);
            }
            case MONTHLY -> {
                startDate = bussDay;
                endDate = DateUtil.getMonthLast(startDate);
            }
            case WEEKLY -> {

            }
            case QUARTERLY -> {

            }
            case DAILY -> {
                startDate = DateUtil.getDate(bussKey,DAILY_DATE_FORMAT);
                endDate = startDate;
            }
            case ONCE -> {

            }
        }
        //添加午夜时分秒
        if(amn){
            endDate = DateUtil.tillMiddleNight(endDate);
        }
        bean.setBussKey(bussKey);
        bean.setBussDay(bussDay);
        bean.setPeriod(period);
        bean.setStartDate(startDate);
        bean.setEndDate(endDate);
        if(startDate!=null&&endDate!=null){
            int totalDays = DateUtil.getIntervalDays(startDate,endDate);
            bean.setTotalDays(totalDays);
        }
        return bean;
    }

    /**
     * 计算周期
     *
     * @param bussKey
     * @param period
     * @return
     */
    public static PeriodDateBean calPeriod(String bussKey,PeriodType period){
        Date bussDay = getBussDay(period,bussKey);
        return calPeriod(bussDay,period,true);
    }

    /**
     * 计算周期
     *
     * @param bussKey
     * @param period
     * @return
     */
    public static PeriodDateBean calPeriod(String bussKey,PeriodType period,boolean amn){
        Date bussDay = getBussDay(period,bussKey);
        return calPeriod(bussDay,period,amn);
    }

    /**
     * 计算周期
     *
     * @param bussDay
     * @param period
     * @return
     */
    public static PeriodDateBean calPeriod(Date bussDay,PeriodType period){
        return calPeriod(bussDay,period,true);
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
     * 通过bussKey获取BussDay
     *
     * @param bussKey
     * @return
     */
    public static Date getBussDay(PeriodType periodType , String bussKey) {
        String date = bussKey;
        switch (periodType){
            case YEARLY -> {
                date = bussKey+"0101";
            }
            case MONTHLY -> {
                date = bussKey+"01";
            }
            case WEEKLY -> {

            }
            case QUARTERLY -> {

            }
            case DAILY -> {
            }
        }
        return DateUtil.getDate(date, DAILY_DATE_FORMAT);
    }

    /**
     * 业务key
     * @param periodType
     * @param date 任意时间
     * @return
     */
    public static String getBussKey(PeriodType periodType , Date date){
        String bussKey = null;
        switch (periodType){
            case YEARLY -> {
                bussKey = DateUtil.getFormatDate(date,YEARLY_DATE_FORMAT);
            }
            case MONTHLY -> {
                Date dd = DateUtil.getMonthFirst(date);
                bussKey = DateUtil.getFormatDate(dd,MONTHLY_DATE_FORMAT);
            }
            case WEEKLY -> {
                bussKey = DateUtil.getFormatDate(date,YEARLY_DATE_FORMAT);
                bussKey+=("-W"+DateUtil.getWeek(date));
            }
            case QUARTERLY -> {
                bussKey = DateUtil.getFormatDate(date,YEARLY_DATE_FORMAT);
                int month = DateUtil.getMonth(date);
                bussKey+=("-S"+((month+2)/3));
            }
            case DAILY -> {
                bussKey = DateUtil.getFormatDate(date,DAILY_DATE_FORMAT);
            }
        }
        return bussKey;
    }

    /**
     * 用户日历唯一业务key
     *
     * @param bussType 业务类型
     * @param bussId 业务主键（一般是模版的编号）
     * @param bussValue 业务值（比如用户计划的绑定值）
     * @return
     */
    public static String getCalendarBussIdentityKey(BussType bussType,Long bussId,String bussValue){
        return bussType.name()+"_"+bussId+"_"+bussValue;
    }
}
