package cn.mulanbay.pms.persistent.dto.body;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class BodyAbnormalStat {

    private String name;

    private BigInteger totalCount;

    private BigDecimal totalDays;

    private Date maxOccurDate;

    private Date minOccurDate;

    public BodyAbnormalStat(String name, BigInteger totalCount, BigDecimal totalDays, Date maxOccurDate, Date minOccurDate) {
        this.name = name;
        this.totalCount = totalCount;
        this.totalDays = totalDays;
        this.maxOccurDate = maxOccurDate;
        this.minOccurDate = minOccurDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(BigInteger totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(BigDecimal totalDays) {
        this.totalDays = totalDays;
    }

    public Date getMaxOccurDate() {
        return maxOccurDate;
    }

    public void setMaxOccurDate(Date maxOccurDate) {
        this.maxOccurDate = maxOccurDate;
    }

    public Date getMinOccurDate() {
        return minOccurDate;
    }

    public void setMinOccurDate(Date minOccurDate) {
        this.minOccurDate = minOccurDate;
    }
}
