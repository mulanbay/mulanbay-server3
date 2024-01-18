package cn.mulanbay.pms.web.bean.req.auth.fastMenu;

import cn.mulanbay.common.aop.BindUser;

public class FastMenuForm implements BindUser {

    private Long userId;

    private String menuIds;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(String menuIds) {
        this.menuIds = menuIds;
    }
}
