package cn.mulanbay.pms.persistent.dto.commonData;

import java.math.BigDecimal;

public class CommonDataAnalyseStat {

    private String name;

    private Long totalCount;

    private BigDecimal totalValue;

    public CommonDataAnalyseStat(String name, Long totalCount, BigDecimal totalValue) {
        this.name = name;
        this.totalCount = totalCount;
        this.totalValue = totalValue;
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

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
}
