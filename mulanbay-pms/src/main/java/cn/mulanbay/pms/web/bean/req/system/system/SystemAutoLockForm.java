package cn.mulanbay.pms.web.bean.req.system.system;

import cn.mulanbay.schedule.enums.TriggerStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SystemAutoLockForm {

    @NotNull(message = "调度状态不能为空")
    private TriggerStatus triggerStatus;

    private String stopPeriod;

    @NotNull(message = "关闭代码不能为空")
    private int stopStatus;

    @NotEmpty(message = "消息提示不能为空")
    private String message;

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

    public int getStopStatus() {
        return stopStatus;
    }

    public void setStopStatus(int stopStatus) {
        this.stopStatus = stopStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
