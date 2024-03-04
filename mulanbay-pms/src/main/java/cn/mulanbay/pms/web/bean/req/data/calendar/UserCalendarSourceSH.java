package cn.mulanbay.pms.web.bean.req.data.calendar;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.UserCalendarSource;
import jakarta.validation.constraints.NotNull;

public class UserCalendarSourceSH implements BindUser {

    @NotNull(message = "来源不能为空")
    private UserCalendarSource sourceType;

    public Long userId;

    @NotNull(message = "来源ID不能为空")
    private Long sourceId;

    public UserCalendarSource getSourceType() {
        return sourceType;
    }

    public void setSourceType(UserCalendarSource sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }
}
