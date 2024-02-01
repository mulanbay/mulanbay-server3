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
 * 用户预算
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "budget")
public class Budget implements java.io.Serializable {

    private static final long serialVersionUID = 7254329209805899896L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "budget_id", unique = true, nullable = false)
    private Long budgetId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "budget_name")
    //名称
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
    @Column(name = "remark")
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time")
    private Date modifyTime;

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
    public String getPeriodName(){
        return period==null? null:period.getName();
    }

    @Transient
    public String getTypeName(){
        return type==null? null:type.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Budget bean) {
            return bean.getBudgetId().equals(this.getBudgetId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(budgetId);
    }
}
