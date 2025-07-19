package cn.mulanbay.pms.web.bean.req.life.consume;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public class ExperienceConsumeForm implements BindUser {

    private Long consumeId;
    private Long userId;

    @NotEmpty(message = "名称不能为空")
    private String consumeName;

    // 购买日期
    @NotNull(message = "购买日期不能为空")
    private Date buyTime;

    @NotNull(message = "明细编号不能为空")
    private Long detailId;

    @NotNull(message = "商品类型不能为空")
    private Long goodsTypeId;

    @NotNull(message = "花费不能为空")
    private BigDecimal cost;

    //有些花费可以直接与消费记录挂钩
    private Long scId;

    // 是否加入统计
    @NotNull(message = "是否统计不能为空")
    private Boolean stat;

    private String remark;

    public Long getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(Long consumeId) {
        this.consumeId = consumeId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getConsumeName() {
        return consumeName;
    }

    public void setConsumeName(String consumeName) {
        this.consumeName = consumeName;
    }

    public Date getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getGoodsTypeId() {
        return goodsTypeId;
    }

    public void setGoodsTypeId(Long goodsTypeId) {
        this.goodsTypeId = goodsTypeId;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Long getScId() {
        return scId;
    }

    public void setScId(Long scId) {
        this.scId = scId;
    }

    public Boolean getStat() {
        return stat;
    }

    public void setStat(Boolean stat) {
        this.stat = stat;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
