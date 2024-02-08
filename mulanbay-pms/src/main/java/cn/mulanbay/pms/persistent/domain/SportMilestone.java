package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 锻炼里程碑
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "sport_milestone")
public class SportMilestone implements Serializable {

    private static final long serialVersionUID = 8614289867949669013L;
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "milestone_id", unique = true, nullable = false)
    private Long milestoneId;

    @Column(name = "milestone_name")
    private String milestoneName;

    @Column(name = "alais")
    private String alais;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "sport_id")
    private Sport sport;

    /**
     * 锻炼值
     */
    @Column(name = "value")
    private Double value;

    /**
     * 锻炼时间(分钟)
     */
    private Integer duration;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    /**
     * 实现的开始时间
     */
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "start_time")
    private Date startTime;

    /**
     * 实现的结束时间
     */
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "cost_days")
    private Integer costDays;
    @Column(name = "order_index", nullable = false)
    private Short orderIndex;

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

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
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
        if (other instanceof SportMilestone bean) {
            return bean.getMilestoneId().equals(this.getMilestoneId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(milestoneId);
    }
}
