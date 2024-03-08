package cn.mulanbay.pms.persistent.dto.health;

import cn.mulanbay.pms.persistent.domain.Treat;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TreatAnalyseDetailStat {

    private Long totalCount;

    private BigDecimal totalFee;

    private Timestamp minTreatTime;

    private Timestamp maxTreatTime;

    //最早一次
    private Treat minTreat;

    //最近一次
    private Treat maxTreat;

    public TreatAnalyseDetailStat(Long totalCount, BigDecimal totalFee, Timestamp minTreatTime, Timestamp maxTreatTime) {
        this.totalCount = totalCount;
        this.totalFee = totalFee;
        this.minTreatTime = minTreatTime;
        this.maxTreatTime = maxTreatTime;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public Timestamp getMinTreatTime() {
        return minTreatTime;
    }

    public void setMinTreatTime(Timestamp minTreatTime) {
        this.minTreatTime = minTreatTime;
    }

    public Timestamp getMaxTreatTime() {
        return maxTreatTime;
    }

    public void setMaxTreatTime(Timestamp maxTreatTime) {
        this.maxTreatTime = maxTreatTime;
    }

    public Treat getMinTreat() {
        return minTreat;
    }

    public void setMinTreat(Treat minTreat) {
        this.minTreat = minTreat;
    }

    public Treat getMaxTreat() {
        return maxTreat;
    }

    public void setMaxTreat(Treat maxTreat) {
        this.maxTreat = maxTreat;
    }
}
