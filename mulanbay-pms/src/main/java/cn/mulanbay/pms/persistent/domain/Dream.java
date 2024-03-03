package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DreamStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 梦想
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "dream")
public class Dream implements java.io.Serializable {
    private static final long serialVersionUID = 2715806005973261424L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "dream_id", unique = true, nullable = false)
    private Long dreamId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "dream_name")
    private String dreamName;

    /**
     * 单位元
     */
    @Column(name = "cost",precision = 9,scale = 2)
    private BigDecimal cost;

    @Column(name = "difficulty")
    private Integer difficulty;

    @Column(name = "important")
    private Double important;

    @Column(name = "expect_days")
    private Integer expectDays;

    @Column(name = "status")
    private DreamStatus status;

    @Column(name = "rate")
    private Integer rate;

    /**
     * 期望实现日期
     */
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "expect_date")
    private Date expectDate;

    /**
     * 截止日期
     */
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "deadline")
    private Date deadline;

    /**
     * 完成日期
     */
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "finish_date")
    private Date finishDate;

    //延期次数
    @Column(name = "delays")
    private Integer delays;

    //关联的计划配置项，到时根据无时间条件来计算进度
    @Column(name = "plan_id")
    private Long planId;
    //计划值(小时、天、次数等等)
    @Column(name = "plan_value")
    private Long planValue;

    @Column(name = "remind")
    private Boolean remind;

    @Column(name = "rewards")
    private Integer rewards;

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

    public Long getDreamId() {
        return dreamId;
    }

    public void setDreamId(Long dreamId) {
        this.dreamId = dreamId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDreamName() {
        return dreamName;
    }

    public void setDreamName(String dreamName) {
        this.dreamName = dreamName;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Double getImportant() {
        return important;
    }

    public void setImportant(Double important) {
        this.important = important;
    }

    public Integer getExpectDays() {
        return expectDays;
    }

    public void setExpectDays(Integer expectDays) {
        this.expectDays = expectDays;
    }

    public DreamStatus getStatus() {
        return status;
    }

    public void setStatus(DreamStatus status) {
        this.status = status;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Date getExpectDate() {
        return expectDate;
    }

    public void setExpectDate(Date expectDate) {
        this.expectDate = expectDate;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Integer getDelays() {
        return delays;
    }

    public void setDelays(Integer delays) {
        this.delays = delays;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getPlanValue() {
        return planValue;
    }

    public void setPlanValue(Long planValue) {
        this.planValue = planValue;
    }

    public Boolean getRemind() {
        return remind;
    }

    public void setRemind(Boolean remind) {
        this.remind = remind;
    }

    public Integer getRewards() {
        return rewards;
    }

    public void setRewards(Integer rewards) {
        this.rewards = rewards;
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

    @Transient
    public String getStatusName() {
        return status==null? null:status.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Dream bean) {
            return bean.getDreamId().equals(this.getDreamId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dreamId);
    }

}
