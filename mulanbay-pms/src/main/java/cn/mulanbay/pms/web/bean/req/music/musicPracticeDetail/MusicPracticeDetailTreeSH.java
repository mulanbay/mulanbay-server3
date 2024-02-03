package cn.mulanbay.pms.web.bean.req.music.musicPracticeDetail;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.TuneType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class MusicPracticeDetailTreeSH extends QueryBuilder implements DateStatSH, BindUser {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "practice.practiceDate", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "practice.practiceDate", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "practice.userId", op = Parameter.Operator.EQ)
    public Long userId;

    @Query(fieldName = "practice.instrument.instrumentId", op = Parameter.Operator.EQ)
    private Long instrumentId;

    @Query(fieldName = "tuneType", op = Parameter.Operator.EQ)
    private TuneType tuneType;

    private Boolean needRoot;

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public DateGroupType getDateGroupType() {
        return null;
    }

    @Override
    public Boolean isCompleteDate() {
        return null;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

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

    public TuneType getTuneType() {
        return tuneType;
    }

    public void setTuneType(TuneType tuneType) {
        this.tuneType = tuneType;
    }

    public Boolean getNeedRoot() {
        return needRoot;
    }

    public void setNeedRoot(Boolean needRoot) {
        this.needRoot = needRoot;
    }
}
