package cn.mulanbay.pms.persistent.dto.fund;

import java.math.BigDecimal;

public class IncomeSummaryStat {

    private Long totalCount;

    private BigDecimal totalAmount;

    public IncomeSummaryStat(Long totalCount, BigDecimal totalAmount) {
        this.totalCount = totalCount;
        this.totalAmount = totalAmount;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
