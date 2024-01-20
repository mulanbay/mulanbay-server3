package cn.mulanbay.pms.web.bean.req.consume.consume;

import cn.mulanbay.common.aop.BindUser;

import jakarta.validation.constraints.NotNull;

public class ConsumeDeleteChildrenForm implements BindUser {

    @NotNull(message = "父级编号不能为空")
    private Long pid;

    private Long userId;

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
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
