package cn.mulanbay.pms.persistent.dto.health;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class TreatFullStat {

    private String tags;

    private Long totalCount;

    private Date minTreatTime;

    private Date maxTreatTime;

    // 挂号费（原价:个人+医保）
    private BigDecimal regFee;
    // 药费（原价:个人+医保）
    private BigDecimal drugFee;
    // 手术费用（原价:个人+医保）
    private BigDecimal operationFee;
    // 总共花费
    private BigDecimal totalFee;
    // 医保花费
    private BigDecimal miFee;
    // 个人支付费用
    private BigDecimal pdFee;

    public TreatFullStat(String tags, Long totalCount, Date minTreatTime, Date maxTreatTime, BigDecimal regFee, BigDecimal drugFee, BigDecimal operationFee, BigDecimal totalFee, BigDecimal miFee, BigDecimal pdFee) {
        this.tags = tags;
        this.totalCount = totalCount;
        this.minTreatTime = minTreatTime;
        this.maxTreatTime = maxTreatTime;
        this.regFee = regFee;
        this.drugFee = drugFee;
        this.operationFee = operationFee;
        this.totalFee = totalFee;
        this.miFee = miFee;
        this.pdFee = pdFee;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Date getMinTreatTime() {
        return minTreatTime;
    }

    public void setMinTreatTime(Date minTreatTime) {
        this.minTreatTime = minTreatTime;
    }

    public Date getMaxTreatTime() {
        return maxTreatTime;
    }

    public void setMaxTreatTime(Date maxTreatTime) {
        this.maxTreatTime = maxTreatTime;
    }

    public BigDecimal getRegFee() {
        return regFee;
    }

    public void setRegFee(BigDecimal regFee) {
        this.regFee = regFee;
    }

    public BigDecimal getDrugFee() {
        return drugFee;
    }

    public void setDrugFee(BigDecimal drugFee) {
        this.drugFee = drugFee;
    }

    public BigDecimal getOperationFee() {
        return operationFee;
    }

    public void setOperationFee(BigDecimal operationFee) {
        this.operationFee = operationFee;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public BigDecimal getMiFee() {
        return miFee;
    }

    public void setMiFee(BigDecimal miFee) {
        this.miFee = miFee;
    }

    public BigDecimal getPdFee() {
        return pdFee;
    }

    public void setPdFee(BigDecimal pdFee) {
        this.pdFee = pdFee;
    }
}
