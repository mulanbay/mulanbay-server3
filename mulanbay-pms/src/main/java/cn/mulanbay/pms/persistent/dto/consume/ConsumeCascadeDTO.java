package cn.mulanbay.pms.persistent.dto.consume;

import java.math.BigDecimal;

public class ConsumeCascadeDTO {

    private Long consumeId;

    private Long pid;

    private String goodsName;

    private BigDecimal totalPrice;

    public ConsumeCascadeDTO() {
    }

    public ConsumeCascadeDTO(Long consumeId, Long pid, String goodsName, BigDecimal totalPrice) {
        this.consumeId = consumeId;
        this.pid = pid;
        this.goodsName = goodsName;
        this.totalPrice = totalPrice;
    }

    public Long getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(Long consumeId) {
        this.consumeId = consumeId;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
