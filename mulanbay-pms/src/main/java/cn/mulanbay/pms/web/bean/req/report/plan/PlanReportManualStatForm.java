package cn.mulanbay.pms.web.bean.req.report.plan;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.ManualStatType;
import cn.mulanbay.pms.persistent.enums.PlanValueCompareType;
import cn.mulanbay.pms.persistent.enums.PlanType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class PlanReportManualStatForm implements BindUser {

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    private Date startDate;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    private Date endDate;

    private Long planId;

    private Long userId;

    private ManualStatType statType;

    private PlanType planType;

    private PlanValueCompareType reStatCompareType;

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

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ManualStatType getStatType() {
        return statType;
    }

    public void setStatType(ManualStatType statType) {
        this.statType = statType;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public PlanValueCompareType getReStatCompareType() {
        return reStatCompareType;
    }

    public void setReStatCompareType(PlanValueCompareType reStatCompareType) {
        this.reStatCompareType = reStatCompareType;
    }
}
