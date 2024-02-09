package cn.mulanbay.pms.persistent.dto.body;

import cn.mulanbay.pms.persistent.dto.common.CalendarDateStat;
import cn.mulanbay.pms.persistent.dto.common.DateStat;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BodyAbnormalDateStat implements DateStat, CalendarDateStat {

    private Number indexValue;
    private BigInteger totalCount;
    private BigDecimal totalDays;

    public BodyAbnormalDateStat(Number indexValue, BigInteger totalCount, BigDecimal totalDays) {
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

    public BigInteger getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(BigInteger totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(BigDecimal totalDays) {
        this.totalDays = totalDays;
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
