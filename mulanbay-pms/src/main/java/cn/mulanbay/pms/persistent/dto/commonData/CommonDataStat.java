package cn.mulanbay.pms.persistent.dto.commonData;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class CommonDataStat {

    //最先开始时间
    private Date minDate;

    //最晚开始时间
    private Date maxDate;

    private Long totalCount;

    private BigDecimal totalValue;

    public CommonDataStat(Date minDate, Date maxDate, Long totalCount, BigDecimal totalValue) {
        this.minDate = minDate;
        this.maxDate = maxDate;
        this.totalCount = totalCount;
        this.totalValue = totalValue;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
}
