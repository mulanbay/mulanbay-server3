package cn.mulanbay.pms.web.bean.req.schedule.taskTrigger;

import jakarta.validation.constraints.NotEmpty;

public class DeleteLockKeyForm {

    @NotEmpty(message = "key不能为空")
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
