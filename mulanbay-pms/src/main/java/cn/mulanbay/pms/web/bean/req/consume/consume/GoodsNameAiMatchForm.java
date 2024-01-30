package cn.mulanbay.pms.web.bean.req.consume.consume;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotEmpty;

public class GoodsNameAiMatchForm implements BindUser {

    private Long userId;

    @NotEmpty(message = "商品名称不能为空")
    private String goodsName;

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
}
