package cn.mulanbay.pms.web.bean.req.config.levelConfig;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotNull;

public class JudgeLevelForm implements BindUser {

    private Long userId;

    @NotNull(message = "请选择是否要更新级别")
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
