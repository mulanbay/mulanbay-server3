package cn.mulanbay.pms.web.bean.res.main;

import java.math.BigDecimal;

public class UserGeneralStatVo {

    // 本月预算
    private BigDecimal monthBudget = new BigDecimal(0);

    // 本月预测消费
    private BigDecimal monthPredict = new BigDecimal(0);

    //本年预算
    private BigDecimal yearBudget = new BigDecimal(0);

    // 本年预测消费
    private BigDecimal yearPredict = new BigDecimal(0);

    // 预算
    private BigDecimal totalIncome = new BigDecimal(0);

    // 消费总额，当前查询条件下(包括看病的金额)
    private BigDecimal totalConsumeAmount = new BigDecimal(0);

    // 消费次数
    private Long totalConsumeCount = 0L;

    // 看病总额
    private BigDecimal totalTreatAmount = new BigDecimal(0);

    // 看病次数
    private Long totalTreatCount = 0L;

    // 本月已过去时间（百分比）
    private double dayMonthRate = 0;

    //这个月剩余天数
    private int remainMonthDays;

    //这个月的消费总额
    private BigDecimal monthConsumeAmount = new BigDecimal(0);

    //这个月总天数
    private int monthDays;

    //这个月已经过去天数
    private int monthPassDays;

    public BigDecimal getMonthBudget() {
        return monthBudget;
    }

    public void setMonthBudget(BigDecimal monthBudget) {
        this.monthBudget = monthBudget;
    }

    public BigDecimal getMonthPredict() {
        return monthPredict;
    }

    public void setMonthPredict(BigDecimal monthPredict) {
        this.monthPredict = monthPredict;
    }

    public BigDecimal getYearBudget() {
        return yearBudget;
    }

    public void setYearBudget(BigDecimal yearBudget) {
        this.yearBudget = yearBudget;
    }

    public BigDecimal getYearPredict() {
        return yearPredict;
    }

    public void setYearPredict(BigDecimal yearPredict) {
        this.yearPredict = yearPredict;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalConsumeAmount() {
        return totalConsumeAmount;
    }

    public void setTotalConsumeAmount(BigDecimal totalConsumeAmount) {
        this.totalConsumeAmount = totalConsumeAmount;
    }

    public Long getTotalConsumeCount() {
        return totalConsumeCount;
    }

    public void setTotalConsumeCount(Long totalConsumeCount) {
        this.totalConsumeCount = totalConsumeCount;
    }

    public BigDecimal getTotalTreatAmount() {
        return totalTreatAmount;
    }

    public void setTotalTreatAmount(BigDecimal totalTreatAmount) {
        this.totalTreatAmount = totalTreatAmount;
    }

    public Long getTotalTreatCount() {
        return totalTreatCount;
    }

    public void setTotalTreatCount(Long totalTreatCount) {
        this.totalTreatCount = totalTreatCount;
    }

    public double getDayMonthRate() {
        return dayMonthRate;
    }

    public void setDayMonthRate(double dayMonthRate) {
        this.dayMonthRate = dayMonthRate;
    }

    public int getRemainMonthDays() {
        return remainMonthDays;
    }

    public void setRemainMonthDays(int remainMonthDays) {
        this.remainMonthDays = remainMonthDays;
    }

    public BigDecimal getMonthConsumeAmount() {
        return monthConsumeAmount;
    }

    public void setMonthConsumeAmount(BigDecimal monthConsumeAmount) {
        this.monthConsumeAmount = monthConsumeAmount;
    }

    public int getMonthDays() {
        return monthDays;
    }

    public void setMonthDays(int monthDays) {
        this.monthDays = monthDays;
    }

    public int getMonthPassDays() {
        return monthPassDays;
    }

    public void setMonthPassDays(int monthPassDays) {
        this.monthPassDays = monthPassDays;
    }

    /**
     * 添加消费
     *
     * @param v
     */
    public void appendConsume(BigDecimal v) {
        totalConsumeAmount = totalConsumeAmount.add(v);
    }

    /**
     * 添加消费
     *
     * @param v
     */
    public void appendMonthConsume(BigDecimal v) {
        monthConsumeAmount = monthConsumeAmount.add(v);
    }
}
