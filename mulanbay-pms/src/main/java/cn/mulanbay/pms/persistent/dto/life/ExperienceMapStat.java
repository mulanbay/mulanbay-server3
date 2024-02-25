package cn.mulanbay.pms.persistent.dto.life;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ExperienceMapStat {

    private Number id;

    private String name;

    //次数
    private Long totalCount;

    //天数
    private Long totalDays;

    private BigDecimal totalCost;

    /**
     * 经纬度
     */
    private String location;

    public ExperienceMapStat() {
    }

    public ExperienceMapStat(Number id,Long totalCount, Long totalDays, BigDecimal totalCost) {
        this.id = id;
        this.totalCount = totalCount;
        this.totalDays = totalDays;
        this.totalCost = totalCost;
    }

    public Number getId() {
        return id;
    }

    public void setId(Number id) {
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

    public Long getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Long totalDays) {
        this.totalDays = totalDays;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
