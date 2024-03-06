package cn.mulanbay.pms.persistent.dto.life;

import java.math.BigDecimal;

public class ExperienceCostStat {

    private Number indexValue;
    private String name;
    private BigDecimal totalCost;

    public ExperienceCostStat(Number indexValue, BigDecimal totalCost) {
        this.indexValue = indexValue;
        this.totalCost = totalCost;
    }

    public ExperienceCostStat(Number indexValue, String name, BigDecimal totalCost) {
        this.indexValue = indexValue;
        this.name = name;
        this.totalCost = totalCost;
    }

    public Number getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Number indexValue) {
        this.indexValue = indexValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
