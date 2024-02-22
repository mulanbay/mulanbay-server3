package cn.mulanbay.pms.persistent.dto.food;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DietPriceAnalyseTypeStat {

    private String name;

    private Long totalCount;

    private BigDecimal totalPrice;

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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
