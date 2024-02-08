package cn.mulanbay.pms.persistent.dto.sport;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ExerciseStat {

    private Long totalCount;

    private BigInteger totalDuration;

    private BigDecimal totalValue;

    public ExerciseStat(Long totalCount, BigInteger totalDuration, BigDecimal totalValue) {
        this.totalCount = totalCount;
        this.totalDuration = totalDuration;
        this.totalValue = totalValue;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigInteger getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(BigInteger totalDuration) {
        this.totalDuration = totalDuration;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
}
