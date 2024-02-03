package cn.mulanbay.pms.persistent.dto.music;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class MusicPracticeTuneNameStat {

    private String tune;

    private Date minPracticeDate;

    private Date maxPracticeDate;

    //次数
    private BigDecimal totalTimes;

    private Long totalCounts;

    public MusicPracticeTuneNameStat(String tune, Date minPracticeDate, Date maxPracticeDate, BigDecimal totalTimes, Long totalCounts) {
        this.tune = tune;
        this.minPracticeDate = minPracticeDate;
        this.maxPracticeDate = maxPracticeDate;
        this.totalTimes = totalTimes;
        this.totalCounts = totalCounts;
    }

    public Date getMinPracticeDate() {
        return minPracticeDate;
    }

    public void setMinPracticeDate(Date minPracticeDate) {
        this.minPracticeDate = minPracticeDate;
    }

    public Date getMaxPracticeDate() {
        return maxPracticeDate;
    }

    public void setMaxPracticeDate(Date maxPracticeDate) {
        this.maxPracticeDate = maxPracticeDate;
    }

    public BigDecimal getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(BigDecimal totalTimes) {
        this.totalTimes = totalTimes;
    }

    public String getTune() {
        return tune;
    }

    public void setTune(String tune) {
        this.tune = tune;
    }

    public Long getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(Long totalCounts) {
        this.totalCounts = totalCounts;
    }
}
