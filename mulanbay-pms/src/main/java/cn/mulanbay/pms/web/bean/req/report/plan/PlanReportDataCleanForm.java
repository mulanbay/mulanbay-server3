package cn.mulanbay.pms.web.bean.req.report.plan;

import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.PlanReportDataCleanType;
import cn.mulanbay.pms.persistent.enums.PlanType;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class PlanReportDataCleanForm extends QueryBuilder {

    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "bussStatDate", op = Parameter.Operator.GTE)
    private Date startDate;

    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "bussStatDate", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "template.templateId", op = Parameter.Operator.EQ)
    private Long templateId;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    public Long userId;

    @Query(fieldName = "template.planType", op = Parameter.Operator.EQ)
    private PlanType planType;

    private PlanReportDataCleanType cleanType;

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

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public PlanReportDataCleanType getCleanType() {
        return cleanType;
    }

    public void setCleanType(PlanReportDataCleanType cleanType) {
        this.cleanType = cleanType;
    }
}
