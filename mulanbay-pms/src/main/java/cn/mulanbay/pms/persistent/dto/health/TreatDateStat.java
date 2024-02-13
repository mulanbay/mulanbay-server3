package cn.mulanbay.pms.persistent.dto.health;

import cn.mulanbay.pms.persistent.dto.common.CalendarDateStat;
import cn.mulanbay.pms.persistent.dto.common.DateStat;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TreatDateStat implements DateStat, CalendarDateStat {
    // 月份
    private Number indexValue;
    private Long totalCount;
    private BigDecimal totalFee;

    public TreatDateStat(Number indexValue, Long totalCount, BigDecimal totalFee) {
        this.indexValue = indexValue;
        this.totalCount = totalCount;
        this.totalFee = totalFee;
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

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public double getCalendarStatValue() {
        return totalCount.doubleValue();
    }

    @Override
    public int getDayIndexValue() {
        return this.getDateIndexValue();
    }
}
