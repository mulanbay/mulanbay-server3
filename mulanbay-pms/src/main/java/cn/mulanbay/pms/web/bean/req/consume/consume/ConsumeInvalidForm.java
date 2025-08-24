package cn.mulanbay.pms.web.bean.req.consume.consume;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.ConsumeInvalidType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public class ConsumeInvalidForm implements BindUser {

    @NotNull(message = "消费ID不能为空")
    private Long consumeId;

    private Long userId;

    // 售出价格（单位：元）
    private BigDecimal soldPrice;

    @NotNull(message = "过期类型不能为空")
    private ConsumeInvalidType invalidType;

    // 作废日期
    @NotNull(message = "作废日期不能为空")
    private Date invalidTime;

    public @NotNull(message = "消费ID不能为空") Long getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(@NotNull(message = "消费ID不能为空") Long consumeId) {
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

    public BigDecimal getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(BigDecimal soldPrice) {
        this.soldPrice = soldPrice;
    }

    public @NotNull(message = "过期类型不能为空") ConsumeInvalidType getInvalidType() {
        return invalidType;
    }

    public void setInvalidType(@NotNull(message = "过期类型不能为空") ConsumeInvalidType invalidType) {
        this.invalidType = invalidType;
    }

    public @NotNull(message = "作废日期不能为空") Date getInvalidTime() {
        return invalidTime;
    }

    public void setInvalidTime(@NotNull(message = "作废日期不能为空") Date invalidTime) {
        this.invalidTime = invalidTime;
    }
}
