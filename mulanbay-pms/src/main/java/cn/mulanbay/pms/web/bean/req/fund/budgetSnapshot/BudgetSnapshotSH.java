package cn.mulanbay.pms.web.bean.req.fund.budgetSnapshot;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BudgetSnapshotSH extends PageSearch implements BindUser {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "bussDay", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "bussDay", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "budgetLogId", op = Parameter.Operator.EQ)
    private Long budgetLogId;

    @Query(fieldName = "budgetId", op = Parameter.Operator.EQ)
    private Long budgetId;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getBudgetLogId() {
        return budgetLogId;
    }

    public void setBudgetLogId(Long budgetLogId) {
        this.budgetLogId = budgetLogId;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
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
