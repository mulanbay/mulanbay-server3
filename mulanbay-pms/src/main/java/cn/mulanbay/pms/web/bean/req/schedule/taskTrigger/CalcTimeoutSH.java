package cn.mulanbay.pms.web.bean.req.schedule.taskTrigger;

import cn.mulanbay.web.bean.request.PageSearch;
import jakarta.validation.constraints.NotNull;

/**
 * 计算超时时间
 *
 * @author fenghong
 * @date 2024/3/10
 */
public class CalcTimeoutSH {

    @NotNull(message = "调度编号不能为空")
    private Long triggerId;

    private Integer days;

    public Long getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(Long triggerId) {
        this.triggerId = triggerId;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}
