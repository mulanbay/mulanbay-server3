package cn.mulanbay.pms.persistent.dto.read;

import java.math.BigDecimal;

public class ReadDetailOverallStat {

    private Number indexValue;
    private BigDecimal totalDuration;
    private Long cateId;

    public ReadDetailOverallStat(Number indexValue, BigDecimal totalDuration, Long cateId) {
        this.indexValue = indexValue;
        this.totalDuration = totalDuration;
        this.cateId = cateId;
    }

    public Number getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Number indexValue) {
        this.indexValue = indexValue;
    }

    public BigDecimal getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(BigDecimal totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Long getCateId() {
        return cateId;
    }

    public void setCateId(Long cateId) {
        this.cateId = cateId;
    }
}
