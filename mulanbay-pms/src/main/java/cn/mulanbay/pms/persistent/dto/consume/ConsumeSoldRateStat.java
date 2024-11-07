package cn.mulanbay.pms.persistent.dto.consume;

import java.math.BigDecimal;

public class ConsumeSoldRateStat {

    private BigDecimal rate;

    private Long totalCount;

    public ConsumeSoldRateStat(BigDecimal rate, Long totalCount) {
        this.rate = rate;
        this.totalCount = totalCount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
