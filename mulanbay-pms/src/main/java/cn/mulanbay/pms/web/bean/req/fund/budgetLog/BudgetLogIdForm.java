package cn.mulanbay.pms.web.bean.req.fund.budgetLog;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotNull;

public class BudgetLogIdForm implements BindUser {

    @NotNull(message = "日志流水编号不能为空")
    private Long logId;

    private Long userId;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
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
