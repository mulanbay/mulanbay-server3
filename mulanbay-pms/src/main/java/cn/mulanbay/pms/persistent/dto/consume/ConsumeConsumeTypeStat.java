package cn.mulanbay.pms.persistent.dto.consume;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ConsumeConsumeTypeStat {

    private Short consumeType;

    private Long totalCount;

    // 价格，精确到元，可以为运费、总价
    private BigDecimal totalPrice;

    public ConsumeConsumeTypeStat(Short consumeType, Long totalCount, BigDecimal totalPrice) {
        this.consumeType = consumeType;
        this.totalCount = totalCount;
        this.totalPrice = totalPrice;
    }

    public Short getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(Short consumeType) {
        this.consumeType = consumeType;
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
