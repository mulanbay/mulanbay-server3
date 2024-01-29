package cn.mulanbay.pms.web.bean.req.main;

import cn.mulanbay.common.aop.BindUser;

public class UserGeneralStatSH implements BindUser {

    private Long userId;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
