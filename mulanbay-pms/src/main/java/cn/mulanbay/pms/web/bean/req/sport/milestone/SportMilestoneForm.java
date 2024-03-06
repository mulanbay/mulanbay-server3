package cn.mulanbay.pms.web.bean.req.sport.milestone;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class SportMilestoneForm implements BindUser {

    private Long milestoneId;

    @NotEmpty(message = "名称不能为空")
    private String milestoneName;

    private String alais;

    private Long userId;

    @NotNull(message = "运动类型不能为空")
    private Long sportId;

    @NotNull(message = "锻炼值不能为空")
    private Double value;

    @NotNull(message = "时长不能为空")
    private Integer duration;

    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date startTime;

    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date endTime;
    private Integer costDays;

    //@NotNull(message = "{validate.sportMilestone.orderIndex.NotNull}")
    private Short orderIndex;
    private String remark;

    public Long getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(Long milestoneId) {
        this.milestoneId = milestoneId;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public void setMilestoneName(String milestoneName) {
        this.milestoneName = milestoneName;
    }

    public String getAlais() {
        return alais;
    }

    public void setAlais(String alais) {
        this.alais = alais;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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

    public Integer getCostDays() {
        return costDays;
    }

    public void setCostDays(Integer costDays) {
        this.costDays = costDays;
    }

    public Short getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Short orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
