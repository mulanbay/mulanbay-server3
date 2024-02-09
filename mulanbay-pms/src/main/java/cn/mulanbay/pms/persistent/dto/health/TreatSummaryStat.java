package cn.mulanbay.pms.persistent.dto.health;

import java.math.BigDecimal;
import java.util.Date;

public class TreatSummaryStat {

    private Long totalCount;

    private BigDecimal totalRegFee;

    private BigDecimal totalDrugFee;

    private BigDecimal totalOperationFee;

    private BigDecimal totalTotalFee;

    private BigDecimal totalMiFee;

    private BigDecimal totalPdFee;

    private Date maxTreatTime;

    public TreatSummaryStat() {
    }

    public TreatSummaryStat(Long totalCount, BigDecimal totalRegFee, BigDecimal totalDrugFee, BigDecimal totalOperationFee, BigDecimal totalTotalFee, BigDecimal totalMiFee, BigDecimal totalPdFee, Date maxTreatTime) {
        this.totalCount = totalCount;
        this.totalRegFee = totalRegFee;
        this.totalDrugFee = totalDrugFee;
        this.totalOperationFee = totalOperationFee;
        this.totalTotalFee = totalTotalFee;
        this.totalMiFee = totalMiFee;
        this.totalPdFee = totalPdFee;
        this.maxTreatTime = maxTreatTime;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalRegFee() {
        return totalRegFee;
    }

    public void setTotalRegFee(BigDecimal totalRegFee) {
        this.totalRegFee = totalRegFee;
    }

    public BigDecimal getTotalDrugFee() {
        return totalDrugFee;
    }

    public void setTotalDrugFee(BigDecimal totalDrugFee) {
        this.totalDrugFee = totalDrugFee;
    }

    public BigDecimal getTotalOperationFee() {
        return totalOperationFee;
    }

    public void setTotalOperationFee(BigDecimal totalOperationFee) {
        this.totalOperationFee = totalOperationFee;
    }

    public BigDecimal getTotalTotalFee() {
        return totalTotalFee;
    }

    public void setTotalTotalFee(BigDecimal totalTotalFee) {
        this.totalTotalFee = totalTotalFee;
    }

    public BigDecimal getTotalMiFee() {
        return totalMiFee;
    }

    public void setTotalMiFee(BigDecimal totalMiFee) {
        this.totalMiFee = totalMiFee;
    }

    public BigDecimal getTotalPdFee() {
        return totalPdFee;
    }

    public void setTotalPdFee(BigDecimal totalPdFee) {
        this.totalPdFee = totalPdFee;
    }

    public Date getMaxTreatTime() {
        return maxTreatTime;
    }

    public void setMaxTreatTime(Date maxTreatTime) {
        this.maxTreatTime = maxTreatTime;
    }
}
