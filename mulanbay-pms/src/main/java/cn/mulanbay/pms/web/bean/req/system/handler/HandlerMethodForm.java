package cn.mulanbay.pms.web.bean.req.system.handler;

import jakarta.validation.constraints.NotEmpty;

public class HandlerMethodForm {

    @NotEmpty(message = "类名不能为空")
    private String className;

    @NotEmpty(message = "方法不能为空")
    private String method;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
