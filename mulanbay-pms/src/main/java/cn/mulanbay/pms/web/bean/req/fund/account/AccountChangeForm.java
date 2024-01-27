package cn.mulanbay.pms.web.bean.req.fund.account;

import cn.mulanbay.common.aop.BindUser;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AccountChangeForm implements BindUser {

    private Long accountId;

    private Long userId;

    @NotNull(message = "金额不能为空")
    private BigDecimal afterAmount;

    private String remark;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAfterAmount() {
        return afterAmount;
    }

    public void setAfterAmount(BigDecimal afterAmount) {
        this.afterAmount = afterAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
