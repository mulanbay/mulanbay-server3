package cn.mulanbay.pms.persistent.dto.report;

import cn.mulanbay.pms.persistent.domain.UserPlan;

import java.math.BigDecimal;

public class PlanReportAvgStat {

    private Long planId;

    private BigDecimal avgCountValue;

    private BigDecimal avgValue;

    private Long minCountValue;

    private Long minValue;

    private Long maxCountValue;

    private Long maxValue;

    private UserPlan plan;

    public PlanReportAvgStat(Long planId, BigDecimal avgCountValue, BigDecimal avgValue, Long minCountValue, Long minValue, Long maxCountValue, Long maxValue) {
        this.planId = planId;
        this.avgCountValue = avgCountValue;
        this.avgValue = avgValue;
        this.minCountValue = minCountValue;
        this.minValue = minValue;
        this.maxCountValue = maxCountValue;
        this.maxValue = maxValue;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
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

    public Long getMinCountValue() {
        return minCountValue;
    }

    public void setMinCountValue(Long minCountValue) {
        this.minCountValue = minCountValue;
    }

    public Long getMinValue() {
        return minValue;
    }

    public void setMinValue(Long minValue) {
        this.minValue = minValue;
    }

    public Long getMaxCountValue() {
        return maxCountValue;
    }

    public void setMaxCountValue(Long maxCountValue) {
        this.maxCountValue = maxCountValue;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
    }

    public UserPlan getPlan() {
        return plan;
    }

    public void setPlan(UserPlan plan) {
        this.plan = plan;
    }
}
