package cn.mulanbay.pms.handler.bean.consume;

import cn.mulanbay.pms.persistent.enums.GoodsConsumeType;
import cn.mulanbay.pms.persistent.enums.GoodsMatchType;
import cn.mulanbay.pms.persistent.enums.Payment;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 消费记录匹配
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class ConsumeMatchBean implements Serializable {

    private static final long serialVersionUID = 5066451654516299863L;

    private String traceId;
    private String goodsName;

    private Long goodsTypeId;
    private Long sourceId;

    private String shopName;

    private String brand;
    // 单价（单位：元）
    private BigDecimal price;
    //数量
    private Integer amount;
    //运费单价（单位：元）
    private BigDecimal shipment;
    // 总价（单位：元）
    private BigDecimal totalPrice;
    //支付方式
    private Payment payment;
    // 是否二手
    private Boolean secondhand;
    // 是否加入统计（比如二手的卖给别人的可以不用统计）
    private Boolean stat;
    private GoodsConsumeType consumeType;
    private String sku;

    //匹配度
    private float match=0;

    /**
     * 匹配类型
     */
    private GoodsMatchType matchType;

    //参与比较的消费记录或商品类型ID
    private Long compareId;

    //参与比较的消费名称
    private String compareName;

    private String tags;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getGoodsTypeId() {
        return goodsTypeId;
    }

    public void setGoodsTypeId(Long goodsTypeId) {
        this.goodsTypeId = goodsTypeId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getShipment() {
        return shipment;
    }

    public void setShipment(BigDecimal shipment) {
        this.shipment = shipment;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Boolean getSecondhand() {
        return secondhand;
    }

    public void setSecondhand(Boolean secondhand) {
        this.secondhand = secondhand;
    }

    public Boolean getStat() {
        return stat;
    }

    public void setStat(Boolean stat) {
        this.stat = stat;
    }

    public GoodsConsumeType getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(GoodsConsumeType consumeType) {
        this.consumeType = consumeType;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public float getMatch() {
        return match;
    }

    public void setMatch(float match) {
        this.match = match;
    }

    public GoodsMatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(GoodsMatchType matchType) {
        this.matchType = matchType;
    }

    public Long getCompareId() {
        return compareId;
    }

    public void setCompareId(Long compareId) {
        this.compareId = compareId;
    }

    public String getCompareName() {
        return compareName;
    }

    public void setCompareName(String compareName) {
        this.compareName = compareName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
