package cn.mulanbay.pms.web.bean.res.log.message;

/**
 * 消息详情
 * @author fenghong
 * @date 2024/1/14
 */
public class MessageUserVo {
    private Long userId;

    private String username;

    private String nickname;

    private String phone;

    private String email;

    private Boolean sendEmail;

    private Boolean sendWx;

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
}
