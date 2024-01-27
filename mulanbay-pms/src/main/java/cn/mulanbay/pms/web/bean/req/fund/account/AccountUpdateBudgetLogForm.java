package cn.mulanbay.pms.web.bean.req.fund.account;

import cn.mulanbay.common.aop.BindUser;

/**
 * @Author: fenghong
 * @Create : 2020/12/13 17:45
 */
public class AccountUpdateBudgetLogForm implements BindUser {

    private Long userId;

    private String bussKey;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBussKey() {
        return bussKey;
    }

    public void setBussKey(String bussKey) {
        this.bussKey = bussKey;
    }
}
