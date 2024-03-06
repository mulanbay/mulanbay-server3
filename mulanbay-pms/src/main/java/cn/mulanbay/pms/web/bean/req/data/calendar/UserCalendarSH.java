package cn.mulanbay.pms.web.bean.req.data.calendar;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.enums.UserCalendarFinishType;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class UserCalendarSH extends PageSearch implements BindUser, FullEndDateTime {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "bussDay", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "bussDay", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "title", op = Parameter.Operator.LIKE)
    private String name;

    @Query(fieldName = "bussIdentityKey", op = Parameter.Operator.EQ)
    private String bussIdentityKey;

    @Query(fieldName = "sourceType", op = Parameter.Operator.EQ)
    private BussType sourceType;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    public Long userId;

    @Query(fieldName = "finishType", op = Parameter.Operator.EQ)
    private UserCalendarFinishType finishType;

    @Query(fieldName = "period", op = Parameter.Operator.EQ)
    private PeriodType period;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBussIdentityKey() {
        return bussIdentityKey;
    }

    public void setBussIdentityKey(String bussIdentityKey) {
        this.bussIdentityKey = bussIdentityKey;
    }

    public BussType getSourceType() {
        return sourceType;
    }

    public void setSourceType(BussType sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserCalendarFinishType getFinishType() {
        return finishType;
    }

    public void setFinishType(UserCalendarFinishType finishType) {
        this.finishType = finishType;
    }

    public PeriodType getPeriod() {
        return period;
    }

    public void setPeriod(PeriodType period) {
        this.period = period;
    }
}
