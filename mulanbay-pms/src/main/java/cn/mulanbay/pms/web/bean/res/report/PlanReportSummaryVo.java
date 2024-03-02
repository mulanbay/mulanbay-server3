package cn.mulanbay.pms.web.bean.res.report;

import cn.mulanbay.pms.web.bean.res.chart.ChartPieData;

public class PlanReportSummaryVo {

    private long planCountValue;

    private long planValue;

    private long reportCountValueSum;

    private long reportValueSum;

    private long totalCount;

    //相差多少次
    private long diffCountValueSum;

    //相差多少值
    private long diffValueSum;

    long planCountValueSum;

    long planValueSum;

    private String unit;

    //统计分析图：乐器练习比例
    private ChartPieData pieData;

    public long getPlanCountValue() {
        return planCountValue;
    }

    public void setPlanCountValue(long planCountValue) {
        this.planCountValue = planCountValue;
    }

    public long getPlanValue() {
        return planValue;
    }

    public void setPlanValue(long planValue) {
        this.planValue = planValue;
    }

    public long getReportCountValueSum() {
        return reportCountValueSum;
    }

    public void setReportCountValueSum(long reportCountValueSum) {
        this.reportCountValueSum = reportCountValueSum;
    }

    public long getReportValueSum() {
        return reportValueSum;
    }

    public void setReportValueSum(long reportValueSum) {
        this.reportValueSum = reportValueSum;
    }

    public ChartPieData getPieData() {
        return pieData;
    }

    public void setPieData(ChartPieData pieData) {
        this.pieData = pieData;
    }

    public void addReportCountValue(long value) {
        reportCountValueSum += value;
    }

    public void addReportValue(long value) {
        reportValueSum += value;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public void addTotalCount(long value) {
        totalCount += value;
    }

    public long getDiffCountValueSum() {
        return diffCountValueSum;
    }

    public void setDiffCountValueSum(long diffCountValueSum) {
        this.diffCountValueSum = diffCountValueSum;
    }

    public long getDiffValueSum() {
        return diffValueSum;
    }

    public void setDiffValueSum(long diffValueSum) {
        this.diffValueSum = diffValueSum;
    }

    public long getPlanCountValueSum() {
        return planCountValueSum;
    }

    public void setPlanCountValueSum(long planCountValueSum) {
        this.planCountValueSum = planCountValueSum;
    }

    public long getPlanValueSum() {
        return planValueSum;
    }

    public void setPlanValueSum(long planValueSum) {
        this.planValueSum = planValueSum;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
