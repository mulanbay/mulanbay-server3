package cn.mulanbay.pms.web.bean.req.work.overtime;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class WorkOvertimeForm implements BindUser {

    private Long overtimeId;

    private Long userId;

    @NotNull(message = "公司不能为空")
    private Long companyId;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    //@NotNull(message = "{validate.workOvertime.workDate.NotNull}")
    private Date workDate;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @NotNull(message = "开始时间不能为空")
    private Date startTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @NotNull(message = "结束时间不能为空")
    private Date endTime;

    //@NotNull(message = "{validate.workOvertime.hours.NotNull}")
    private Double hours;

    private String remark;

    public Long getOvertimeId() {
        return overtimeId;
    }

    public void setOvertimeId(Long overtimeId) {
        this.overtimeId = overtimeId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
