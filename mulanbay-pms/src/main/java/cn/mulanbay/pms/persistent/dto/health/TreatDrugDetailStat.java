package cn.mulanbay.pms.persistent.dto.health;

import java.util.Date;

public class TreatDrugDetailStat {

    private Long drugId;

    private String drugName;

    private Date treatDate;

    private Date minTime;

    private Date maxTime;

    private Object totalCount;

    //用药天数
    private Object days;

    public TreatDrugDetailStat(String drugName, Date minTime, Date maxTime, Object totalCount, Object days) {
        this.drugName = drugName;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.totalCount = totalCount;
        this.days = days;
    }

    public TreatDrugDetailStat(Long drugId, String drugName, Date treatDate, Date minTime, Date maxTime, Object totalCount, Object days) {
        this.drugId = drugId;
        this.drugName = drugName;
        this.treatDate = treatDate;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.totalCount = totalCount;
        this.days = days;
    }

    public Long getDrugId() {
        return drugId;
    }

    public void setDrugId(Long drugId) {
        this.drugId = drugId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public Date getTreatDate() {
        return treatDate;
    }

    public void setTreatDate(Date treatDate) {
        this.treatDate = treatDate;
    }

    public Date getMinTime() {
        return minTime;
    }

    public void setMinTime(Date minTime) {
        this.minTime = minTime;
    }

    public Date getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Date maxTime) {
        this.maxTime = maxTime;
    }

    public Object getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Object totalCount) {
        this.totalCount = totalCount;
    }

    public Object getDays() {
        return days;
    }

    public void setDays(Object days) {
        this.days = days;
    }

    /**
     * 不同的sql会产生不同的绑定对象类型
     *
     * @return
     */
    public long getTotalCountValue() {
        return Long.parseLong(totalCount.toString());
    }

    /**
     * 不同的sql会产生不同的绑定对象类型
     *
     * @return
     */
    public long getDaysValue() {
        return Long.parseLong(days.toString());
    }
}
