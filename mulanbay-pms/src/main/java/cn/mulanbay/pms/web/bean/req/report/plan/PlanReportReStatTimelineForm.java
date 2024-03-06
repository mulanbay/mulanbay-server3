package cn.mulanbay.pms.web.bean.req.report.plan;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class PlanReportReStatTimelineForm implements BindUser {

    @NotEmpty(message = "运营日期不能为空")
    private String bussKey;

    @NotNull(message = "计划编号不能为空")
    private Long planId;

    public Long userId;

    public String getBussKey() {
        return bussKey;
    }

    public void setBussKey(String bussKey) {
        this.bussKey = bussKey;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
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
