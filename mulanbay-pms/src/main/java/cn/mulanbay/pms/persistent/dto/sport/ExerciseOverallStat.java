package cn.mulanbay.pms.persistent.dto.sport;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ExerciseOverallStat{

    private Integer indexValue;

    private Long sportId;

    private Long totalCount;

    private BigDecimal totalValue;
    private BigInteger totalDuration;

    public ExerciseOverallStat(Integer indexValue, Long sportId, Long totalCount, BigDecimal totalValue, BigInteger totalDuration) {
        this.indexValue = indexValue;
        this.sportId = sportId;
        this.totalCount = totalCount;
        this.totalValue = totalValue;
        this.totalDuration = totalDuration;
    }

    public Integer getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Integer indexValue) {
        this.indexValue = indexValue;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigInteger getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(BigInteger totalDuration) {
        this.totalDuration = totalDuration;
    }
}
