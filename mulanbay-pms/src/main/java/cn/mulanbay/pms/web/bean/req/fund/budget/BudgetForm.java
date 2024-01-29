package cn.mulanbay.pms.web.bean.req.fund.budget;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BudgetType;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.PeriodType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class BudgetForm implements BindUser {

    private Long budgetId;

    @NotEmpty(message = "名称不能为空")
    private String budgetName;

    private Long userId;

    //类型
    @NotNull(message = "类型不能为空")
    private BudgetType type;

    //周期类型
    @NotNull(message = "周期不能为空")
    private PeriodType period;

    //金额
    @NotNull(message = "预算值不能为空")
    private BigDecimal amount;

    //期望支付时间
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date expectPaidTime;

    private String tags;

    //消费大类（feeType为BUY_RECORD有效）
    private Long goodsTypeId;

    private Boolean icg;

    private String remark;

    @NotNull(message = "是否提醒不能为空")
    private Boolean remind;

    @NotNull(message = "状态不能为空")
    private CommonStatus status;

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

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getExpectPaidTime() {
        return expectPaidTime;
    }

    public void setExpectPaidTime(Date expectPaidTime) {
        this.expectPaidTime = expectPaidTime;
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
}
