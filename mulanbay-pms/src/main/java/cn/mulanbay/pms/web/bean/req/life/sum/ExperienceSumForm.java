package cn.mulanbay.pms.web.bean.req.life.sum;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotNull;

public class ExperienceSumForm implements BindUser {

    private Long sumId;

    private Long userId;

    //年份
    @NotNull(message = "年份不能为空")
    private Integer year;
    //该年总天数
    @NotNull(message = "天数不能为空")
    private Integer totalDays;
    //总工作天数
    @NotNull(message = "工作天数不能为空")
    private Integer workDays;
    //旅行天数
    @NotNull(message = "旅行天数不能为空")
    private Integer travelDays;
    //学习天数
    @NotNull(message = "学习天数不能为空")
    private Integer studyDays;

    public Long getSumId() {
        return sumId;
    }

    public void setSumId(Long sumId) {
        this.sumId = sumId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public Integer getWorkDays() {
        return workDays;
    }

    public void setWorkDays(Integer workDays) {
        this.workDays = workDays;
    }

    public Integer getTravelDays() {
        return travelDays;
    }

    public void setTravelDays(Integer travelDays) {
        this.travelDays = travelDays;
    }

    public Integer getStudyDays() {
        return studyDays;
    }

    public void setStudyDays(Integer studyDays) {
        this.studyDays = studyDays;
    }
}
