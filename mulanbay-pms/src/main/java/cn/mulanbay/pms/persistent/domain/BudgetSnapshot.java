package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BudgetType;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 预算快照
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "budget_snapshot")
public class BudgetSnapshot implements java.io.Serializable {

    private static final long serialVersionUID = 7254329209805899896L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "snapshot_id", unique = true, nullable = false)
    private Long snapshotId;

    @Column(name = "user_id")
    private Long userId;
    //日终统计的月度/年度统计外键
    @Column(name = "budget_Log_id")
    private Long budgetLogId;
    /**
     * 指的是统计的周期类型
     */
    @Column(name = "stat_period")
    private PeriodType statPeriod;
    //冗余
    @Column(name = "buss_key")
    private String bussKey;
    //发生的时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "buss_day")
    private Date bussDay;
    //来源ID，即Budget主键
    @Column(name = "budget_id")
    private Long budgetId;
    //名称
    @Column(name = "budget_name")
    private String budgetName;
    //类型
    @Column(name = "type")
    private BudgetType type;
    //周期类型
    @Column(name = "period")
    private PeriodType period;
    //期望支付时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "expect_paid_time")
    private Date expectPaidTime;
    //第一次支付时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "first_paid_time")
    private Date firstPaidTime;
    //上一次支付时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "last_paid_time")
    private Date lastPaidTime;
    //金额
    @Column(name = "amount",precision = 9,scale = 2)
    private BigDecimal amount;
    @Column(name = "remind")
    private Boolean remind;
    //账户状态
    @Column(name = "status")
    private CommonStatus status;
    @Column(name = "tags")
    private String tags;
    //消费大类（feeType为Consume有效）
    @Column(name = "goods_type_id")
    private Long goodsTypeId;
    /**
     * 统计实际消费时是否包含子商品类型的数据
     */
    @Column(name = "icg")
    private Boolean icg;

    /**
     * 实际消费金额
     */
    @Column(name = "ac_amount",precision = 9,scale = 2)
    private BigDecimal acAmount;

    /**
     * 统计系数
     */
    @Column(name = "factor")
    private Integer factor;

    /**
     * 占该周期预算总值的比例
     */
    @Column(name = "rate",precision = 9,scale = 4)
    private BigDecimal rate;

    /**
     * 占该周期收入的比例
     */
    @Column(name = "ic_rate",precision = 9,scale = 4)
    private BigDecimal icRate;

    @Column(name = "remark")
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time")
    private Date modifyTime;


    @Transient
    public String getTypeName() {
        return type.getName();
    }

    @Transient
    public String getPeriodName() {
        return period.getName();
    }

    @Transient
    public String getStatusName() {
        return status.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof BudgetSnapshot bean) {
            return bean.getSnapshotId().equals(this.getSnapshotId());
        }else {
            return false;
        }
    }

    public Long getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(Long snapshotId) {
        this.snapshotId = snapshotId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBudgetLogId() {
        return budgetLogId;
    }

    public void setBudgetLogId(Long budgetLogId) {
        this.budgetLogId = budgetLogId;
    }

    public PeriodType getStatPeriod() {
        return statPeriod;
    }

    public void setStatPeriod(PeriodType statPeriod) {
        this.statPeriod = statPeriod;
    }

    public String getBussKey() {
        return bussKey;
    }

    public void setBussKey(String bussKey) {
        this.bussKey = bussKey;
    }

    public Date getBussDay() {
        return bussDay;
    }

    public void setBussDay(Date bussDay) {
        this.bussDay = bussDay;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public BudgetType getType() {
        return type;
    }

    public void setType(BudgetType type) {
        this.type = type;
    }

    public PeriodType getPeriod() {
        return period;
    }

    public void setPeriod(PeriodType period) {
        this.period = period;
    }

    public Date getExpectPaidTime() {
        return expectPaidTime;
    }

    public void setExpectPaidTime(Date expectPaidTime) {
        this.expectPaidTime = expectPaidTime;
    }

    public Date getFirstPaidTime() {
        return firstPaidTime;
    }

    public void setFirstPaidTime(Date firstPaidTime) {
        this.firstPaidTime = firstPaidTime;
    }

    public Date getLastPaidTime() {
        return lastPaidTime;
    }

    public void setLastPaidTime(Date lastPaidTime) {
        this.lastPaidTime = lastPaidTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Boolean getRemind() {
        return remind;
    }

    public void setRemind(Boolean remind) {
        this.remind = remind;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Long getGoodsTypeId() {
        return goodsTypeId;
    }

    public void setGoodsTypeId(Long goodsTypeId) {
        this.goodsTypeId = goodsTypeId;
    }

    public Boolean getIcg() {
        return icg;
    }

    public void setIcg(Boolean icg) {
        this.icg = icg;
    }

    public BigDecimal getAcAmount() {
        return acAmount;
    }

    public void setAcAmount(BigDecimal acAmount) {
        this.acAmount = acAmount;
    }

    public Integer getFactor() {
        return factor;
    }

    public void setFactor(Integer factor) {
        this.factor = factor;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getIcRate() {
        return icRate;
    }

    public void setIcRate(BigDecimal icRate) {
        this.icRate = icRate;
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
    public int hashCode() {
        return Objects.hashCode(snapshotId);
    }
}
