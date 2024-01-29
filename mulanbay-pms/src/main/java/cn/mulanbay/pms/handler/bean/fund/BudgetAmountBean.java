package cn.mulanbay.pms.handler.bean.fund;

import cn.mulanbay.pms.persistent.domain.Budget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 预算封装类
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class BudgetAmountBean {

    //月度预算
    private BigDecimal monthBudget = new BigDecimal(0);

    //月度预算列表(可能包括单次、周之类)
    private List<Budget> monthBudgetList = new ArrayList<>();

    //年度预算
    private BigDecimal yearBudget = new BigDecimal(0);

    //月度预算列表(可能包括单次、周之类)
    private List<Budget> yearBudgetList = new ArrayList<>();

    /**
     * 增加月度预算
     *
     * @param v
     */
    public void addMonthBudget(BigDecimal v) {
        monthBudget = monthBudget.add(v);
    }

    /**
     * 增加月度预算
     *
     * @param budget
     */
    public void addMonthBudget(Budget budget) {
        monthBudgetList.add(budget);
    }

    /**
     * 增加年度预算
     *
     * @param v
     */
    public void addYearBudget(BigDecimal v) {
        yearBudget = yearBudget.add(v);
    }

    /**
     * 增加年度预算
     *
     * @param budget
     */
    public void addYearBudget(Budget budget) {
        yearBudgetList.add(budget);
    }

    public BigDecimal getMonthBudget() {
        return monthBudget;
    }

    public void setMonthBudget(BigDecimal monthBudget) {
        this.monthBudget = monthBudget;
    }

    public BigDecimal getYearBudget() {
        return yearBudget;
    }

    public void setYearBudget(BigDecimal yearBudget) {
        this.yearBudget = yearBudget;
    }

    public List<Budget> getMonthBudgetList() {
        return monthBudgetList;
    }

    public List<Budget> getYearBudgetList() {
        return yearBudgetList;
    }
}
