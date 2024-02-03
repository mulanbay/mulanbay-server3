package cn.mulanbay.pms.web.bean.req.music.musicPractice;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.web.bean.req.BaseYoyStatSH;

public class MusicPracticeYoyStatSH extends BaseYoyStatSH implements BindUser {

    private Long userId;

    @Query(fieldName = "instrumentId", op = Parameter.Operator.EQ)
    private Long instrumentId;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Long instrumentId) {
        this.instrumentId = instrumentId;
    }
}
