package cn.mulanbay.pms.web.bean.req.health.test;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotNull;

public class TreatTestLastSH implements BindUser {

    @NotNull(message = "名称不能为空")
    private String name;

    private Long userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
