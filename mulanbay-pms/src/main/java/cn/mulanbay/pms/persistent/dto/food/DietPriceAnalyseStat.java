package cn.mulanbay.pms.persistent.dto.food;

import cn.mulanbay.pms.persistent.dto.common.CalendarDateStat;
import cn.mulanbay.pms.persistent.dto.common.DateStat;

import java.math.BigDecimal;

public class DietPriceAnalyseStat implements DateStat, CalendarDateStat {

    // 可以为天(20100101),周(1-36),月(1-12),年(2014)
    private Number indexValue;

    private Long totalCount;

    private BigDecimal totalPrice;

    public DietPriceAnalyseStat(Number indexValue, Long totalCount, BigDecimal totalPrice) {
        this.indexValue = indexValue;
        this.totalCount = totalCount;
        this.totalPrice = totalPrice;
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
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
