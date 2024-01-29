package cn.mulanbay.pms.web.bean.res.fund.budget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BudgetAnalyseVo {

    private String title;

    //预算金额
    private BigDecimal budgetAmount;

    //最近一次收入(月收入或年收入)
    private BigDecimal lastIncome;

    //实际普通消费金额
    private BigDecimal ncAmount;

    //实际突发消费金额
    private BigDecimal bcAmount;

    //实际看病消费金额
    private BigDecimal trAmount;

    private List<BudgetInfoVo> budgetList = new ArrayList<>();

    /**
     * 添加
     *
     * @param bib
     */
    public void addBudget(BudgetInfoVo bib) {
        budgetList.add(bib);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public BigDecimal getLastIncome() {
        return lastIncome;
    }

    public void setLastIncome(BigDecimal lastIncome) {
        this.lastIncome = lastIncome;
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

    public List<BudgetInfoVo> getBudgetList() {
        return budgetList;
    }

    public void setBudgetList(List<BudgetInfoVo> budgetList) {
        this.budgetList = budgetList;
    }

    /**
     * 总的消费金额
     *
     * @return
     */
    public BigDecimal getConsumeAmount() {
        return ncAmount.add(bcAmount).add(trAmount);
    }
}
