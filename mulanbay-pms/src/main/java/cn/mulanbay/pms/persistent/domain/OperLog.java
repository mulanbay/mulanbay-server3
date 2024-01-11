package cn.mulanbay.pms.persistent.domain;
import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.Date;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 操作日志
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "oper_log")
public class OperLog implements java.io.Serializable {

    private static final long serialVersionUID = 1044394976565512347L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, length = 32)
    private String username;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "sys_func_id", nullable = true)
    private SysFunc sysFunc;

    @Column(name = "url_address")
    private String urlAddress;

    @Column(name = "method")
    private String method;
    /**
     * 调用开始时间
     */
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "occur_start_time", length = 19)
    private Date occurStartTime;
    /**
     * 调用结束时间
     */
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "occur_end_time", length = 19)
    private Date occurEndTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "store_time", length = 19)
    private Date storeTime;

    /**
     * 调用处理时间=occurEndTime-occurStartTime
     * 毫秒
     */
    @Column(name = "handle_duration")
    private long handleDuration;

    /**
     * 存储时间=storeTime-occurEndTime
     * 毫秒
     */
    @Column(name = "store_duration")
    private long storeDuration;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "location_code")
    private String locationCode;

    @Column(name = "mac_address")
    private String macAddress;

    @Column(name = "host_ip_address")
    private String hostIpAddress;

    @Column(name = "paras")
    private String paras;

    @Column(name = "id_value")
    private String idValue;

    @Column(name = "return_data")
    private String returnData;

    @Column(name = "remark", length = 200)
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time", length = 19)
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time", length = 19)
    private Date modifyTime;

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

    public Date getOccurStartTime() {
        return occurStartTime;
    }

    public void setOccurStartTime(Date occurStartTime) {
        this.occurStartTime = occurStartTime;
    }

    public Date getOccurEndTime() {
        return occurEndTime;
    }

    public void setOccurEndTime(Date occurEndTime) {
        this.occurEndTime = occurEndTime;
    }

    public Date getStoreTime() {
        return storeTime;
    }

    public void setStoreTime(Date storeTime) {
        this.storeTime = storeTime;
    }

    public long getHandleDuration() {
        return handleDuration;
    }

    public void setHandleDuration(long handleDuration) {
        this.handleDuration = handleDuration;
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

    public String getReturnData() {
        return returnData;
    }

    public void setReturnData(String returnData) {
        this.returnData = returnData;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "created_time", length = 19)
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
        if (other instanceof OperLog bean) {
            return bean.getId().equals(this.getId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
