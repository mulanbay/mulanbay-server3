package cn.mulanbay.pms.persistent.dto.life;

import java.math.BigDecimal;

public class ExperienceWorldMapStat {

    private Number countryId;

    private String countryName;

    private String countryLocation;
    //次数
    private Long totalCount;

    //天数
    private Long totalDays;

    private BigDecimal totalCost;

    public ExperienceWorldMapStat(Number countryId, String countryLocation, Long totalCount, Long totalDays, BigDecimal totalCost) {
        this.countryId = countryId;
        this.countryLocation = countryLocation;
        this.totalCount = totalCount;
        this.totalDays = totalDays;
        this.totalCost = totalCost;
    }

    public Number getCountryId() {
        return countryId;
    }

    public void setCountryId(Number countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryLocation() {
        return countryLocation;
    }

    public void setCountryLocation(String countryLocation) {
        this.countryLocation = countryLocation;
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
}
