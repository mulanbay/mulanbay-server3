package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.CompareType;
import cn.mulanbay.pms.persistent.enums.PlanType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 用户计划
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "user_plan")
public class UserPlan implements java.io.Serializable {
    private static final long serialVersionUID = -7940254006542862747L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "plan_id", unique = true, nullable = false)
    private Long planId;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "template_id", nullable = false)
    private PlanTemplate template;

    @Column(name = "title")
    private String title;
    //用户自己选择的值
    @Column(name = "bind_values")
    private String bindValues;

    @Column(name = "plan_type")
    private PlanType planType;

    @Column(name = "compare_type")
    private CompareType compareType;

    //计划次数值
    @Column(name = "plan_count_value")
    private Long planCountValue;
    //计划值，比如购买金额，锻炼时间
    @Column(name = "plan_value")
    private Long planValue;

    @Column(name = "status")
    private CommonStatus status;

    @Column(name = "order_index")
    private Short orderIndex;

    //第一天统计日期(因为很多时候每个人，不同业务模块的开始记录时间不一样)
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "first_stat_day")
    private Date firstStatDay;

    @Column(name = "remind")
    private Boolean remind;
    @Column(name = "calendar_title")
    private String calendarTitle;

    @Column(name = "calendar_time")
    private String calendarTime;

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

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PlanTemplate getTemplate() {
        return template;
    }

    public void setTemplate(PlanTemplate template) {
        this.template = template;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBindValues() {
        return bindValues;
    }

    public void setBindValues(String bindValues) {
        this.bindValues = bindValues;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public CompareType getCompareType() {
        return compareType;
    }

    public void setCompareType(CompareType compareType) {
        this.compareType = compareType;
    }

    public Long getPlanCountValue() {
        return planCountValue;
    }

    public void setPlanCountValue(Long planCountValue) {
        this.planCountValue = planCountValue;
    }

    public Long getPlanValue() {
        return planValue;
    }

    public void setPlanValue(Long planValue) {
        this.planValue = planValue;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public Short getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Short orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Date getFirstStatDay() {
        return firstStatDay;
    }

    public void setFirstStatDay(Date firstStatDay) {
        this.firstStatDay = firstStatDay;
    }

    public Boolean getRemind() {
        return remind;
    }

    public void setRemind(Boolean remind) {
        this.remind = remind;
    }

    public String getCalendarTitle() {
        return calendarTitle;
    }

    public void setCalendarTitle(String calendarTitle) {
        this.calendarTitle = calendarTitle;
    }

    public String getCalendarTime() {
        return calendarTime;
    }

    public void setCalendarTime(String calendarTime) {
        this.calendarTime = calendarTime;
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

    @Transient
    public String getPlanTypeName() {
        return planType==null? null:planType.getName();
    }

    @Transient
    public String getCompareTypeName() {
        return compareType==null ? null:compareType.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof UserPlan bean) {
            return bean.getPlanId().equals(this.getPlanId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(planId);
    }
}
