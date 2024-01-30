package cn.mulanbay.pms.web.bean.res.fund.budget;

import cn.mulanbay.pms.handler.bean.fund.FundStatBean;
import cn.mulanbay.pms.persistent.domain.Budget;

import java.util.ArrayList;
import java.util.List;

public class BudgetAnalyseVo {

    private String title;

    private FundStatBean fundStat;

    private List<Budget> budgetList = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FundStatBean getFundStat() {
        return fundStat;
    }

    public void setFundStat(FundStatBean fundStat) {
        this.fundStat = fundStat;
    }

    public List<Budget> getBudgetList() {
        return budgetList;
    }

    public void setBudgetList(List<Budget> budgetList) {
        this.budgetList = budgetList;
    }
}
