package cn.mulanbay.pms.persistent.dto.log;

import cn.mulanbay.pms.persistent.dto.common.CalendarDateStat;
import cn.mulanbay.pms.persistent.dto.common.DateStat;

import java.math.BigInteger;

public class OperLogDateStat implements DateStat, CalendarDateStat {

    // 月份
    private Long indexValue;
    private Long totalCount;

    public OperLogDateStat(Long indexValue, Long totalCount) {
        this.indexValue = indexValue;
        this.totalCount = totalCount;
    }

    @Override
    public Integer getDateIndexValue() {
        return indexValue==null ? null : indexValue.intValue();
    }

    public Long getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Long indexValue) {
        this.indexValue = indexValue;
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
