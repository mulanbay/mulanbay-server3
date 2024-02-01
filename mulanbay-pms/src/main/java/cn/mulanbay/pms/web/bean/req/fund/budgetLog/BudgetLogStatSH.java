package cn.mulanbay.pms.web.bean.req.fund.budgetLog;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BudgetLogStatSH extends QueryBuilder implements DateStatSH, BindUser, FullEndDateTime {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "bussDay", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "bussDay", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    @Query(fieldName = "budget.budgetId", op = Parameter.Operator.EQ)
    private Long budgetId;

    //页面传输使用，因为可能是budgetId，也可能是分组的编号
    private String budgetKey;

    private Boolean needOutBurst;

    /**
     * 是否要预测
     */
    private Boolean predict = false;

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public DateGroupType getDateGroupType() {
        return null;
    }

    @Override
    public Boolean isCompleteDate() {
        return null;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

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

    public String getBudgetKey() {
        return budgetKey;
    }

    public void setBudgetKey(String budgetKey) {
        this.budgetKey = budgetKey;
    }

    public Boolean getNeedOutBurst() {
        return needOutBurst;
    }

    public void setNeedOutBurst(Boolean needOutBurst) {
        this.needOutBurst = needOutBurst;
    }

    public Boolean getPredict() {
        return predict;
    }

    public void setPredict(Boolean predict) {
        this.predict = predict;
    }
}
