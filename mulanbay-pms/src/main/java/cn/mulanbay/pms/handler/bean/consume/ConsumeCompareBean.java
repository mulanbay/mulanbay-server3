package cn.mulanbay.pms.handler.bean.consume;

import cn.mulanbay.pms.persistent.enums.GoodsConsumeType;
import cn.mulanbay.pms.persistent.enums.GoodsMatchType;
import cn.mulanbay.pms.persistent.enums.Payment;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 消费记录比较
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class ConsumeCompareBean implements Serializable {

    private static final long serialVersionUID = 1L;

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

    /**
     * 商品或商品类型
     */
    private GoodsMatchType matchType;

    //参与比较的消费记录或商品类型ID
    private Long compareId;

    //参与比较的消费名称
    private String compareName;

    /**
     * 原始的名称
     * 消费：商品名称
     * 商品类型：类型名称
     */
    private String originName;

    /**
     * 关键字标签
     *
     */
    private String tags;

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

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
