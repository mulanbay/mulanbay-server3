package cn.mulanbay.pms.web.bean.req.auth.user;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.AuthType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class MyInfoForm implements BindUser {

    private Long userId;

    @NotEmpty(message = "用户名不能为空")
    private String username;

    //@NotEmpty(message = "{validate.user.nickname.notEmpty}")
    private String nickname;

    //验证使用
    @NotEmpty(message = "密码不能为空")
    private String password;

    private String newPassword;

    @NotNull(message = "二次授权类型不能为空")
    private AuthType secAuthType;

    //@NotEmpty(message = "{validate.user.phone.notEmpty}")
    private String phone;

    //邮件发送
    //@NotEmpty(message = "{validate.user.email.notEmpty}")
    private String email;

    //生日（计算最大心率使用到）
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "生日不能为空")
    private Date birthday;

    @NotNull(message = "是否发送EMail不能为空")
    private Boolean sendEmail;

    @NotNull(message = "是否发送微信消息不能为空")
    private Boolean sendWx;

    private String residentCity;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public AuthType getSecAuthType() {
        return secAuthType;
    }

    public void setSecAuthType(AuthType secAuthType) {
        this.secAuthType = secAuthType;
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

    public Boolean getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(Boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public Boolean getSendWx() {
        return sendWx;
    }

    public void setSendWx(Boolean sendWx) {
        this.sendWx = sendWx;
    }

    public String getResidentCity() {
        return residentCity;
    }

    public void setResidentCity(String residentCity) {
        this.residentCity = residentCity;
    }
}
