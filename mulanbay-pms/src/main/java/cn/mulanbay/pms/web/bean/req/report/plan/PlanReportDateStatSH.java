package cn.mulanbay.pms.web.bean.req.report.plan;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.PlanStatResult;
import cn.mulanbay.pms.persistent.enums.PlanType;
import cn.mulanbay.web.bean.request.PageSearch;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class PlanReportDateStatSH extends PageSearch implements BindUser {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "bussDay", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "bussDay", op = Parameter.Operator.LTE)
    private Date endDate;

    @NotNull(message = "用户计划不能为空")
    @Query(fieldName = "plan.planId", op = Parameter.Operator.EQ)
    private Long planId;

    @Query(fieldName = "plan.template.templateId", op = Parameter.Operator.EQ)
    private Long templateId;

    @Query(fieldName = "plan.planType", op = Parameter.Operator.EQ)
    private PlanType planType;

    @Query(fieldName = "title", op = Parameter.Operator.LIKE)
    private String name;

    @Query(fieldName = "status", op = Parameter.Operator.EQ)
    private CommonStatus status;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    public Long userId;

    @Query(fieldName = "plan.template.bussType", op = Parameter.Operator.EQ)
    private BussType bussType;

    @Query(fieldName = "countValueResult", op = Parameter.Operator.EQ)
    private PlanStatResult countValueResult;

    @Query(fieldName = "valueResult", op = Parameter.Operator.EQ)
    private PlanStatResult valueResult;

    /**
     * 是否要预测
     */
    private Boolean predict = false;

    private boolean original = false;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BussType getBussType() {
        return bussType;
    }

    public void setBussType(BussType bussType) {
        this.bussType = bussType;
    }

    public Boolean getPredict() {
        return predict;
    }

    public void setPredict(Boolean predict) {
        this.predict = predict;
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

    public PlanStatResult getCountValueResult() {
        return countValueResult;
    }

    public void setCountValueResult(PlanStatResult countValueResult) {
        this.countValueResult = countValueResult;
    }

    public PlanStatResult getValueResult() {
        return valueResult;
    }

    public void setValueResult(PlanStatResult valueResult) {
        this.valueResult = valueResult;
    }
}
