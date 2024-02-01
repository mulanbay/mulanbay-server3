package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 预算的时间线
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "budget_timeline")
public class BudgetTimeline implements java.io.Serializable {

    private static final long serialVersionUID = 6891444266523508770L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "timeline_id", unique = true, nullable = false)
    private Long timelineId;

    @Column(name = "user_id")
    private Long userId;
    /**
     * 指的是统计的周期类型
     */
    @Column(name = "stat_period")
    private PeriodType statPeriod;
    //业务key，唯一性判断使用，避免重复设置
    @Column(name = "buss_key")
    private String bussKey;
    //发生的时间
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "buss_day")
    private Date bussDay;
    //总共多少天
    @Column(name = "total_days")
    private Integer totalDays;
    //已经过去多少天
    @Column(name = "pass_days")
    private Integer passDays;
    //预算金额
    @Column(name = "budget_amount",precision = 9,scale = 2)
    private BigDecimal budgetAmount;
    //实际普通消费金额
    @Column(name = "nc_amount",precision = 9,scale = 2)
    private BigDecimal ncAmount;
    //实际突发消费金额
    @Column(name = "bc_amount",precision = 9,scale = 2)
    private BigDecimal bcAmount;
    //实际看病消费金额
    @Column(name = "tr_amount",precision = 9,scale = 2)
    private BigDecimal trAmount;
    @Column(name = "remark")
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time")
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

    public PeriodType getStatPeriod() {
        return statPeriod;
    }

    public void setStatPeriod(PeriodType statPeriod) {
        this.statPeriod = statPeriod;
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

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public Integer getPassDays() {
        return passDays;
    }

    public void setPassDays(Integer passDays) {
        this.passDays = passDays;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public BigDecimal getNcAmount() {
        return ncAmount;
    }

    public void setNcAmount(BigDecimal ncAmount) {
        this.ncAmount = ncAmount;
    }

    public BigDecimal getBcAmount() {
        return bcAmount;
    }

    public void setBcAmount(BigDecimal bcAmount) {
        this.bcAmount = bcAmount;
    }

    public BigDecimal getTrAmount() {
        return trAmount;
    }

    public void setTrAmount(BigDecimal trAmount) {
        this.trAmount = trAmount;
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
    public String getStatPeriodName() {
        return statPeriod.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof BudgetTimeline bean) {
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
