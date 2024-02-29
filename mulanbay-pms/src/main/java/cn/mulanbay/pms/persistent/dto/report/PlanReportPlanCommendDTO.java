package cn.mulanbay.pms.persistent.dto.report;

import java.math.BigDecimal;

public class PlanReportPlanCommendDTO {

    private BigDecimal reportCountValue;

    private BigDecimal reportValue;

    public PlanReportPlanCommendDTO(BigDecimal reportCountValue, BigDecimal reportValue) {
        this.reportCountValue = reportCountValue;
        this.reportValue = reportValue;
    }

    public BigDecimal getReportCountValue() {
        return reportCountValue;
    }

    public void setReportCountValue(BigDecimal reportCountValue) {
        this.reportCountValue = reportCountValue;
    }

    public BigDecimal getReportValue() {
        return reportValue;
    }

    public void setReportValue(BigDecimal reportValue) {
        this.reportValue = reportValue;
    }
}
