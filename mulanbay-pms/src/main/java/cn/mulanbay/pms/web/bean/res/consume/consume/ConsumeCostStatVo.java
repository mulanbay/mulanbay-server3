package cn.mulanbay.pms.web.bean.res.consume.consume;

import java.math.BigDecimal;
import java.util.Date;

public class ConsumeCostStatVo {

    private Long consumeId;

    private String goodsName;

    /**
     * 商品的价格（不包含下一级商品）
     */
    private BigDecimal totalPrice;

    /**
     * 购买日期
     */
    private Date buyTime;

    /**
     * 消费日期(比如音乐会门票购买日期和实际消费日期不一样)
     */
    private Date consumeTime;

    /**
     * 售出价格（单位：元）
     */
    private BigDecimal soldPrice;

    /**
     * 作废日期
     */
    private Date invalidTime;

    /**
     * 预期作废日期
     */
    private Date expectInvalidTime;

    /**
     * 下级商品总成本(价格)
     */
    private BigDecimal childrenPrice;

    /**
     * 下级商品总售出价格
     */
    private BigDecimal childrenSoldPrice;

    /**
     * 下级商品数
     */
    private Long childrens;

    /**
     * 总成本
     */
    private BigDecimal totalCost;

    /**
     * 使用时长(毫秒)
     */
    private Long usedMillSecs;

    /**
     * 离预期作废
     */
    private Long expMillSecs;

    /**
     * 每天花费
     */
    private BigDecimal costPerDay;

    /**
     * 每天花费（包含下级商品）
     */
    private BigDecimal totalCostPerDay;

    /**
     * 折旧率
     */
    private BigDecimal depRate;

    /**
     * 折旧率（包含下级商品）
     */
    private BigDecimal totalDepRate;

    public Long getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(Long consumeId) {
        this.consumeId = consumeId;
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

    public BigDecimal getChildrenPrice() {
        return childrenPrice;
    }

    public void setChildrenPrice(BigDecimal childrenPrice) {
        this.childrenPrice = childrenPrice;
    }

    public BigDecimal getChildrenSoldPrice() {
        return childrenSoldPrice;
    }

    public void setChildrenSoldPrice(BigDecimal childrenSoldPrice) {
        this.childrenSoldPrice = childrenSoldPrice;
    }

    public Long getChildrens() {
        return childrens;
    }

    public void setChildrens(Long childrens) {
        this.childrens = childrens;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public Long getUsedMillSecs() {
        return usedMillSecs;
    }

    public void setUsedMillSecs(Long usedMillSecs) {
        this.usedMillSecs = usedMillSecs;
    }

    public Long getExpMillSecs() {
        return expMillSecs;
    }

    public void setExpMillSecs(Long expMillSecs) {
        this.expMillSecs = expMillSecs;
    }

    public BigDecimal getCostPerDay() {
        return costPerDay;
    }

    public void setCostPerDay(BigDecimal costPerDay) {
        this.costPerDay = costPerDay;
    }

    public BigDecimal getTotalCostPerDay() {
        return totalCostPerDay;
    }

    public void setTotalCostPerDay(BigDecimal totalCostPerDay) {
        this.totalCostPerDay = totalCostPerDay;
    }

    public BigDecimal getDepRate() {
        return depRate;
    }

    public void setDepRate(BigDecimal depRate) {
        this.depRate = depRate;
    }

    public BigDecimal getTotalDepRate() {
        return totalDepRate;
    }

    public void setTotalDepRate(BigDecimal totalDepRate) {
        this.totalDepRate = totalDepRate;
    }
}
