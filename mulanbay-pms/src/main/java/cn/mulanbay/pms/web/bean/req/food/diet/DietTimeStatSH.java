package cn.mulanbay.pms.web.bean.req.food.diet;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DietTimeStatSH extends QueryBuilder implements BindUser, DateStatSH {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occur_time", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occur_time", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "user_id", op = Parameter.Operator.EQ)
    public Long userId;

    @Query(fieldName = "diet_type", op = Parameter.Operator.EQ)
    private Integer dietType;

    private DateGroupType groupType;

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
        return groupType;
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

    public Integer getDietType() {
        return dietType;
    }

    public void setDietType(Integer dietType) {
        this.dietType = dietType;
    }

    public DateGroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(DateGroupType groupType) {
        this.groupType = groupType;
    }
}
