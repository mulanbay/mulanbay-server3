package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 系统日志
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "sys_log")
public class SysLog implements java.io.Serializable {

    private static final long serialVersionUID = 6569869954055287227L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, length = 32)
    private String username;

    @Column(name = "error_code")
    private Integer errorCode;

    @Column(name = "log_level")
    private LogLevel logLevel;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "sys_func_id", nullable = true)
    private SysFunc sysFunc;

    @Column(name = "url_address")
    private String urlAddress;

    @Column(name = "method")
    private String method;
    //调用开始时间

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "occur_time", length = 19)
    private Date occurTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "store_time", length = 19)
    private Date storeTime;
    //存储时间=storeTime-occurTime

    @Column(name = "store_duration")
    private long storeDuration;

    /**
     * 操作者的IP地址
     */
    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "location_code")
    private String locationCode;

    @Column(name = "mac_address")
    private String macAddress;

    /**
     * 服务器IP地址
     */
    @Column(name = "host_ip_address")
    private String hostIpAddress;

    @Column(name = "paras")
    private String paras;

    /**
     * 被操作的业务主键值
     */
    @Column(name = "id_value")
    private String idValue;

    @Column(name = "exception_class_name")
    private String exceptionClassName;

    @Column(name = "remark", length = 200)
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

    /**
     * 需要在field上打上标签，否则映射异常
     */
    @Transient
    private Map paraMap;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SysFunc getSysFunc() {
        return sysFunc;
    }

    public void setSysFunc(SysFunc sysFunc) {
        this.sysFunc = sysFunc;
    }

    public String getUrlAddress() {
        return urlAddress;
    }

    public void setUrlAddress(String urlAddress) {
        this.urlAddress = urlAddress;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }

    public Date getStoreTime() {
        return storeTime;
    }

    public void setStoreTime(Date storeTime) {
        this.storeTime = storeTime;
    }

    public long getStoreDuration() {
        return storeDuration;
    }

    public void setStoreDuration(long storeDuration) {
        this.storeDuration = storeDuration;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getHostIpAddress() {
        return hostIpAddress;
    }

    public void setHostIpAddress(String hostIpAddress) {
        this.hostIpAddress = hostIpAddress;
    }

    public String getParas() {
        return paras;
    }

    public void setParas(String paras) {
        this.paras = paras;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }

    public String getExceptionClassName() {
        return exceptionClassName;
    }

    public void setExceptionClassName(String exceptionClassName) {
        this.exceptionClassName = exceptionClassName;
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
    public Map getParaMap() {
        return paraMap;
    }

    public void setParaMap(Map paraMap) {
        this.paraMap = paraMap;
    }

    @Transient
    public String getLogLevelName() {
        return logLevel == null ? null : logLevel.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SysLog bean) {
            return bean.getId().equals(this.getId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
