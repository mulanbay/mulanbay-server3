package cn.mulanbay.pms.persistent.dto.health;

import java.math.BigDecimal;
import java.util.Date;

public class TreatOperationStat {

    private String name;

    private Long totalCount;

    private BigDecimal totalFee;

    private Date minDate;

    private Date maxDate;

    public TreatOperationStat() {
    }

    public TreatOperationStat(String name, Long totalCount, BigDecimal totalFee, Date minDate, Date maxDate) {
        this.name = name;
        this.totalCount = totalCount;
        this.totalFee = totalFee;
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
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
}
