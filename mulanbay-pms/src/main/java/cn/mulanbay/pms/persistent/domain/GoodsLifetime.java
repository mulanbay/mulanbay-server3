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
 * 商品的寿命配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "goods_lifetime")
public class GoodsLifetime implements java.io.Serializable {

    private static final long serialVersionUID = 870031048037177916L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "lifetime_id", unique = true, nullable = false)
    private Long lifetimeId;

    @Column(name = "lifetime_name")
    private String lifetimeName;

    @Column(name = "tags")
    private String tags;

    @Column(name = "days")
    private Integer days;

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

    public Long getLifetimeId() {
        return lifetimeId;
    }

    public void setLifetimeId(Long lifetimeId) {
        this.lifetimeId = lifetimeId;
    }

    public String getLifetimeName() {
        return lifetimeName;
    }

    public void setLifetimeName(String lifetimeName) {
        this.lifetimeName = lifetimeName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
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
        if (other instanceof GoodsLifetime bean) {
            return bean.getLifetimeId().equals(this.getLifetimeId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(lifetimeId);
    }
}
