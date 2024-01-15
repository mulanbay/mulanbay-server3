package cn.mulanbay.pms.web.bean.req.system.handler;

import jakarta.validation.constraints.NotEmpty;

public class HandlerClassForm {

    @NotEmpty(message = "类名不能为空")
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
