package cn.mulanbay.pms.persistent.dto.report;


public class PlanReportResultGroupStat {

    private Long totalCount;

    private Short resultType;

    public PlanReportResultGroupStat(Long totalCount, Short resultType) {
        this.totalCount = totalCount;
        this.resultType = resultType;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Short getResultType() {
        return resultType;
    }

    public void setResultType(Short resultType) {
        this.resultType = resultType;
    }
}
