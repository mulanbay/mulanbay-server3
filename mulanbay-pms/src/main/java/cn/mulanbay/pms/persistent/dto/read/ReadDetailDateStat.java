package cn.mulanbay.pms.persistent.dto.read;

import cn.mulanbay.pms.persistent.dto.common.DateStat;

import java.math.BigDecimal;

public class ReadDetailDateStat implements DateStat {

    private Number indexValue;

    private Long totalCount;

    private BigDecimal totalDuration;

    public ReadDetailDateStat(Number indexValue, Long totalCount, BigDecimal totalDuration) {
        this.indexValue = indexValue;
        this.totalCount = totalCount;
        this.totalDuration = totalDuration;
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

    public BigDecimal getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(BigDecimal totalDuration) {
        this.totalDuration = totalDuration;
    }
}
