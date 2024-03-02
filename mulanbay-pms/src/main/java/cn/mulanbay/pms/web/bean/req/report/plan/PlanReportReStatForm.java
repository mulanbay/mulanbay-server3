package cn.mulanbay.pms.web.bean.req.report.plan;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotNull;

public class PlanReportReStatForm implements BindUser {

    @NotNull(message = "报告编号不能为空")
    private Long reportId;

    private Long userId;

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
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
