package cn.mulanbay.pms.web.bean.req.life.sum;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotNull;

public class ExperienceSumReviseForm implements BindUser {

    @NotNull(message = "ID不能为空")
    private Long sumId;

    private Long userId;

    public Long getSumId() {
        return sumId;
    }

    public void setSumId(Long sumId) {
        this.sumId = sumId;
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
