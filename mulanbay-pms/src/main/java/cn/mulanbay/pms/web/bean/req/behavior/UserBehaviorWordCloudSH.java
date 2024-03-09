package cn.mulanbay.pms.web.bean.req.behavior;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.web.bean.request.PageSearch;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class UserBehaviorWordCloudSH extends UserBehaviorCalendarSH {

    /**
     * 是否忽略短语
     */
    private Boolean ignoreShort;

    public Boolean getIgnoreShort() {
        return ignoreShort;
    }

    public void setIgnoreShort(Boolean ignoreShort) {
        this.ignoreShort = ignoreShort;
    }
}
