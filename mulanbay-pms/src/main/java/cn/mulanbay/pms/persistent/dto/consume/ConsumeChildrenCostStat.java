package cn.mulanbay.pms.persistent.dto.consume;

import java.math.BigDecimal;

public class ConsumeChildrenCostStat {

    // 价格，精确到元，可以为运费、总价
    private BigDecimal totalPrice;

    private BigDecimal soldPrice;

    private Long totalCount;

    public ConsumeChildrenCostStat() {
    }

    public ConsumeChildrenCostStat(BigDecimal totalPrice, BigDecimal soldPrice, Long totalCount) {
        this.totalPrice = totalPrice;
        this.soldPrice = soldPrice;
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(BigDecimal soldPrice) {
        this.soldPrice = soldPrice;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
