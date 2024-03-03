package cn.mulanbay.pms.web.bean.req.dream;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DreamStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public class DreamForm implements BindUser {
    private Long dreamId;

    private Long userId;

    @NotEmpty(message = "名称不能为空")
    private String dreamName;

    /**
     * 单位元
     */
    private BigDecimal cost;

    @NotNull(message = "困难程度不能为空")
    private Integer difficulty;

    @NotNull(message = "重要程度不能为空")
    private Double important;

    private Integer expectDays;

    @NotNull(message = "状态不能为空")
    private DreamStatus status;

    private Integer rate;

    /**
     * 期望实现日期
     */
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "期望实现日期不能为空")
    private Date expectDate;

    /**
     * 截止日期
     */
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "截止日期不能为空")
    private Date deadline;

    /**
     * 完成日期
     */
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    private Date finishDate;


    //关联的计划配置项，到时根据无时间条件来计算进度
    private Long planId;

    //计划值(小时、天、次数等等)
    private Long planValue;

    @NotNull(message = "是否提醒不能为空")
    private Boolean remind;

    private Integer rewards;

    private String remark;

    public Long getDreamId() {
        return dreamId;
    }

    public void setDreamId(Long dreamId) {
        this.dreamId = dreamId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDreamName() {
        return dreamName;
    }

    public void setDreamName(String dreamName) {
        this.dreamName = dreamName;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Double getImportant() {
        return important;
    }

    public void setImportant(Double important) {
        this.important = important;
    }

    public Integer getExpectDays() {
        return expectDays;
    }

    public void setExpectDays(Integer expectDays) {
        this.expectDays = expectDays;
    }

    public DreamStatus getStatus() {
        return status;
    }

    public void setStatus(DreamStatus status) {
        this.status = status;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Date getExpectDate() {
        return expectDate;
    }

    public void setExpectDate(Date expectDate) {
        this.expectDate = expectDate;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getPlanValue() {
        return planValue;
    }

    public void setPlanValue(Long planValue) {
        this.planValue = planValue;
    }

    public Boolean getRemind() {
        return remind;
    }

    public void setRemind(Boolean remind) {
        this.remind = remind;
    }

    public Integer getRewards() {
        return rewards;
    }

    public void setRewards(Integer rewards) {
        this.rewards = rewards;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
