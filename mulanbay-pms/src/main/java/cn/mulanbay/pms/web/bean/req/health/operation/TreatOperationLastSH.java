package cn.mulanbay.pms.web.bean.req.health.operation;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotNull;

public class TreatOperationLastSH implements BindUser {

    @NotNull(message = "名称不能为空")
    private String operationName;

    private Long userId;

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
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
