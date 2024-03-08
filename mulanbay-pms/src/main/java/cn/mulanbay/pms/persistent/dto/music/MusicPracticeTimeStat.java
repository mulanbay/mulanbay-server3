package cn.mulanbay.pms.persistent.dto.music;

import cn.mulanbay.pms.persistent.dto.common.DateStat;

public class MusicPracticeTimeStat implements DateStat {
    // 月份
    private Number indexValue;
    private Long totalCount;

    public MusicPracticeTimeStat(Number indexValue, Long totalCount) {
        this.indexValue = indexValue;
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

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
