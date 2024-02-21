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
 * 梦想延迟
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "dream_delay")
public class DreamDelay implements java.io.Serializable {

    private static final long serialVersionUID = 3974478414998752381L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "delay_id", unique = true, nullable = false)
    private Long delayId;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dream_id", nullable = false)
    private Dream dream;

    /**
     * 修改开始前的日期
     */
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "from_date")
    private Date fromDate;

    /**
     * 修改后的日期
     */
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "to_date")
    private Date toDate;

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

    public Long getDelayId() {
        return delayId;
    }

    public void setDelayId(Long delayId) {
        this.delayId = delayId;
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

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
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
        if (other instanceof DreamDelay bean) {
            return bean.getDelayId().equals(this.getDelayId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(delayId);
    }
}
