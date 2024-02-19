package cn.mulanbay.pms.persistent.dto.life;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ExperienceMapStat {

    private Long id;

    private String name;

    //次数
    private Long totalCount;

    //天数
    private BigInteger totalDays;

    private BigDecimal totalCost;

    public ExperienceMapStat() {
    }

    public ExperienceMapStat(Long id, Long totalCount, BigInteger totalDays, BigDecimal totalCost) {
        this.id = id;
        this.totalCount = totalCount;
        this.totalDays = totalDays;
        this.totalCost = totalCost;
    }

    public ExperienceMapStat(String name, Long totalCount, BigInteger totalDays, BigDecimal totalCost) {
        this.name = name;
        this.totalCount = totalCount;
        this.totalDays = totalDays;
        this.totalCost = totalCost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigInteger getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(BigInteger totalDays) {
        this.totalDays = totalDays;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
