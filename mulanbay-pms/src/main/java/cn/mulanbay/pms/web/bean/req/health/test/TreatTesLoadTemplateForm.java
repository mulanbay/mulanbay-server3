package cn.mulanbay.pms.web.bean.req.health.test;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotNull;

public class TreatTesLoadTemplateForm implements BindUser {

    private Long userId;

    @NotNull(message = "手术编号不能为空")
    private Long operationId;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId( Long operationId) {
        this.operationId = operationId;
    }
}
