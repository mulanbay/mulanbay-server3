package cn.mulanbay.pms.web.bean.res.main;

import java.math.BigDecimal;

public class GeneralStatDetailVo {

    /**
     * 预算
     */
    private BigDecimal budget;

    /**
     * 普通消费
     */
    private BigDecimal ncAmount;
    //实际突发消费金额
    private BigDecimal bcAmount;
    //实际看病消费金额
    private BigDecimal trAmount;

    private BigDecimal totalConsume;

    /**
     * 消费次数
     */
    private Long consumeCount = 0L;

    /**
     * 消费预测
     */
    private BigDecimal consumePredict;

    /**
     * 收入
     */
    private BigDecimal income;

    private BigDecimal incomePredict;

    /**
     * 总天数
     */
    private int totalDays;

    //这个月已经过去天数
    private int passDays;

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public BigDecimal getNcAmount() {
        return ncAmount;
    }

    public void setNcAmount(BigDecimal ncAmount) {
        this.ncAmount = ncAmount;
    }

    public BigDecimal getBcAmount() {
        return bcAmount;
    }

    public void setBcAmount(BigDecimal bcAmount) {
        this.bcAmount = bcAmount;
    }

    public BigDecimal getTrAmount() {
        return trAmount;
    }

    public void setTrAmount(BigDecimal trAmount) {
        this.trAmount = trAmount;
    }

    public BigDecimal getTotalConsume() {
        return totalConsume;
    }

    public void setTotalConsume(BigDecimal totalConsume) {
        this.totalConsume = totalConsume;
    }

    public Long getConsumeCount() {
        return consumeCount;
    }

    public void setConsumeCount(Long consumeCount) {
        this.consumeCount = consumeCount;
    }

    public BigDecimal getConsumePredict() {
        return consumePredict;
    }

    public void setConsumePredict(BigDecimal consumePredict) {
        this.consumePredict = consumePredict;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getIncomePredict() {
        return incomePredict;
    }

    public void setIncomePredict(BigDecimal incomePredict) {
        this.incomePredict = incomePredict;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public int getPassDays() {
        return passDays;
    }

    public void setPassDays(int passDays) {
        this.passDays = passDays;
    }
}
