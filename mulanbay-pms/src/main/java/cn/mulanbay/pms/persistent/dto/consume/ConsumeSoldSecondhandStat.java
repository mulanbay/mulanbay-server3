package cn.mulanbay.pms.persistent.dto.consume;

import java.math.BigDecimal;

public class ConsumeSoldSecondhandStat {

    // 0非二手 1二手
    private Boolean secondhand;

    private Long totalCount;

    // 价格，精确到元，可以为运费、总价
    private BigDecimal totalPrice;

    public ConsumeSoldSecondhandStat(Boolean secondhand, Long totalCount, BigDecimal totalPrice) {
        this.secondhand = secondhand;
        this.totalCount = totalCount;
        this.totalPrice = totalPrice;
    }

    public Boolean getSecondhand() {
        return secondhand;
    }

    public void setSecondhand(Boolean secondhand) {
        this.secondhand = secondhand;
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
