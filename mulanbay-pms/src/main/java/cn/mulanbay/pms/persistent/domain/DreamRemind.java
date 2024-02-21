package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.schedule.enums.TriggerType;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 梦想提醒配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "dream_remind")
public class DreamRemind implements java.io.Serializable {

    private static final long serialVersionUID = 3974478414998752381L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "remind_id", unique = true, nullable = false)
    private Long remindId;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dream_id", nullable = false)
    private Dream dream;

    //从完成的百分比开始开始提醒
    @Column(name = "from_rate")
    private Integer fromRate;

    //距离期望日期的天数
    @Column(name = "from_expect_days")
    private Integer fromExpectDays;

    //完成时是否要提醒
    @Column(name = "finish_remind")
    private Boolean finishRemind;

    //最后一次提醒时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "last_remind_time")
    private Date lastRemindTime;

    @Column(name = "trigger_type")
    private TriggerType triggerType;

    @Column(name = "trigger_interval")
    private Integer triggerInterval;

    //提醒时间
    @Column(name = "remind_time")
    private String remindTime;

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

    public Long getRemindId() {
        return remindId;
    }

    public void setRemindId(Long remindId) {
        this.remindId = remindId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Dream getDream() {
        return dream;
    }

    public void setDream(Dream dream) {
        this.dream = dream;
    }

    public Integer getFromRate() {
        return fromRate;
    }

    public void setFromRate(Integer fromRate) {
        this.fromRate = fromRate;
    }

    public Integer getFromExpectDays() {
        return fromExpectDays;
    }

    public void setFromExpectDays(Integer fromExpectDays) {
        this.fromExpectDays = fromExpectDays;
    }

    public Boolean getFinishRemind() {
        return finishRemind;
    }

    public void setFinishRemind(Boolean finishRemind) {
        this.finishRemind = finishRemind;
    }

    public Date getLastRemindTime() {
        return lastRemindTime;
    }

    public void setLastRemindTime(Date lastRemindTime) {
        this.lastRemindTime = lastRemindTime;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public Integer getTriggerInterval() {
        return triggerInterval;
    }

    public void setTriggerInterval(Integer triggerInterval) {
        this.triggerInterval = triggerInterval;
    }

    public String getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
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
        if (other instanceof DreamRemind bean) {
            return bean.getRemindId().equals(this.getRemindId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(remindId);
    }
}
