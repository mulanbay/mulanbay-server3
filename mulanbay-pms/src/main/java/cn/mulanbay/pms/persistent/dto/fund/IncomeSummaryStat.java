package cn.mulanbay.pms.persistent.dto.fund;

import java.math.BigDecimal;
import java.math.BigInteger;

public class IncomeSummaryStat {

    private BigInteger totalCount;

    private BigDecimal totalAmount;

    public IncomeSummaryStat(BigInteger totalCount, BigDecimal totalAmount) {
        this.totalCount = totalCount;
        this.totalAmount = totalAmount;
    }

    public BigInteger getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(BigInteger totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
