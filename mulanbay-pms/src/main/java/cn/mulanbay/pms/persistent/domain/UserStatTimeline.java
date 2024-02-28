package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 用户统计日志
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "user_stat_timeline")
public class UserStatTimeline implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "timeline_id", unique = true, nullable = false)
    private Long timelineId;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stat_id", nullable = false)
    private UserStat stat;

    /**
     * 统计值
     */
    @Column(name = "value")
    private Long value;

    /**
     * 有名称的统计值
     */
    @Column(name = "name_value")
    private String nameValue;

    /**
     * 期望值
     */
    @Column(name = "expect_value")
    private Long expectValue;

    @Column(name = "unit")
    private String unit;
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

    public Long getTimelineId() {
        return timelineId;
    }

    public void setTimelineId(Long timelineId) {
        this.timelineId = timelineId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserStat getStat() {
        return stat;
    }

    public void setStat(UserStat stat) {
        this.stat = stat;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getNameValue() {
        return nameValue;
    }

    public void setNameValue(String nameValue) {
        this.nameValue = nameValue;
    }

    public Long getExpectValue() {
        return expectValue;
    }

    public void setExpectValue(Long expectValue) {
        this.expectValue = expectValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
        if (other instanceof UserStatTimeline bean) {
            return bean.getTimelineId().equals(this.getTimelineId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(timelineId);
    }
}
