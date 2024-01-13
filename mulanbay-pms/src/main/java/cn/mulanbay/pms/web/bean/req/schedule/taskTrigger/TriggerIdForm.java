package cn.mulanbay.pms.web.bean.req.schedule.taskTrigger;

import jakarta.validation.constraints.NotNull;

public class TriggerIdForm {

    @NotNull(message = "调度编号不能为空")
    private Long triggerId;

    public Long getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(Long triggerId) {
        this.triggerId = triggerId;
    }

}
