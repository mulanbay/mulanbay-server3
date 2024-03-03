package cn.mulanbay.pms.web.bean.req.system.system;

import cn.mulanbay.schedule.enums.TriggerStatus;
import jakarta.validation.constraints.NotNull;

public class SystemAutoLockForm {

    @NotNull(message = "调度状态不能为空")
    private TriggerStatus triggerStatus;

    private String stopPeriod;

    public TriggerStatus getTriggerStatus() {
        return triggerStatus;
    }

    public void setTriggerStatus(TriggerStatus triggerStatus) {
        this.triggerStatus = triggerStatus;
    }

    public String getStopPeriod() {
        return stopPeriod;
    }

    public void setStopPeriod(String stopPeriod) {
        this.stopPeriod = stopPeriod;
    }
}
