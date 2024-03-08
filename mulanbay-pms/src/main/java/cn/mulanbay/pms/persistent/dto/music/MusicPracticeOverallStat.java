package cn.mulanbay.pms.persistent.dto.music;

import java.math.BigDecimal;

public class MusicPracticeOverallStat {

    private Integer indexValue;

    private Long instrumentId;

    private Long totalCount;
    private BigDecimal totalMinutes;

    public MusicPracticeOverallStat(Integer indexValue, Long instrumentId, Long totalCount, BigDecimal totalMinutes) {
        this.indexValue = indexValue;
        this.instrumentId = instrumentId;
        this.totalCount = totalCount;
        this.totalMinutes = totalMinutes;
    }

    public Integer getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Integer indexValue) {
        this.indexValue = indexValue;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Long instrumentId) {
        this.instrumentId = instrumentId;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(BigDecimal totalMinutes) {
        this.totalMinutes = totalMinutes;
    }
}
