package cn.mulanbay.pms.web.bean.req.dream;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DreamDelaySH extends PageSearch implements BindUser {

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    @Query(fieldName = "dream.dreamId", op = Parameter.Operator.EQ)
    private Long dreamId;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDreamId() {
        return dreamId;
    }

    public void setDreamId(Long dreamId) {
        this.dreamId = dreamId;
    }
}
