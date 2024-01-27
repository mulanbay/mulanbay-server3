package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BudgetLogSource;
import cn.mulanbay.pms.persistent.enums.PeriodType;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 预算日志
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "budget_log")
public class BudgetLog implements java.io.Serializable {

    private static final long serialVersionUID = 6891444266523508770L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "log_id", unique = true, nullable = false)
    private Long logId;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;
    //周期类型
    @Column(name = "period")
    private PeriodType period;
    //业务key，唯一性判断使用，避免重复设置
    @Column(name = "buss_key")
    private String bussKey;

    //发生的时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "occur_date")
    private Date occurDate;
    //预算金额
    @Column(name = "budget_amount")
    private BigDecimal budgetAmount;
    //实际普通消费金额
    @Column(name = "nc_amount")
    private BigDecimal ncAmount;
    //实际突发消费金额
    @Column(name = "bc_amount")
    private BigDecimal bcAmount;
    //实际看病消费金额
    @Column(name = "tr_amount")
    private BigDecimal trAmount;
    //收入
    @Column(name = "income_amount")
    private BigDecimal incomeAmount;
    //账户变化值
    @Column(name = "account_change_amount")
    private BigDecimal accountChangeAmount;

    @Column(name = "source")
    private BudgetLogSource source;
    @Column(name = "remark")
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time")
    private Date modifyTime;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public PeriodType getPeriod() {
        return period;
    }

    public void setPeriod(PeriodType period) {
        this.period = period;
    }

    public String getBussKey() {
        return bussKey;
    }

    public void setBussKey(String bussKey) {
        this.bussKey = bussKey;
    }

    public Date getOccurDate() {
        return occurDate;
    }

    public void setOccurDate(Date occurDate) {
        this.occurDate = occurDate;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public BigDecimal getNcAmount() {
        return ncAmount;
    }

    public void setNcAmount(BigDecimal ncAmount) {
        this.ncAmount = ncAmount;
    }

    public BigDecimal getBcAmount() {
        return bcAmount;
    }

    public void setBcAmount(BigDecimal bcAmount) {
        this.bcAmount = bcAmount;
    }

    public BigDecimal getTrAmount() {
        return trAmount;
    }

    public void setTrAmount(BigDecimal trAmount) {
        this.trAmount = trAmount;
    }

    public BigDecimal getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(BigDecimal incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public BigDecimal getAccountChangeAmount() {
        return accountChangeAmount;
    }

    public void setAccountChangeAmount(BigDecimal accountChangeAmount) {
        this.accountChangeAmount = accountChangeAmount;
    }

    public BudgetLogSource getSource() {
        return source;
    }

    public void setSource(BudgetLogSource source) {
        this.source = source;
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

    @Override
    public boolean equals(Object other) {
        if (other instanceof BudgetLog bean) {
            return bean.getLogId().equals(this.getLogId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(logId);
    }
}
