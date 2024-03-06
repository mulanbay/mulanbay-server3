package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 通用记录类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "common_data_type")
public class CommonDataType implements java.io.Serializable {

    // Fields

    /**
     *
     */
    private static final long serialVersionUID = 8575214853538973501L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "type_id", unique = true, nullable = false)
    private Long typeId;

    @Column(name = "type_name", nullable = false, length = 32)
    private String typeName;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "unit")
    private String unit;
    //加入月分析
    @Column(name = "month_stat")
    private Boolean monthStat;

    @Column(name = "year_stat")
    private Boolean yearStat;
    // 加入八小时之外统计
    @Column(name = "over_work_stat")
    private Boolean overWorkStat;

    @Column(name = "status", nullable = false)
    private CommonStatus status;

    @Column(name = "order_index", nullable = false)
    private Short orderIndex;

    //奖励积分(正为加，负为减)
    @Column(name = "reward_point")
    private Integer rewardPoint;

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

    // Constructors

    // Property accessors


    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getMonthStat() {
        return monthStat;
    }

    public void setMonthStat(Boolean monthStat) {
        this.monthStat = monthStat;
    }

    public Boolean getYearStat() {
        return yearStat;
    }

    public void setYearStat(Boolean yearStat) {
        this.yearStat = yearStat;
    }

    public Boolean getOverWorkStat() {
        return overWorkStat;
    }

    public void setOverWorkStat(Boolean overWorkStat) {
        this.overWorkStat = overWorkStat;
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

    public Integer getRewardPoint() {
        return rewardPoint;
    }

    public void setRewardPoint(Integer rewardPoint) {
        this.rewardPoint = rewardPoint;
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
        if (other instanceof CommonDataType bean) {
            return bean.getTypeId().equals(this.getTypeId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(typeId);
    }
}