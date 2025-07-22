package cn.mulanbay.pms.web.bean.req.life.experience;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.ExperienceType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class ExperienceRefreshCostForm implements BindUser {

    @NotNull(message = "经历编号不能为空")
    private Long expId;

    private Long userId;

    public @NotNull(message = "经历编号不能为空") Long getExpId() {
        return expId;
    }

    public void setExpId(@NotNull(message = "经历编号不能为空") Long expId) {
        this.expId = expId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
