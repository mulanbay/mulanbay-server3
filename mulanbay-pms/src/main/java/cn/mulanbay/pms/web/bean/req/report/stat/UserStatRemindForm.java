package cn.mulanbay.pms.web.bean.req.report.stat;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.schedule.enums.TriggerType;

import jakarta.validation.constraints.NotNull;

public class UserStatRemindForm implements BindUser {

    private Long remindId;

    private Long userId;

    @NotNull(message = "用户统计不能为空")
    private Long statId;

    @NotNull(message = "周期类型不能为空")
    private TriggerType triggerType;

    @NotNull(message = "周期值不能为空")
    private Integer triggerInterval;

    //超过告警值提醒
    @NotNull(message = "超过告警值提醒值不能为空")
    private Boolean owr;

    //超过警报值提醒
    @NotNull(message = "超过警报值提醒值不能为空")
    private Boolean oar;

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

    public Long getStatId() {
        return statId;
    }

    public void setStatId(Long statId) {
        this.statId = statId;
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

    public Boolean getOwr() {
        return owr;
    }

    public void setOwr(Boolean owr) {
        this.owr = owr;
    }

    public Boolean getOar() {
        return oar;
    }

    public void setOar(Boolean oar) {
        this.oar = oar;
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
