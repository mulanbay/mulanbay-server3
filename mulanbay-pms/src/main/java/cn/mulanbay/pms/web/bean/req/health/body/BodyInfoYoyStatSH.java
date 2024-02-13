package cn.mulanbay.pms.web.bean.req.health.body;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.web.bean.req.BaseYoyStatSH;

public class BodyInfoYoyStatSH extends BaseYoyStatSH implements BindUser {

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
