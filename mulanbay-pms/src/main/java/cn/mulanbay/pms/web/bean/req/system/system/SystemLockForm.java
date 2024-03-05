package cn.mulanbay.pms.web.bean.req.system.system;

import cn.mulanbay.pms.common.Constant;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class SystemLockForm {

    @NotNull(message = "状态码不能为空")
    private Integer status;

    @NotEmpty(message = "消息不能为空")
    private String message;

    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date expireTime;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
