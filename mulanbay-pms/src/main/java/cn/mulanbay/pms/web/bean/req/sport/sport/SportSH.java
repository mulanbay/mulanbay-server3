package cn.mulanbay.pms.web.bean.req.sport.sport;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.web.bean.request.PageSearch;

public class SportSH extends PageSearch implements BindUser {

    @Query(fieldName = "sportName", op = Parameter.Operator.LIKE)
    private String name;

    @Query(fieldName = "status", op = Parameter.Operator.EQ)
    private CommonStatus status;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    private Boolean needRoot;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getNeedRoot() {
        return needRoot;
    }

    public void setNeedRoot(Boolean needRoot) {
        this.needRoot = needRoot;
    }
}
