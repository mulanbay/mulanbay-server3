package cn.mulanbay.pms.persistent.dto.work;

import java.math.BigDecimal;
import java.math.BigInteger;

public class WorkOvertimeStat {

    private Long totalCount;

    private BigDecimal totalHours;

    public WorkOvertimeStat(Long totalCount, BigDecimal totalHours) {
        this.totalCount = totalCount;
        this.totalHours = totalHours;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }

}
