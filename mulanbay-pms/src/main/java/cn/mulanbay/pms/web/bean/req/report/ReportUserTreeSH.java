package cn.mulanbay.pms.web.bean.req.report;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.BindUserLevel;

public class ReportUserTreeSH extends ReportTreeSH implements BindUser, BindUserLevel {

    private Long userId;

    private Integer level;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getLevel() {
        return level;
    }

    @Override
    public void setLevel(Integer level) {
        this.level = level;
    }
}
