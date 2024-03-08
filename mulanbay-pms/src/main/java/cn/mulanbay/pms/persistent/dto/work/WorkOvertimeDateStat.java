package cn.mulanbay.pms.persistent.dto.work;

import cn.mulanbay.pms.persistent.dto.common.CalendarDateStat;
import cn.mulanbay.pms.persistent.dto.common.DateStat;

import java.math.BigDecimal;

public class WorkOvertimeDateStat implements DateStat, CalendarDateStat {
    // 月份
    private Number indexValue;

    private Long totalCount;

    private BigDecimal totalHours;

    public WorkOvertimeDateStat(Number indexValue, Long totalCount, BigDecimal totalHours) {
        this.indexValue = indexValue;
        this.totalCount = totalCount;
        this.totalHours = totalHours;
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

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public double getCalendarStatValue() {
        return totalHours.doubleValue();
    }

    @Override
    public int getDayIndexValue() {
        return this.getDateIndexValue();
    }
}
