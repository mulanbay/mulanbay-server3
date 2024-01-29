package cn.mulanbay.pms.handler.bean.consume;

import java.math.BigDecimal;

public class ConsumeBean {

    //普通消费
    private BigDecimal ncAmount;
    //突发消费
    private BigDecimal bcAmount;
    //看病
    private BigDecimal treatAmount;

    public BigDecimal getNcAmount() {
        return ncAmount;
    }

    public void setNcAmount(BigDecimal ncAmount) {
        this.ncAmount = ncAmount;
    }

    public BigDecimal getBcAmount() {
        return bcAmount;
    }

    public void setBcAmount(BigDecimal bcAmount) {
        this.bcAmount = bcAmount;
    }

    public BigDecimal getTreatAmount() {
        return treatAmount;
    }

    public void setTreatAmount(BigDecimal treatAmount) {
        this.treatAmount = treatAmount;
    }
}
