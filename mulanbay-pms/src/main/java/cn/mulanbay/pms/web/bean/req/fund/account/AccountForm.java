package cn.mulanbay.pms.web.bean.req.fund.account;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.AccountStatus;
import cn.mulanbay.pms.persistent.enums.AccountType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AccountForm implements BindUser {

    private Long accountId;

    @NotEmpty(message = "账号名称不能为空")
    private String accountName;

    private Long userId;

    private String cardNo;

    @NotNull(message = "账号类型不能为空")
    private AccountType type;

    @NotNull(message = "账号金额不能为空")
    private BigDecimal amount;

    private String remark;

    @NotNull(message = "账号状态不能为空")
    private AccountStatus status;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
