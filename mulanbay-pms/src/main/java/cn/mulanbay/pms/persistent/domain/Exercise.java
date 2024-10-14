package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BestType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 锻炼记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "exercise")
public class Exercise implements java.io.Serializable {
    private static final long serialVersionUID = 6821542686263268133L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "exercise_id", unique = true, nullable = false)
    private Long exerciseId;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "exercise_time")
    private Date exerciseTime;
    /**
     * 锻炼值
     */
    @Column(name = "value")
    private Double value;

    /**
     * 锻炼时间(分钟)
     */
    @Column(name = "duration")
    private Integer duration;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "max_speed")
    private Double maxSpeed;

    @Column(name = "pace")
    private Double pace;

    @Column(name = "max_pace")
    private Double maxPace;

    @Column(name = "max_heart_rate")
    private Integer maxHeartRate;

    @Column(name = "avg_heart_rate")
    private Integer avgHeartRate;

    /**
     * 锻炼值是否最佳
     */
    @Column(name = "value_best")
    private BestType valueBest;
    //速度最佳状态
    @Column(name = "fast_best")
    private BestType fastBest;

    /**
     * 经历 ID
     */
    @Column(name = "exp_id")
    private Long expId;

    @Column(name = "remark")
    private String remark;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time",updatable = false)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time",insertable = false)
    private Date modifyTime;

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
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

    public BestType getValueBest() {
        return valueBest;
    }

    public void setValueBest(BestType valueBest) {
        this.valueBest = valueBest;
    }

    public BestType getFastBest() {
        return fastBest;
    }

    public void setFastBest(BestType fastBest) {
        this.fastBest = fastBest;
    }

    public Long getExpId() {
        return expId;
    }

    public void setExpId(Long expId) {
        this.expId = expId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Exercise bean) {
            return bean.getExerciseId().equals(this.getExerciseId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(exerciseId);
    }
}
