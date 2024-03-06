package cn.mulanbay.pms.persistent.dto.body;

import cn.mulanbay.pms.persistent.dto.common.DateStat;

import java.math.BigDecimal;

public class BodyInfoDateStat implements DateStat {

    private Number indexValue;
    private Long totalCount;
    private BigDecimal totalWeight;
    private BigDecimal totalHeight;
    private BigDecimal totalBmi;

    public BodyInfoDateStat(Number indexValue, Long totalCount, BigDecimal totalWeight, BigDecimal totalHeight, BigDecimal totalBmi) {
        this.indexValue = indexValue;
        this.totalCount = totalCount;
        this.totalWeight = totalWeight;
        this.totalHeight = totalHeight;
        this.totalBmi = totalBmi;
    }

    @Override
    public Integer getDateIndexValue() {
        return indexValue==null ? null : indexValue.intValue();
    }

    public Number getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Number indexValue) {
        this.indexValue = indexValue;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getTotalHeight() {
        return totalHeight;
    }

    public void setTotalHeight(BigDecimal totalHeight) {
        this.totalHeight = totalHeight;
    }

    public BigDecimal getTotalBmi() {
        return totalBmi;
    }

    public void setTotalBmi(BigDecimal totalBmi) {
        this.totalBmi = totalBmi;
    }
}
