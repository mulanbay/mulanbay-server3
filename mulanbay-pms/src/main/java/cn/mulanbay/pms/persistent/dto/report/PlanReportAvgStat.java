package cn.mulanbay.pms.persistent.dto.report;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PlanReportAvgStat {

    private BigInteger planId;

    private BigDecimal avgCountValue;

    private BigDecimal avgValue;

    public PlanReportAvgStat(BigInteger planId, BigDecimal avgCountValue, BigDecimal avgValue) {
        this.planId = planId;
        this.avgCountValue = avgCountValue;
        this.avgValue = avgValue;
    }

    public BigInteger getPlanId() {
        return planId;
    }

    public void setPlanId(BigInteger planId) {
        this.planId = planId;
    }

    public BigDecimal getAvgCountValue() {
        return avgCountValue;
    }

    public void setAvgCountValue(BigDecimal avgCountValue) {
        this.avgCountValue = avgCountValue;
    }

    public BigDecimal getAvgValue() {
        return avgValue;
    }

    public void setAvgValue(BigDecimal avgValue) {
        this.avgValue = avgValue;
    }
}
