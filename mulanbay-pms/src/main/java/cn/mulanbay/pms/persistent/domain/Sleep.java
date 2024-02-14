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
 * 睡眠记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "sleep")
public class Sleep implements java.io.Serializable {

    private static final long serialVersionUID = 678113094988722465L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "sleep_id", unique = true, nullable = false)
    private Long sleepId;

    @Column(name = "user_id")
    private Long userId;

    //睡眠日
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "sleep_date", length = 10)
    private Date sleepDate;

    //睡觉时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "sleep_time")
    private Date sleepTime;

    //起床时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "get_up_time")
    private Date getUpTime;

    //首次醒来时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "fwp_time")
    private Date fwpTime;

    //最后一次醒来时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "lwp_time")
    private Date lwpTime;

    //醒来次数
    @Column(name = "wps")
    private Integer wps;

    //睡眠时长(分钟)
    @Column(name = "duration")
    private Integer duration;

    //浅睡时长（分钟）
    @Column(name = "light_sleep")
    private Integer lightSleep;

    //深睡时长（分钟）
    @Column(name = "deep_sleep")
    private Integer deepSleep;

    //睡眠质量（0-5分）
    @Column(name = "quality")
    private Integer quality;

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

    public Long getSleepId() {
        return sleepId;
    }

    public void setSleepId(Long sleepId) {
        this.sleepId = sleepId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getSleepDate() {
        return sleepDate;
    }

    public void setSleepDate(Date sleepDate) {
        this.sleepDate = sleepDate;
    }

    public Date getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Date sleepTime) {
        this.sleepTime = sleepTime;
    }

    public Date getGetUpTime() {
        return getUpTime;
    }

    public void setGetUpTime(Date getUpTime) {
        this.getUpTime = getUpTime;
    }

    public Date getFwpTime() {
        return fwpTime;
    }

    public void setFwpTime(Date fwpTime) {
        this.fwpTime = fwpTime;
    }

    public Date getLwpTime() {
        return lwpTime;
    }

    public void setLwpTime(Date lwpTime) {
        this.lwpTime = lwpTime;
    }

    public Integer getWps() {
        return wps;
    }

    public void setWps(Integer wps) {
        this.wps = wps;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getLightSleep() {
        return lightSleep;
    }

    public void setLightSleep(Integer lightSleep) {
        this.lightSleep = lightSleep;
    }

    public Integer getDeepSleep() {
        return deepSleep;
    }

    public void setDeepSleep(Integer deepSleep) {
        this.deepSleep = deepSleep;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
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
        if (other instanceof Sleep bean) {
            return bean.getSleepId().equals(this.getSleepId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sleepId);
    }
}
