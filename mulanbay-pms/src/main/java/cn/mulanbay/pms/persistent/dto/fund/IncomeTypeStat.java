package cn.mulanbay.pms.persistent.dto.fund;

import cn.mulanbay.pms.persistent.dto.common.CalendarDateStat;

import java.math.BigDecimal;

public class IncomeTypeStat implements CalendarDateStat {
    // 月份
    private Number indexValue;
    private BigDecimal totalAmount;
    private Long totalCount;

    public IncomeTypeStat(Number indexValue, BigDecimal totalAmount, Long totalCount) {
        this.indexValue = indexValue;
        this.totalAmount = totalAmount;
        this.totalCount = totalCount;
    }

    public Number getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Number indexValue) {
        this.indexValue = indexValue;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public double getCalendarStatValue() {
        return totalAmount.doubleValue();
    }

    @Override
    public int getDayIndexValue() {
        return indexValue.intValue();
    }
}
