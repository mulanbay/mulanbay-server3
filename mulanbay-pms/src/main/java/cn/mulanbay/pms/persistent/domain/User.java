package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.AuthType;
import cn.mulanbay.pms.persistent.enums.FamilyMode;
import cn.mulanbay.pms.persistent.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.util.Date;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 用户
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "user")
public class User implements java.io.Serializable {

    private static final long serialVersionUID = -8290503768108861704L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    /**
     * 邮件发送
     */
    @Column(name = "email")
    private String email;
    /**
     * 生日（计算最大心率使用到）
     */
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "birthday")
    private Date birthday;

    /**
     * 后期和模版绑定
     * 不同等级的用户可以看到的计划模版、行为习惯模版会不一样
     * 等级越高，看到的内容会更多
     */
    @Column(name = "level")
    private Integer level;

    @Column(name = "points")
    private Integer points;

    /**
     * 二次认证类型(针对某些功能点)
     */
    @Column(name = "sec_auth_type")
    private AuthType secAuthType;

    /**
     * 最后的登陆token
     */
    @Column(name = "last_login_token")
    private String lastLoginToken;

    /**
     * 最后登录的模式
     */
    @Column(name = "last_family_mode")
    private FamilyMode lastFamilyMode;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "last_login_time")
    private Date lastLoginTime;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    /**
     * 用户状态
     */
    @Column(name = "status")
    private UserStatus status;

    /**
     * 用户过期时间
     */
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "expire_time")
    private Date expireTime;

    /**
     * 头像地址
     */
    @Column(name = "avatar")
    private String avatar;

    @Column(name = "remark")
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time")
    private Date modifyTime;

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


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public AuthType getSecAuthType() {
        return secAuthType;
    }

    public void setSecAuthType(AuthType secAuthType) {
        this.secAuthType = secAuthType;
    }

    public String getLastLoginToken() {
        return lastLoginToken;
    }

    public void setLastLoginToken(String lastLoginToken) {
        this.lastLoginToken = lastLoginToken;
    }

    public FamilyMode getLastFamilyMode() {
        return lastFamilyMode;
    }

    public void setLastFamilyMode(FamilyMode lastFamilyMode) {
        this.lastFamilyMode = lastFamilyMode;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
    public String getStatusName() {
        if (status != null) {
            return status.getName();
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof User) {
            User bean = (User) other;
            return bean.getUserId().equals(this.getUserId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Transient
    public String getSecAuthTypeName() {
        if (this.secAuthType != null) {
            return secAuthType.getName();
        } else {
            return null;
        }
    }
}
