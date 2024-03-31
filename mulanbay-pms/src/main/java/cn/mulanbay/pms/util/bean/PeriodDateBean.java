package cn.mulanbay.pms.util.bean;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.pms.persistent.enums.PeriodType;

import java.util.Date;

public class PeriodDateBean {

    /**
     * 周期类型
     */
    private PeriodType period;

    /**
     * 总天数
     */
    private int totalDays;

    /**
     * 运营日
     */
    private Date bussDay;

    /**
     * 业务key
     */
    private String bussKey;

    /**
     * 开始时间，精确到天
     */
    private Date startDate;

    /**
     * 结束时间，精确到天
     */
    private Date endDate;

    public PeriodType getPeriod() {
        return period;
    }

    public void setPeriod(PeriodType period) {
        this.period = period;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public Date getBussDay() {
        return bussDay;
    }

    public void setBussDay(Date bussDay) {
        this.bussDay = bussDay;
    }

    public String getBussKey() {
        return bussKey;
    }

    public void setBussKey(String bussKey) {
        this.bussKey = bussKey;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * 更新截止日期，因为有些情况下，截止日期不是最后一天，而是当前日期(运营日)
     * @param endDate
     */
    public void updateEndDate(Date endDate){
        this.endDate = endDate;
        int totalDays = DateUtil.getIntervalDays(startDate,endDate);
        this.setTotalDays(totalDays);
    }
}
