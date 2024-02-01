package cn.mulanbay.pms.web.bean.req.fund.budgetLog;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class BudgetLogForm implements BindUser {

    private Long logId;

    private Long userId;

    @NotNull(message = "预算编号不能为空")
    private Long budgetId;

    //金额
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @NotNull(message = "业务时间不能为空")
    private Date bussDay;

    private Long consumeId;

    private String remark;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getBussDay() {
        return bussDay;
    }

    public void setBussDay(Date bussDay) {
        this.bussDay = bussDay;
    }

    public Long getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(Long consumeId) {
        this.consumeId = consumeId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
