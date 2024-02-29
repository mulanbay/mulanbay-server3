package cn.mulanbay.pms.web.bean.req.report.plan;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class PlanReportPlanCommendForm extends QueryBuilder implements BindUser, FullEndDateTime {

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "buss_stat_date", op = Parameter.Operator.GTE)
    private Date startDate;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "buss_stat_date", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "template_id", op = Parameter.Operator.EQ)
    private Long templateId;

    @Query(fieldName = "user_id", op = Parameter.Operator.EQ)
    public Long userId;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
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
