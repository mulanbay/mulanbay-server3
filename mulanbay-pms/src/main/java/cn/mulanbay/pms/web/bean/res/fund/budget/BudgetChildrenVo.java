package cn.mulanbay.pms.web.bean.res.fund.budget;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.pms.web.bean.res.chart.ChartData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BudgetChildrenVo  {

    private String budgetName;

    private BigDecimal budgetAmount;

    private BigDecimal cpPaidAmount;

    private String bussKey;

    private List<BudgetDetailVo> children;

    private ChartData chartData;

    /**
     * 增加子类
     * @param vo
     */
    public void addChild(BudgetDetailVo vo){
        if(children==null){
            children = new ArrayList<>();
        }
        children.add(vo);
    }

    /**
     * 比例
     * @return
     */
    public BigDecimal getRate(){
        return NumberUtil.getPercent(this.cpPaidAmount,this.budgetAmount,2);
    }

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public BigDecimal getCpPaidAmount() {
        return cpPaidAmount;
    }

    public void setCpPaidAmount(BigDecimal cpPaidAmount) {
        this.cpPaidAmount = cpPaidAmount;
    }

    public String getBussKey() {
        return bussKey;
    }

    public void setBussKey(String bussKey) {
        this.bussKey = bussKey;
    }

    public List<BudgetDetailVo> getChildren() {
        return children;
    }

    public void setChildren(List<BudgetDetailVo> children) {
        this.children = children;
    }

    public ChartData getChartData() {
        return chartData;
    }

    public void setChartData(ChartData chartData) {
        this.chartData = chartData;
    }
}
