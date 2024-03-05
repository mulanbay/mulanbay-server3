package cn.mulanbay.pms.web.bean.res.system.system;

import cn.mulanbay.schedule.domain.TaskTrigger;

/**
 * @author fenghong
 * @date 2024/3/2
 */
public class SystemAutoLockVo {

    private TaskTrigger trigger;

    private String stopPeriod;

    private int stopStatus;

    private String message;


    public TaskTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(TaskTrigger trigger) {
        this.trigger = trigger;
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
