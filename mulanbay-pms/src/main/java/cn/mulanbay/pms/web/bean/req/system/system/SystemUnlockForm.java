package cn.mulanbay.pms.web.bean.req.system.system;

import jakarta.validation.constraints.NotEmpty;

public class SystemUnlockForm {

    @NotEmpty(message = "解锁码不能为空")
    private String unlockCode;

    public String getUnlockCode() {
        return unlockCode;
    }

    public void setUnlockCode(String unlockCode) {
        this.unlockCode = unlockCode;
    }
}
