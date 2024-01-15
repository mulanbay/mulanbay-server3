package cn.mulanbay.pms.web.bean.req.system.command;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CommandExeForm implements BindUser {

    @NotEmpty(message = "代码不能为空")
    private String code;

    private boolean sync;

    private Long userId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
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
