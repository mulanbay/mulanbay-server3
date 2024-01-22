package cn.mulanbay.pms.web.bean.req.consume.consume;

import cn.mulanbay.common.aop.BindUser;

import jakarta.validation.constraints.NotNull;

public class ConsumeCostStatForm implements BindUser {

    @NotNull(message = "消费编号不能为空")
    private Long consumeId;

    @NotNull(message = "是否查询子集不能为空")
    private Boolean deepCost;

    private Long userId;

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

    public Boolean getDeepCost() {
        return deepCost;
    }

    public void setDeepCost(Boolean deepCost) {
        this.deepCost = deepCost;
    }
}
