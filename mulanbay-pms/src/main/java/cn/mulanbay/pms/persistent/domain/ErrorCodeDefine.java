package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import cn.mulanbay.pms.persistent.enums.MonitorBussType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 错误代码定义
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "error_code_define")
public class ErrorCodeDefine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "code", unique = true, nullable = false)
    private Integer code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "level", nullable = false)
    private LogLevel level;

    /**
     * 是否需要提醒通知
     */
    @Column(name = "notifiable", nullable = false)
    private Boolean notifiable;

    /**
     * 是否实时通知
     */
    @Column(name = "realtime_notify", nullable = false)
    private Boolean realtimeNotify;

    /**
     * 是否需要记录日志
     */
    @Column(name = "loggable", nullable = false)
    private Boolean loggable;

    /**
     * 产生原因
     */
    @Column(name = "causes")
    private String causes;

    @Column(name = "solutions")
    private String solutions;

    @Column(name = "buss_type", nullable = false)
    private MonitorBussType bussType;

    /**
     * 累计发生次数
     */
    @Column(name = "count")
    private Integer count;

    /**
     * 周期(秒数)，默认是0，如果大于0表示一段时间内只会对一个用户只发送一次通知
     */
    @Column(name = "limit_period")
    private Integer limitPeriod;
    /**
     * pc端连接
     */
    @Column(name = "url")
    private String url;
    /**
     * 移动端连接
     */
    @Column(name = "mobile_url")
    private String mobileUrl;

    @Column(name = "remark")
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time")
    private Date modifyTime;

    // Property accessors
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public Boolean getNotifiable() {
        return notifiable;
    }

    public void setNotifiable(Boolean notifiable) {
        this.notifiable = notifiable;
    }

    public Boolean getRealtimeNotify() {
        return realtimeNotify;
    }

    public void setRealtimeNotify(Boolean realtimeNotify) {
        this.realtimeNotify = realtimeNotify;
    }

    public Boolean getLoggable() {
        return loggable;
    }

    public void setLoggable(Boolean loggable) {
        this.loggable = loggable;
    }

    public String getCauses() {
        return causes;
    }

    public void setCauses(String causes) {
        this.causes = causes;
    }

    public String getSolutions() {
        return solutions;
    }

    public void setSolutions(String solutions) {
        this.solutions = solutions;
    }

    public MonitorBussType getBussType() {
        return bussType;
    }

    public void setBussType(MonitorBussType bussType) {
        this.bussType = bussType;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getLimitPeriod() {
        return limitPeriod;
    }

    public void setLimitPeriod(Integer limitPeriod) {
        this.limitPeriod = limitPeriod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
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
    public String getLevelName() {
        return level == null ? null : level.getName();
    }

    @Transient
    public String getBussTypeName() {
        return bussType == null ? null : bussType.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ErrorCodeDefine bean) {
            return bean.getCode().equals(this.getCode());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
