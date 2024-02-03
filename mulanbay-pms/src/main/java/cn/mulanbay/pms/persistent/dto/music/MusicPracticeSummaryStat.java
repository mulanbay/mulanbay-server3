package cn.mulanbay.pms.persistent.dto.music;

import java.math.BigDecimal;

public class MusicPracticeSummaryStat {

    private Long totalCount;

    private BigDecimal totalMinutes;

    public double avg;

    public MusicPracticeSummaryStat(Long totalCount, BigDecimal totalMinutes) {
        this.totalCount = totalCount;
        this.totalMinutes = totalMinutes;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(BigDecimal totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }
}
