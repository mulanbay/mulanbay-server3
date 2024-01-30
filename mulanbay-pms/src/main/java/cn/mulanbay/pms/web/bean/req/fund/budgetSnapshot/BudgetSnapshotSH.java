package cn.mulanbay.pms.web.bean.req.fund.budgetSnapshot;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.web.bean.request.PageSearch;

public class BudgetSnapshotSH extends PageSearch implements BindUser {

    @Query(fieldName = "budgetLogId", op = Parameter.Operator.EQ)
    private Long budgetLogId;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    public Long getBudgetLogId() {
        return budgetLogId;
    }

    public void setBudgetLogId(Long budgetLogId) {
        this.budgetLogId = budgetLogId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
