package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.AccountAdjustType;
import cn.mulanbay.pms.persistent.enums.AccountStatus;
import cn.mulanbay.pms.persistent.enums.AccountType;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 账户流水记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "account_flow")
public class AccountFlow implements java.io.Serializable {

    private static final long serialVersionUID = 7254329209805899896L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "flow_id", unique = true, nullable = false)
    private Long flowId;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = true)
    private Account account;

    //账户名称
    @Column(name = "account_name")
    private String accountName;

    //账户卡号
    @Column(name = "card_no")
    private String cardNo;

    //账户类型
    @Column(name = "type")
    private AccountType type;

    @Column(name = "before_amount")
    private BigDecimal beforeAmount;

    @Column(name = "after_amount")
    private BigDecimal afterAmount;

    @Column(name = "adjust_type")
    private AccountAdjustType adjustType;
    //业务key，只针对快照方式有效

    @ManyToOne
    @JoinColumn(name = "snapshot_id", nullable = true)
    private AccountSnapshot snapshot;

    //账户状态
    @Column(name = "status")
    private AccountStatus status;
    @Column(name = "remark")
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time")
    private Date modifyTime;

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
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

    public BigDecimal getBeforeAmount() {
        return beforeAmount;
    }

    public void setBeforeAmount(BigDecimal beforeAmount) {
        this.beforeAmount = beforeAmount;
    }

    public BigDecimal getAfterAmount() {
        return afterAmount;
    }

    public void setAfterAmount(BigDecimal afterAmount) {
        this.afterAmount = afterAmount;
    }

    public AccountAdjustType getAdjustType() {
        return adjustType;
    }

    public void setAdjustType(AccountAdjustType adjustType) {
        this.adjustType = adjustType;
    }

    public AccountSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(AccountSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Transient
    public String getTypeName() {
        return type==null ? null:type.getName();
    }

    @Transient
    public String getAdjustTypeName() {
        return adjustType==null ? null:adjustType.getName();
    }

    @Transient
    public String getStatusName() {
        return status==null ? null:status.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AccountFlow bean) {
            return bean.getFlowId().equals(this.getFlowId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(flowId);
    }
}
