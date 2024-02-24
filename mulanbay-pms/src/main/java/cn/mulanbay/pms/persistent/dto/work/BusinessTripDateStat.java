package cn.mulanbay.pms.persistent.dto.work;

import cn.mulanbay.pms.persistent.dto.common.CalendarDateStat;
import cn.mulanbay.pms.persistent.dto.common.DateStat;

import java.math.BigDecimal;

public class BusinessTripDateStat implements DateStat, CalendarDateStat {
    // 月份
    private Number indexValue;

    private Long totalCount;

    private BigDecimal totalDays;

    public BusinessTripDateStat(Number indexValue, Long totalCount, BigDecimal totalDays) {
        this.indexValue = indexValue;
        this.totalCount = totalCount;
        this.totalDays = totalDays;
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

    public BigDecimal getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(BigDecimal totalDays) {
        this.totalDays = totalDays;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public double getCalendarStatValue() {
        return totalDays.doubleValue();
    }

    @Override
    public int getDayIndexValue() {
        return this.getDateIndexValue();
    }
}
