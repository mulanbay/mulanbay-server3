package cn.mulanbay.pms.web.bean.req.data.reward;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class UserRewardDateStatSH extends QueryBuilder implements DateStatSH, BindUser, FullEndDateTime {


    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "created_time", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "created_time", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "user_id", op = Parameter.Operator.EQ)
    private Long userId;

    @Query(fieldName = "source", op = Parameter.Operator.EQ)
    private Short source;

    @Query(fieldName = "source_id", op = Parameter.Operator.EQ)
    private Long sourceId;

    private DateGroupType dateGroupType;

    // 是否补全日期
    private Boolean completeDate;

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

    @Override
    public DateGroupType getDateGroupType() {
        return dateGroupType;
    }

    @Override
    public Boolean isCompleteDate() {
        return completeDate;
    }

    public void setDateGroupType(DateGroupType dateGroupType) {
        this.dateGroupType = dateGroupType;
    }

    public Short getSource() {
        return source;
    }

    public void setSource(Short source) {
        this.source = source;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Boolean getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Boolean completeDate) {
        this.completeDate = completeDate;
    }

}
