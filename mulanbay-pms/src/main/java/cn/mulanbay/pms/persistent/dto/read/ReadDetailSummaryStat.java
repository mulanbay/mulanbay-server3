package cn.mulanbay.pms.persistent.dto.read;

import java.math.BigDecimal;

public class ReadDetailSummaryStat {

    private Long totalCount;

    private BigDecimal totalDuration;

    public ReadDetailSummaryStat(Long totalCount, BigDecimal totalDuration) {
        this.totalCount = totalCount;
        this.totalDuration = totalDuration;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(BigDecimal totalDuration) {
        this.totalDuration = totalDuration;
    }

}
