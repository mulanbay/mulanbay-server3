package cn.mulanbay.pms.web.bean.req.dream;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.schedule.enums.TriggerType;
import jakarta.validation.constraints.NotNull;

public class DreamRemindForm implements BindUser {

    private Long remindId;

    private Long userId;

    @NotNull(message = "梦想不能为空")
    private Long dreamId;

    //从时间过去的百分比开始，比如月计划，从时间过去50%（即半个月）时开始提醒
    @NotNull(message = "开始比例不能为空")
    private Integer fromRate;

    @NotNull(message = "开始天数不能为空")
    private Integer fromExpectDays;

    //完成时是否要提醒
    @NotNull(message = "完成时是否要提醒不能为空")
    private Boolean finishRemind;

    @NotNull(message = "提醒周期类型不能为空")
    private TriggerType triggerType;

    @NotNull(message = "提醒周期不能为空")
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

    public Long getDreamId() {
        return dreamId;
    }

    public void setDreamId(Long dreamId) {
        this.dreamId = dreamId;
    }

    public Integer getFromRate() {
        return fromRate;
    }

    public void setFromRate(Integer fromRate) {
        this.fromRate = fromRate;
    }

    public Integer getFromExpectDays() {
        return fromExpectDays;
    }

    public void setFromExpectDays(Integer fromExpectDays) {
        this.fromExpectDays = fromExpectDays;
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
