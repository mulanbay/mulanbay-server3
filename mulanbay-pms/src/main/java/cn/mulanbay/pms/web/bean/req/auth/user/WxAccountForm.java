package cn.mulanbay.pms.web.bean.req.auth.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class WxAccountForm {

    private Long id;

    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @NotEmpty(message = "微信OpenId不能为空")
    private String openId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
