package cn.mulanbay.pms.persistent.dto.body;

import java.math.BigDecimal;

public class BodyInfoAvgStat {

    private BigDecimal avgWeight;
    private BigDecimal avgHeight;

    public BodyInfoAvgStat(BigDecimal avgWeight, BigDecimal avgHeight) {
        this.avgWeight = avgWeight;
        this.avgHeight = avgHeight;
    }

    public BigDecimal getAvgWeight() {
        return avgWeight;
    }

    public void setAvgWeight(BigDecimal avgWeight) {
        this.avgWeight = avgWeight;
    }

    public BigDecimal getAvgHeight() {
        return avgHeight;
    }

    public void setAvgHeight(BigDecimal avgHeight) {
        this.avgHeight = avgHeight;
    }
}
