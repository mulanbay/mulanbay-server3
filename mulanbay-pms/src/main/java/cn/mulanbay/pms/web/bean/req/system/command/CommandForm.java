package cn.mulanbay.pms.web.bean.req.system.command;

import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CommandForm  {

    private Long id;

    @NotEmpty(message = "代码不能为空")
    private String code;

    @NotEmpty(message = "名称不能为空")
    private String name;

    //简单的四位数字代码，目前用于微信的公众号
    @NotEmpty(message = "S名称不能为空")
    private String scode;

    @NotEmpty(message = "URL不能为空")
    private String url;

    @NotNull(message = "级别不能为空")
    private LogLevel level;

    @NotNull(message = "排序号不能为空")
    private Short orderIndex;

    @NotNull(message = "状态不能为空")
    private CommonStatus status;

    @Column(name = "remark")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public Short getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Short orderIndex) {
        this.orderIndex = orderIndex;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
