package cn.mulanbay.pms.web.bean.req.config.scoreGroup;

import cn.mulanbay.common.aop.BindUser;

import jakarta.validation.constraints.NotNull;

public class SelfJudgeLevelForm implements BindUser {

    private Long userId;

    @NotNull(message = "是否更新级别不能为空")
    private Boolean updateLevel;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getUpdateLevel() {
        return updateLevel;
    }

    public void setUpdateLevel(Boolean updateLevel) {
        this.updateLevel = updateLevel;
    }
}
