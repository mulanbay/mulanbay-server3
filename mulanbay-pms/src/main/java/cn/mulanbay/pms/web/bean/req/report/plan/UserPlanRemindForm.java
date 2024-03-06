package cn.mulanbay.pms.web.bean.req.report.plan;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.schedule.enums.TriggerType;
import jakarta.validation.constraints.NotNull;

public class UserPlanRemindForm implements BindUser {

    private Long remindId;

    private Long userId;

    @NotNull(message = "计划不能为空")
    private Long planId;

    //从时间过去的百分比开始，比如月计划，从时间过去50%（即半个月）时开始提醒
    @NotNull(message = "比例不能为空")
    private Integer fromRate;

    //完成时是否要提醒
    @NotNull(message = "完成时是否要提醒不能为空")
    private Boolean finishRemind;

    @NotNull(message = "周期类型不能为空")
    private TriggerType triggerType;

    @NotNull(message = "周期值不能为空")
    private Integer triggerInterval;

    //提醒时间
    @NotNull(message = "提醒时间不能为空")
    private String remindTime;
    private String remark;

    public Long getRemindId() {
        return remindId;
    }

    public void setRemindId(Long remindId) {
        this.remindId = remindId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Integer getFromRate() {
        return fromRate;
    }

    public void setFromRate(Integer fromRate) {
        this.fromRate = fromRate;
    }

    public Boolean getFinishRemind() {
        return finishRemind;
    }

    public void setFinishRemind(Boolean finishRemind) {
        this.finishRemind = finishRemind;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public Integer getTriggerInterval() {
        return triggerInterval;
    }

    public void setTriggerInterval(Integer triggerInterval) {
        this.triggerInterval = triggerInterval;
    }

    public String getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
