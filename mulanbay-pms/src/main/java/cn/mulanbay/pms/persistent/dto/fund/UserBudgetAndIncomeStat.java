package cn.mulanbay.pms.persistent.dto.fund;

import java.math.BigDecimal;
import java.util.Date;

public class UserBudgetAndIncomeStat {

    private Long userId;
    private Date occurDate;
    private BigDecimal budgetAmount;
    private BigDecimal ncAmount;
    private BigDecimal bcAmount;
    private BigDecimal trAmount;
    private BigDecimal totalIncome;

    public UserBudgetAndIncomeStat(Long userId, Date occurDate, BigDecimal budgetAmount, BigDecimal ncAmount, BigDecimal bcAmount, BigDecimal trAmount, BigDecimal totalIncome) {
        this.userId = userId;
        this.occurDate = occurDate;
        this.budgetAmount = budgetAmount;
        this.ncAmount = ncAmount;
        this.bcAmount = bcAmount;
        this.trAmount = trAmount;
        this.totalIncome = totalIncome;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getOccurDate() {
        return occurDate;
    }

    public void setOccurDate(Date occurDate) {
        this.occurDate = occurDate;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
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

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }
}
