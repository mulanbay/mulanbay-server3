package cn.mulanbay.pms.web.bean.req.schedule.taskTrigger;

import jakarta.validation.constraints.NotNull;

public class TriggerResetForm {

    @NotNull(message = "调度编号不能为空")
    private Long triggerId;

    @NotNull(message = "总次数不能为空")
    private Long totalCount;

    @NotNull(message = "失败次数不能为空")
    private Long failCount;

    public Long getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(Long triggerId) {
        this.triggerId = triggerId;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getFailCount() {
        return failCount;
    }

    public void setFailCount(Long failCount) {
        this.failCount = failCount;
    }
}
