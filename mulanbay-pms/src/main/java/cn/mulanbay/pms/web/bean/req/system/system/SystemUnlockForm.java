package cn.mulanbay.pms.web.bean.req.system.system;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SystemUnlockForm {

    @NotNull(message = "解锁状态码不能为空")
    private int status;

    @NotEmpty(message = "解锁码不能为空")
    private String unlockCode;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUnlockCode() {
        return unlockCode;
    }

    public void setUnlockCode(String unlockCode) {
        this.unlockCode = unlockCode;
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
}
