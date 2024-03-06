package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.PlanStatResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 计划时间线记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "plan_report_timeline")
public class PlanReportTimeline implements java.io.Serializable {
    private static final long serialVersionUID = 7814575336496758772L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "timeline_id", unique = true, nullable = false)
    private Long timelineId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "timeline_name")
    private String timelineName;

    //业务日期索引:如201701（2017一月份），2017，201728（第28周）
    @Column(name = "buss_key")
    private String bussKey;
    //业务日期
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "buss_day")
    private Date bussDay;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private UserPlan plan;
    @Column(name = "report_count_value")
    private Long reportCountValue;

    //值，比如购买金额，锻炼时间
    @Column(name = "report_value")
    private Long reportValue;

    //计算出来的实际值
    @Column(name = "count_value_result")
    private PlanStatResult countValueResult;

    @Column(name = "value_result")
    private PlanStatResult valueResult;

    //下面两个冗余，因为不同时期的值会不一样（计划的值）
    //次数值
    @Column(name = "plan_count_value")
    private Long planCountValue;
    //值，比如购买金额，锻炼时间
    @Column(name = "plan_value")
    private Long planValue;

    //数据参考值的年份
    @Column(name = "compare_year")
    private Integer compareYear;
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

    public String getTimelineName() {
        return timelineName;
    }

    public void setTimelineName(String timelineName) {
        this.timelineName = timelineName;
    }

    public String getBussKey() {
        return bussKey;
    }

    public void setBussKey(String bussKey) {
        this.bussKey = bussKey;
    }

    public Date getBussDay() {
        return bussDay;
    }

    public void setBussDay(Date bussDay) {
        this.bussDay = bussDay;
    }

    public UserPlan getPlan() {
        return plan;
    }

    public void setPlan(UserPlan plan) {
        this.plan = plan;
    }

    public Long getReportCountValue() {
        return reportCountValue;
    }

    public void setReportCountValue(Long reportCountValue) {
        this.reportCountValue = reportCountValue;
    }

    public Long getReportValue() {
        return reportValue;
    }

    public void setReportValue(Long reportValue) {
        this.reportValue = reportValue;
    }

    public PlanStatResult getCountValueResult() {
        return countValueResult;
    }

    public void setCountValueResult(PlanStatResult countValueResult) {
        this.countValueResult = countValueResult;
    }

    public PlanStatResult getValueResult() {
        return valueResult;
    }

    public void setValueResult(PlanStatResult valueResult) {
        this.valueResult = valueResult;
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

    public Integer getCompareYear() {
        return compareYear;
    }

    public void setCompareYear(Integer compareYear) {
        this.compareYear = compareYear;
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

    /**
     * 判断是个没有计划配置值
     *
     * @return
     */
    public boolean checkPlanValuesEmpty() {
        if (compareYear == null || planCountValue == null || planValue == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PlanReportTimeline bean) {
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
