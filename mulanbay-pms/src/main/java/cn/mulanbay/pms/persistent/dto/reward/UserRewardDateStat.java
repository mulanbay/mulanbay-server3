package cn.mulanbay.pms.persistent.dto.reward;

import cn.mulanbay.pms.persistent.dto.common.CalendarDateStat;
import cn.mulanbay.pms.persistent.dto.common.DateStat;

import java.math.BigDecimal;

public class UserRewardDateStat implements DateStat, CalendarDateStat {
    // 月份
    private Number indexValue;
    private BigDecimal totalRewards;
    private Long totalCount;

    public UserRewardDateStat(Number indexValue, BigDecimal totalRewards, Long totalCount) {
        this.indexValue = indexValue;
        this.totalRewards = totalRewards;
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

    public BigDecimal getTotalRewards() {
        return totalRewards;
    }

    public void setTotalRewards(BigDecimal totalRewards) {
        this.totalRewards = totalRewards;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public double getCalendarStatValue() {
        return totalRewards.longValue();
    }

    @Override
    public int getDayIndexValue() {
        return this.getDateIndexValue();
    }
}
