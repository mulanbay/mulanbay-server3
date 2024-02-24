package cn.mulanbay.pms.persistent.dto.work;

import java.math.BigDecimal;

public class BusinessTripMapStat {

    private Number id;

    private String name;

    //次数
    private Long totalCount;

    //天数
    private BigDecimal totalDays;

    //地理位置坐标
    private String location;

    public BusinessTripMapStat() {
    }

    public BusinessTripMapStat(Number id, Long totalCount, BigDecimal totalDays) {
        this.id = id;
        this.totalCount = totalCount;
        this.totalDays = totalDays;
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

    public BigDecimal getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(BigDecimal totalDays) {
        this.totalDays = totalDays;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
