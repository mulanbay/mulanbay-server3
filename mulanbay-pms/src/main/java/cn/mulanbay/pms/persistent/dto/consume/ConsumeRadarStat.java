package cn.mulanbay.pms.persistent.dto.consume;

import cn.mulanbay.pms.persistent.dto.common.DateStat;

import java.math.BigDecimal;

public class ConsumeRadarStat implements DateStat {

    // 商品类型，来源类型等
    private Long groupId;

    // 可以为天(20100101),周(1-36),月(1-12),年(2014)
    private Long indexValue;

    private Long totalCount;

    // 价格，精确到元，可以为运费、总价
    private BigDecimal totalPrice;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public Integer getDateIndexValue() {
        return indexValue==null ? null : indexValue.intValue();
    }

    public Long getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Long indexValue) {
        this.indexValue = indexValue;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

}
