package cn.mulanbay.pms.persistent.dto.consume;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ConsumeUseTimeStat {

    private Object name;
    private Long totalDuration;
    private Long totalCount;

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }


    public Long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
