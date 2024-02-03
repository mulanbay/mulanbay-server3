package cn.mulanbay.pms.web.bean.req.music.musicPracticeDetail;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.TuneType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class MusicPracticeDetailSH extends PageSearch implements DateStatSH, BindUser, FullEndDateTime {

    @Query(fieldName = "detailId", op = Parameter.Operator.EQ)
    private Long detailId;

    @Query(fieldName = "practice.practiceId", op = Parameter.Operator.EQ)
    private Long practiceId;

    @Query(fieldName = "tune", op = Parameter.Operator.LIKE)
    private String tune;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "practice.practiceDate", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "practice.practiceDate", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    public Long userId;

    @Query(fieldName = "practice.instrument.instrumentId", op = Parameter.Operator.EQ)
    private Long instrumentId;

    @Query(fieldName = "tuneType", op = Parameter.Operator.EQ)
    private TuneType tuneType;

    private Boolean allMi;

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(Long practiceId) {
        this.practiceId = practiceId;
    }

    public String getTune() {
        return tune;
    }

    public void setTune(String tune) {
        this.tune = tune;
    }

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

    @Override
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

    public Boolean getAllMi() {
        return allMi;
    }

    public void setAllMi(Boolean allMi) {
        this.allMi = allMi;
    }
}
