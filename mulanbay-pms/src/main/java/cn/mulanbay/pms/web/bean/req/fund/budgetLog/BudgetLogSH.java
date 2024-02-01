package cn.mulanbay.pms.web.bean.req.fund.budgetLog;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.web.bean.request.PageSearch;

public class BudgetLogSH extends PageSearch implements BindUser {

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    @Query(fieldName = "budget.budgetId", op = Parameter.Operator.EQ)
    private Long budgetId;

    @Query(fieldName = "period", op = Parameter.Operator.EQ)
    private PeriodType period;

    private String budgetKey;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public PeriodType getPeriod() {
        return period;
    }

    public void setPeriod(PeriodType period) {
        this.period = period;
    }

    public String getBudgetKey() {
        return budgetKey;
    }

    public void setBudgetKey(String budgetKey) {
        this.budgetKey = budgetKey;
    }
}
