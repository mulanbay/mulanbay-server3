package cn.mulanbay.pms.web.bean.req.data.calendar;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.web.bean.request.PageSearch;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class UserCalendarFlowLogSH extends PageSearch implements BindUser, FullEndDateTime {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "开始日期不能为空")
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "结束日期不能为空")
    private Date endDate;

    @NotNull(message = "日历编号不能为空")
    private Long calendarId;

    private Long userId;

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

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
