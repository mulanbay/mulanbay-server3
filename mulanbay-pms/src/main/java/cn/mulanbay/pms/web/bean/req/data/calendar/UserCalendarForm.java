package cn.mulanbay.pms.web.bean.req.data.calendar;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.enums.UserCalendarFinishType;
import cn.mulanbay.pms.persistent.enums.UserCalendarSource;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class UserCalendarForm implements BindUser {

    private Long calendarId;

    private Long userId;

    @NotEmpty(message = "标题不能为空")
    private String title;

    @NotEmpty(message = "内容不能为空")
    private String content;

    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date bussDay;

    //失效时间
    //@NotNull(message = "{validate.userCalendar.expireTime.NotNull}")
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date expireTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date finishTime;

    //是否为全天任务
    private Boolean allDay;

    //手动设置有用
    private String location;

    @NotNull(message = "周期类型不能为空")
    private PeriodType period;

    private String periodValues;

    //以模板新增的，可以查询是否完成的判断
    private Long templateId;

    //用户自己选择的值，以模板新增的，可以查询是否完成的判断
    private String bindValues;

    private String remark;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getBussDay() {
        return bussDay;
    }

    public void setBussDay(Date bussDay) {
        this.bussDay = bussDay;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }


    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public PeriodType getPeriod() {
        return period;
    }

    public void setPeriod(PeriodType period) {
        this.period = period;
    }

    public String getPeriodValues() {
        return periodValues;
    }

    public void setPeriodValues(String periodValues) {
        this.periodValues = periodValues;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getBindValues() {
        return bindValues;
    }

    public void setBindValues(String bindValues) {
        this.bindValues = bindValues;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
