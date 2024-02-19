package cn.mulanbay.pms.web.bean.req.life.consume;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.web.bean.request.PageSearch;

public class ExperienceConsumeSH extends PageSearch implements BindUser {

    @Query(fieldName = "detail.experience.expId", op = Parameter.Operator.EQ)
    private Long expId;

    @Query(fieldName = "detail.detailId", op = Parameter.Operator.EQ)
    private Long detailId;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    public Long userId;

    public Long getExpId() {
        return expId;
    }

    public void setExpId(Long expId) {
        this.expId = expId;
    }

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
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
