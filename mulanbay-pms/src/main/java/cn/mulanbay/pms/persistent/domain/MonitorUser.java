package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.MonitorBussType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 系统监控用户
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "monitor_user")
public class MonitorUser implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "buss_type")
    private MonitorBussType bussType;

    @Column(name = "sms_notify")
    private Boolean smsNotify;

    @Column(name = "wx_notify")
    private Boolean wxNotify;

    @Column(name = "sys_msg_notify")
    private Boolean sysMsgNotify;

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

    public MonitorBussType getBussType() {
        return bussType;
    }

    public void setBussType(MonitorBussType bussType) {
        this.bussType = bussType;
    }

    public Boolean getSmsNotify() {
        return smsNotify;
    }

    public void setSmsNotify(Boolean smsNotify) {
        this.smsNotify = smsNotify;
    }

    public Boolean getWxNotify() {
        return wxNotify;
    }

    public void setWxNotify(Boolean wxNotify) {
        this.wxNotify = wxNotify;
    }

    public Boolean getSysMsgNotify() {
        return sysMsgNotify;
    }

    public void setSysMsgNotify(Boolean sysMsgNotify) {
        this.sysMsgNotify = sysMsgNotify;
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
        if (other instanceof MonitorUser bean) {
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
