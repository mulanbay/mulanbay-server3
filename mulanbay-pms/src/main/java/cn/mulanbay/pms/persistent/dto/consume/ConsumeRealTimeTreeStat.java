package cn.mulanbay.pms.persistent.dto.consume;

import java.math.BigDecimal;

public class ConsumeRealTimeTreeStat {

    private Long goodsTypeId;

    private Long parentGoodsTypeId;

    private String goodsName;

    private String parentGoodsTypeName;

    private BigDecimal value;

    public ConsumeRealTimeTreeStat(Long goodsTypeId, BigDecimal value) {
        this.goodsTypeId = goodsTypeId;
        this.value = value;
    }

    public Long getGoodsTypeId() {
        return goodsTypeId;
    }

    public void setGoodsTypeId(Long goodsTypeId) {
        this.goodsTypeId = goodsTypeId;
    }

    public Long getParentGoodsTypeId() {
        return parentGoodsTypeId;
    }

    public void setParentGoodsTypeId(Long parentGoodsTypeId) {
        this.parentGoodsTypeId = parentGoodsTypeId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getParentGoodsTypeName() {
        return parentGoodsTypeName;
    }

    public void setParentGoodsTypeName(String parentGoodsTypeName) {
        this.parentGoodsTypeName = parentGoodsTypeName;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
