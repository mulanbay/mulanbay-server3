package cn.mulanbay.pms.web.bean.req.health.treat;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.CrossType;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.TreatStage;
import cn.mulanbay.pms.persistent.enums.TreatType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class TreatSH extends PageSearch implements DateStatSH, BindUser {

    // 支持多个字段查询
    @Query(fieldName = "hospital,department,organ,disease,confirmDisease,tags", op = Parameter.Operator.LIKE, crossType = CrossType.OR)
    private String name;

    @Query(fieldName = "tags", op = Parameter.Operator.EQ)
    private String tags;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "treatTime", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "treatTime", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    @Query(fieldName = "sick", op = Parameter.Operator.EQ)
    private Boolean sick;

    @Query(fieldName = "treatType", op = Parameter.Operator.EQ)
    private TreatType treatType;

    @Query(fieldName = "stage", op = Parameter.Operator.EQ)
    private TreatStage stage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public Boolean getSick() {
        return sick;
    }

    public void setSick(Boolean sick) {
        this.sick = sick;
    }

    public TreatType getTreatType() {
        return treatType;
    }

    public void setTreatType(TreatType treatType) {
        this.treatType = treatType;
    }

    public TreatStage getStage() {
        return stage;
    }

    public void setStage(TreatStage stage) {
        this.stage = stage;
    }
}
