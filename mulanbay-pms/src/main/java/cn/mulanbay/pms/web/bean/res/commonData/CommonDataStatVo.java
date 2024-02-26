package cn.mulanbay.pms.web.bean.res.commonData;

import java.math.BigDecimal;
import java.util.Date;

public class CommonDataStatVo {

    private String unit;

    //最先开始时间
    private Date minDate;

    //最晚开始时间
    private Date maxDate;

    private Long totalCount;

    private BigDecimal totalValue;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
