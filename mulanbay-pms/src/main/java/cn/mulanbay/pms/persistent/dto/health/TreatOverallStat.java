package cn.mulanbay.pms.persistent.dto.health;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TreatOverallStat {

    private Integer indexValue;
    private String name;
    private BigInteger totalCount;
    private BigDecimal totalFee;

    public TreatOverallStat(Integer indexValue, String name, BigInteger totalCount, BigDecimal totalFee) {
        this.indexValue = indexValue;
        this.name = name;
        this.totalCount = totalCount;
        this.totalFee = totalFee;
    }

    public Integer getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Integer indexValue) {
        this.indexValue = indexValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(BigInteger totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

}
