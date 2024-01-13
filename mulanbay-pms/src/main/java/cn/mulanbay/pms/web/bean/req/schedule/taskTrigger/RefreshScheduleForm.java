package cn.mulanbay.pms.web.bean.req.schedule.taskTrigger;

import cn.mulanbay.common.aop.BindUser;

public class RefreshScheduleForm implements BindUser {

    private Long triggerId;

    private Long userId;

    private Boolean force;

    public Long getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(Long triggerId) {
        this.triggerId = triggerId;
    }

    public Boolean getForce() {
        return force;
    }

    public void setForce(Boolean force) {
        this.force = force;
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
