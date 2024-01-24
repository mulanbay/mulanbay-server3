package cn.mulanbay.pms.persistent.dto.consume;

import java.math.BigDecimal;

public class ConsumeUseTimeStat {

    private Object name;
    private BigDecimal totalDuration;
    private Long totalCount;

    public ConsumeUseTimeStat(Object name, BigDecimal totalDuration, Long totalCount) {
        this.name = name;
        this.totalDuration = totalDuration;
        this.totalCount = totalCount;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }


    public BigDecimal getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(BigDecimal totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
