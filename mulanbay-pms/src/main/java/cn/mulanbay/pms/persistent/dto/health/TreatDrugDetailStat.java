package cn.mulanbay.pms.persistent.dto.health;

import java.math.BigInteger;
import java.util.Date;

public class TreatDrugDetailStat {

    private BigInteger drugId;

    private String drugName;

    private Date treatTime;

    private Date minTime;

    private Date maxTime;

    private BigInteger totalCount;

    //用药天数
    private Object days;

    public TreatDrugDetailStat(String drugName, Date minTime, Date maxTime, BigInteger totalCount, Object days) {
        this.drugName = drugName;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.totalCount = totalCount;
        this.days = days;
    }

    public TreatDrugDetailStat(BigInteger drugId, String drugName, Date treatTime, Date minTime, Date maxTime, BigInteger totalCount, Object days) {
        this.drugId = drugId;
        this.drugName = drugName;
        this.treatTime = treatTime;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.totalCount = totalCount;
        this.days = days;
    }

    public BigInteger getDrugId() {
        return drugId;
    }

    public void setDrugId(BigInteger drugId) {
        this.drugId = drugId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public Date getTreatTime() {
        return treatTime;
    }

    public void setTreatTime(Date treatTime) {
        this.treatTime = treatTime;
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

    public BigInteger getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(BigInteger totalCount) {
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
        return totalCount.longValue();
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
