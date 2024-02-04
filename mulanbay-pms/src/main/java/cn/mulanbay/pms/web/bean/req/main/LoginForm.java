package cn.mulanbay.pms.web.bean.req.main;

import cn.mulanbay.pms.persistent.enums.FamilyMode;
import jakarta.validation.constraints.NotEmpty;

public class LoginForm {

    @NotEmpty(message = "用户名不能为空")
    private String username;

    @NotEmpty(message = "密码不能为空")
    private String password;

    /**
     * 图形验证码ID
     */
    @NotEmpty(message = "图形验证码ID不能为空")
    private String uuid;

    /**
     * 图形验证码
     */
    @NotEmpty(message = "图形验证码不能为空")
    private String code;


    //家庭模式
    private FamilyMode familyMode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public FamilyMode getFamilyMode() {
        return familyMode;
    }

    public void setFamilyMode(FamilyMode familyMode) {
        this.familyMode = familyMode;
    }
}
