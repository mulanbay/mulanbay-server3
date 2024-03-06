package cn.mulanbay.pms.web.bean.req.report.bind;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.StatBussType;
import jakarta.validation.constraints.NotNull;

public class StatBindConfigsSH implements BindUser {

    @NotNull(message = "外键不能为空")
    private Long fid;

    @NotNull(message = "业务类型不能为空")
    private StatBussType type;

    private Long userId;

    public Long getFid() {
        return fid;
    }

    public void setFid(Long fid) {
        this.fid = fid;
    }

    public StatBussType getType() {
        return type;
    }

    public void setType(StatBussType type) {
        this.type = type;
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
