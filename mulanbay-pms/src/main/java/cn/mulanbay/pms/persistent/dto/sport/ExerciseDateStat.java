package cn.mulanbay.pms.persistent.dto.sport;

import cn.mulanbay.pms.persistent.dto.common.CalendarDateStat;
import cn.mulanbay.pms.persistent.dto.common.DateStat;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ExerciseDateStat implements DateStat, CalendarDateStat {

    private Number indexValue;
    private BigDecimal totalValue;
    private Long totalCount;
    private BigDecimal totalMaxHeartRate;
    private BigInteger totalAvgHeartRate;
    private BigDecimal totalSpeed;
    private BigInteger totalDuration;
    private BigDecimal totalPace;

    public ExerciseDateStat(Number indexValue, BigDecimal totalValue, Long totalCount, BigDecimal totalMaxHeartRate, BigInteger totalAvgHeartRate, BigDecimal totalSpeed, BigInteger totalDuration, BigDecimal totalPace) {
        this.indexValue = indexValue;
        this.totalValue = totalValue;
        this.totalCount = totalCount;
        this.totalMaxHeartRate = totalMaxHeartRate;
        this.totalAvgHeartRate = totalAvgHeartRate;
        this.totalSpeed = totalSpeed;
        this.totalDuration = totalDuration;
        this.totalPace = totalPace;
    }

    public Number getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Number indexValue) {
        this.indexValue = indexValue;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalMaxHeartRate() {
        return totalMaxHeartRate;
    }

    public void setTotalMaxHeartRate(BigDecimal totalMaxHeartRate) {
        this.totalMaxHeartRate = totalMaxHeartRate;
    }

    public BigInteger getTotalAvgHeartRate() {
        return totalAvgHeartRate;
    }

    public void setTotalAvgHeartRate(BigInteger totalAvgHeartRate) {
        this.totalAvgHeartRate = totalAvgHeartRate;
    }

    public BigDecimal getTotalSpeed() {
        return totalSpeed;
    }

    public void setTotalSpeed(BigDecimal totalSpeed) {
        this.totalSpeed = totalSpeed;
    }

    public BigInteger getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(BigInteger totalDuration) {
        this.totalDuration = totalDuration;
    }

    public BigDecimal getTotalPace() {
        return totalPace;
    }

    public void setTotalPace(BigDecimal totalPace) {
        this.totalPace = totalPace;
    }

    @Override
    public double getCalendarStatValue() {
        return 0;
    }

    @Override
    public int getDayIndexValue() {
        return 0;
    }

    @Override
    public Integer getDateIndexValue() {
        return null;
    }
}
