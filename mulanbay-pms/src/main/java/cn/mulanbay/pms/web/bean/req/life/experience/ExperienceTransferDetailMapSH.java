package cn.mulanbay.pms.web.bean.req.life.experience;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.persistent.enums.MapField;
import jakarta.validation.constraints.NotNull;

public class ExperienceTransferDetailMapSH extends QueryBuilder implements BindUser {

    @NotNull(message = "经历编号不能为空")
    private Long expId;

    private Long userId;

    private MapField field;

    public Long getExpId() {
        return expId;
    }

    public void setExpId(Long expId) {
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

    public MapField getField() {
        return field;
    }

    public void setField(MapField field) {
        this.field = field;
    }
}
