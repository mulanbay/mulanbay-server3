package cn.mulanbay.pms.web.bean.req.report.plan;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.PlanType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class PlanReportTimelineStatSH implements BindUser {

    @NotEmpty(message = "业务key不能为空")
    private String bussKey;

    public Long userId;

    @NotNull(message = "用户计划不能为空")
    private Long planId;

    private PlanType planType;

    /**
     * 是否要预测
     */
    private Boolean predict = false;

    public String getBussKey() {
        return bussKey;
    }

    public void setBussKey(String bussKey) {
        this.bussKey = bussKey;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public Boolean getPredict() {
        return predict;
    }

    public void setPredict(Boolean predict) {
        this.predict = predict;
    }
}
