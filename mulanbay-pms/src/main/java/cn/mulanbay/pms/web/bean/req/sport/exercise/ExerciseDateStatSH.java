package cn.mulanbay.pms.web.bean.req.sport.exercise;

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

public class ExerciseDateStatSH extends QueryBuilder implements DateStatSH, BindUser, FullEndDateTime {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "exercise_time", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "exercise_time", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "user_id", op = Parameter.Operator.EQ)
    public Long userId;

    //@NotNull(message = "{validate.sportExercise.sportTypeId.NotNull}")
    @Query(fieldName = "sport_id", op = Parameter.Operator.EQ)
    private Long sportId;

    private DateGroupType dateGroupType;

    // 是否补全日期
    private Boolean completeDate;

    //获取最佳的记录（绘制图使用）
    private String bestField;

    // 是否统计更详细的信息
    private Boolean fullStat;

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

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Boolean getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Boolean completeDate) {
        this.completeDate = completeDate;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBestField() {
        return bestField;
    }

    public void setBestField(String bestField) {
        this.bestField = bestField;
    }

    public Boolean getFullStat() {
        return fullStat;
    }

    public void setFullStat(Boolean fullStat) {
        this.fullStat = fullStat;
    }
}
