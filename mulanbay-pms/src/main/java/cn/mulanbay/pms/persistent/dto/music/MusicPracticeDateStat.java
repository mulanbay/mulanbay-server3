package cn.mulanbay.pms.persistent.dto.music;

import cn.mulanbay.pms.persistent.dto.common.CalendarDateStat;
import cn.mulanbay.pms.persistent.dto.common.DateStat;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MusicPracticeDateStat implements DateStat, CalendarDateStat {
    // 月份
    private Number indexValue;
    private BigDecimal totalMinutes;
    private Long totalCount;

    public MusicPracticeDateStat(Number indexValue, BigDecimal totalMinutes, Long totalCount) {
        this.indexValue = indexValue;
        this.totalMinutes = totalMinutes;
        this.totalCount = totalCount;
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

    public BigDecimal getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(BigDecimal totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public double getCalendarStatValue() {
        return totalMinutes.doubleValue();
    }

    @Override
    public int getDayIndexValue() {
        return this.getDateIndexValue();
    }
}
