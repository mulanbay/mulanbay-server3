package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.AccountStatus;
import cn.mulanbay.pms.persistent.enums.AccountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 账户
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "account")
public class Account implements java.io.Serializable {

    private static final long serialVersionUID = 7254329209805899896L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "account_id", unique = true, nullable = false)
    private Long accountId;

    @Column(name = "user_id")
    private Long userId;

    /**
     * 账户名称
     */
    @Column(name = "account_name")
    private String accountName;
    /**
     * 账户卡号
     */
    @Column(name = "card_no")
    private String cardNo;

    /*8
    账户类型
     */
    @Column(name = "type")
    private AccountType type;

    /**
     * 账户余额
     */
    @Column(name = "amount",precision = 9,scale = 2)
    private BigDecimal amount;

    /**
     * 账户状态
     */
    @Column(name = "status")
    private AccountStatus status;

    @Column(name = "remark")
    private String remark;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time",updatable = false)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time",insertable = false)
    private Date modifyTime;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
    public String getStatusName() {
        return status==null ? null:status.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Account bean) {
            return bean.getAccountId().equals(this.getAccountId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountId);
    }
}
