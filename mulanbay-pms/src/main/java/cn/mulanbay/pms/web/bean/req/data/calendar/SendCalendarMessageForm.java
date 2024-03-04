package cn.mulanbay.pms.web.bean.req.data.calendar;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class SendCalendarMessageForm implements BindUser {

    /**
     * POST请求需要加JsonFormat标签
     */
    private String date;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    private Date calendarDate;

    public Long userId;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(Date calendarDate) {
        this.calendarDate = calendarDate;
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
