package cn.mulanbay.pms.web.bean.req.sport.exercise;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ExerciseForm implements BindUser {

    private Long exerciseId;
    private Long userId;

    @NotNull(message = "运动类型不能为空")
    private Long sportId;

    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @NotNull(message = "锻炼时间不能为空")
    private Date exerciseTime;

    @NotNull(message = "锻炼值不能为空")
    private Double value;

    @NotNull(message = "锻炼时长不能为空")
    private Integer duration;
    private Double speed;
    private Double maxSpeed;
    private Double pace;
    private Double maxPace;
    private Integer maxHeartRate;
    private Integer avgHeartRate;
    private String remark;

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
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

    public Date getExerciseTime() {
        return exerciseTime;
    }

    public void setExerciseTime(Date exerciseTime) {
        this.exerciseTime = exerciseTime;
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

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Double getPace() {
        return pace;
    }

    public void setPace(Double pace) {
        this.pace = pace;
    }

    public Double getMaxPace() {
        return maxPace;
    }

    public void setMaxPace(Double maxPace) {
        this.maxPace = maxPace;
    }

    public Integer getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(Integer maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }

    public Integer getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(Integer avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
