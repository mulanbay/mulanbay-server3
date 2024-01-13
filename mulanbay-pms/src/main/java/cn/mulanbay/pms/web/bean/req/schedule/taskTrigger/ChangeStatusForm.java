package cn.mulanbay.pms.web.bean.req.schedule.taskTrigger;

import cn.mulanbay.common.aop.BindUser;

public class ChangeStatusForm implements BindUser {

    private boolean afterStatus;

    private Long userId;

    public boolean isAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(boolean afterStatus) {
        this.afterStatus = afterStatus;
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
