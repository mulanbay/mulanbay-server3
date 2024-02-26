package cn.mulanbay.pms.persistent.dto.commonData;

import cn.mulanbay.pms.persistent.dto.common.CalendarDateStat;
import cn.mulanbay.pms.persistent.dto.common.DateStat;

import java.math.BigDecimal;
import java.math.BigInteger;

public class CommonDataDateStat implements DateStat, CalendarDateStat {

    private Number indexValue;
    private Long totalCount;
    private BigDecimal totalValue;

    public CommonDataDateStat(Number indexValue, Long totalCount, BigDecimal totalValue) {
        this.indexValue = indexValue;
        this.totalCount = totalCount;
        this.totalValue = totalValue;
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

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    @Override
    public double getCalendarStatValue() {
        return totalValue.doubleValue();
    }

    @Override
    public int getDayIndexValue() {
        return this.getDateIndexValue();
    }
}
