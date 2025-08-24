package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.ConsumeInvalidType;
import cn.mulanbay.pms.persistent.enums.GoodsConsumeType;
import cn.mulanbay.pms.persistent.enums.Payment;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 消费记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "consume")
public class Consume implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4411942746206104324L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "consume_id", unique = true, nullable = false)
    private Long consumeId;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "source_id", nullable = true)
    private ConsumeSource source;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "goods_type_id", nullable = true)
    private GoodsType goodsType;
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "goods_name", nullable = false, length = 200)
    private String goodsName;

    @Column(name = "sku")
    private String sku;

    /**
     * 店铺名称
     */
    @Column(name = "shop_name")
    private String shopName;

    /**
     * 单价（单位：元）
     */
    @Column(name = "price",precision = 9,scale = 2)
    private BigDecimal price;

    /**
     * 数量
     */
    @Column(name = "amount")
    private Integer amount;

    /**
     * 运费单价（单位：元）
     */
    @Column(name = "shipment",precision = 9,scale = 2)
    private BigDecimal shipment;

    /**
     * 总价（单位：元）
     */
    @Column(name = "total_price",precision = 9,scale = 2)
    private BigDecimal totalPrice;

    /**
     * 支付方式
     */
    @Column(name = "payment")
    private Payment payment;

    /**
     * 购买时间
     */
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "buy_time")
    private Date buyTime;

    /**
     * 消费日期(比如音乐会门票购买日期和实际消费日期不一样)
     */
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "consume_time")
    private Date consumeTime;

    /**
     * 售出价格（单位：元）
     */
    @Column(name = "sold_price",precision = 9,scale = 2)
    private BigDecimal soldPrice;

    /**
     * 商品过期类型
     */
    @Column(name = "invalid_type")
    private ConsumeInvalidType invalidType;

    /**
     * 过期时间/卖出时间
     */
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "invalid_time")
    private Date invalidTime;

    // 预期作废日期
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "expert_invalid_time")
    private Date expectInvalidTime;

    /**
     * 使用时长(毫秒)
     */
    @Column(name = "duration")
    private Long duration;

    /**
     * 关键字，统计使用
     */
    @Column(name = "tags")
    private String tags;

    /**
     * 品牌
     */
    @Column(name = "brand")
    private String brand;

    /**
     * 是否二手
     */
    @Column(name = "secondhand")
    private Boolean secondhand;

    /**
     * 是否加入统计（比如二手的卖给别人的可以不用统计）
     */
    @Column(name = "stat")
    private Boolean stat;

    @Column(name = "consume_type")
    private GoodsConsumeType consumeType;

    /**
     * 父级ID
     */
    @Column(name = "pid")
    private Long pid;

    @Column(name = "remark")
    private String remark;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time",updatable = false)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time",insertable = false)
    private Date modifyTime;

    // Constructors

    // Property accessors


    public Long getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(Long consumeId) {
        this.consumeId = consumeId;
    }

    public ConsumeSource getSource() {
        return source;
    }

    public void setSource(ConsumeSource source) {
        this.source = source;
    }

    public GoodsType getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(GoodsType goodsType) {
        this.goodsType = goodsType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public Date getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }

    public Date getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(Date consumeTime) {
        this.consumeTime = consumeTime;
    }

    public BigDecimal getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(BigDecimal soldPrice) {
        this.soldPrice = soldPrice;
    }

    public ConsumeInvalidType getInvalidType() {
        return invalidType;
    }

    public void setInvalidType(ConsumeInvalidType invalidType) {
        this.invalidType = invalidType;
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
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

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Transient
    public String getConsumeTypeName() {
        return consumeType.getName();
    }

    @Transient
    public String getPaymentName() {
        return payment == null ? null : payment.getName();
    }

    @Transient
    public String getInvalidTypeName() {
        return invalidType == null ? null : invalidType.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Consume bean) {
            return bean.getConsumeId().equals(this.getConsumeId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(consumeId);
    }
}
