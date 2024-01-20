package cn.mulanbay.pms.web.bean.req.consume.consume;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.GoodsConsumeType;
import cn.mulanbay.pms.persistent.enums.Payment;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public class ConsumeForm implements BindUser {

    private Long consumeId;

    @NotNull(message = "购买来源不能为空")
    private Long sourceId;

    @Min(value = 1,message = "商品类型不能选择根节点")
    @NotNull(message = "商品类型不能为空")
    private Long goodsTypeId;

    private Long userId;

    @NotEmpty(message = "商品名称不能为空")
    private String goodsName;

    //店铺名称
    private String shopName;

    // 单价（单位：元）
    @NotNull(message = "单价不能为空")
    private BigDecimal price;

    //数量
    @NotNull(message = "数量不能为空")
    private Integer amount;

    //运费单价（单位：元）
    @NotNull(message = "运费不能为空")
    private BigDecimal shipment;

    //支付方式
    @NotNull(message = "支付方式不能为空")
    private Payment payment;

    // 购买日期
    @NotNull(message = "购买日期不能为空")
    private Date buyTime;

    // 消费日期(比如音乐会门票购买日期和实际消费日期不一样)
    private Date consumeDate;

    // 售出价格（单位：元）
    private BigDecimal soldPrice;

    // 作废日期
    private Date invalidTime;

    private Date expectInvalidTime;

    //关键字，统计使用
    private String tags;

    private String brand;

    private String remark;

    // 是否二手
    @NotNull(message = "请选择是否二手")
    private Boolean secondhand;

    // 是否加入统计（比如二手的卖给别人的可以不用统计）
    @NotNull(message = "请选择是否统计")
    private Boolean stat = true;

    @NotNull(message = "消费类型不能为空")
    private GoodsConsumeType consumeType;

    private String sku;

    public Long getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(Long consumeId) {
        this.consumeId = consumeId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getGoodsTypeId() {
        return goodsTypeId;
    }

    public void setGoodsTypeId(Long goodsTypeId) {
        this.goodsTypeId = goodsTypeId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
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

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Date getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }

    public Date getConsumeDate() {
        return consumeDate;
    }

    public void setConsumeDate(Date consumeDate) {
        this.consumeDate = consumeDate;
    }

    public BigDecimal getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(BigDecimal soldPrice) {
        this.soldPrice = soldPrice;
    }

    public Date getInvalidTime() {
        return invalidTime;
    }

    public void setInvalidTime(Date invalidTime) {
        this.invalidTime = invalidTime;
    }

    public Date getExpectInvalidTime() {
        return expectInvalidTime;
    }

    public void setExpectInvalidTime(Date expectInvalidTime) {
        this.expectInvalidTime = expectInvalidTime;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
}
