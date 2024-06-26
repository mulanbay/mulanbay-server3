package cn.mulanbay.pms.web.bean.req.data.calendar;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class UserCalendarFinishForm implements BindUser {

    @NotNull(message = "日历编号不能为空")
    private Long calendarId;

    private Long userId;

    @NotNull(message = "完成时间不能为空")
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date finishTime;

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

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }
}
