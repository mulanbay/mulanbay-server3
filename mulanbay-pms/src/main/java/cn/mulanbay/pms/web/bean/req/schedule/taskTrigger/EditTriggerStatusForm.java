package cn.mulanbay.pms.web.bean.req.schedule.taskTrigger;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.schedule.enums.TriggerStatus;
import jakarta.validation.constraints.NotNull;

public class EditTriggerStatusForm implements BindUser {

    @NotNull(message = "触发器编号不能为空")
    private Long triggerId;

    @NotNull(message = "修改后的状态不能为空")
    private TriggerStatus triggerStatus;

    private Long userId;

    public Long getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(Long triggerId) {
        this.triggerId = triggerId;
    }

    public TriggerStatus getTriggerStatus() {
        return triggerStatus;
    }

    public void setTriggerStatus(TriggerStatus triggerStatus) {
        this.triggerStatus = triggerStatus;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
