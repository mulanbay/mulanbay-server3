package cn.mulanbay.pms.persistent.dto.consume;

import java.math.BigDecimal;

public class ConsumeTagsStat {

    private String tags;

    private Long totalCount;

    // 价格，精确到元，可以为运费、总价
    private BigDecimal totalPrice;

    public ConsumeTagsStat(String tags, Long totalCount, BigDecimal totalPrice) {
        this.tags = tags;
        this.totalCount = totalCount;
        this.totalPrice = totalPrice;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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
