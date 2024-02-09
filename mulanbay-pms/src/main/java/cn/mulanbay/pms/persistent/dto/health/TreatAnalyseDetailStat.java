package cn.mulanbay.pms.persistent.dto.health;

import cn.mulanbay.pms.persistent.domain.Treat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class TreatAnalyseDetailStat {

    private BigInteger totalCount;

    private BigDecimal totalFee;

    private Date minTreatTime;

    private Date maxTreatTime;

    //最早一次
    private Treat minTreat;

    //最近一次
    private Treat maxTreat;

    public TreatAnalyseDetailStat(BigInteger totalCount, BigDecimal totalFee, Date minTreatTime, Date maxTreatTime) {
        this.totalCount = totalCount;
        this.totalFee = totalFee;
        this.minTreatTime = minTreatTime;
        this.maxTreatTime = maxTreatTime;
    }

    public BigInteger getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(BigInteger totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public Date getMinTreatTime() {
        return minTreatTime;
    }

    public void setMinTreatTime(Date minTreatTime) {
        this.minTreatTime = minTreatTime;
    }

    public Date getMaxTreatTime() {
        return maxTreatTime;
    }

    public void setMaxTreatTime(Date maxTreatTime) {
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
