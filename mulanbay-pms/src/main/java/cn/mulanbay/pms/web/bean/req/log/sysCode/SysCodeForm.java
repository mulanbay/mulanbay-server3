package cn.mulanbay.pms.web.bean.req.log.sysCode;

import cn.mulanbay.pms.persistent.enums.LogLevel;
import cn.mulanbay.pms.persistent.enums.MonitorBussType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SysCodeForm {

    @NotNull(message = "代码不能为空")
    private Integer code;

    @NotEmpty(message = "名称不能为空")
    private String name;

    @NotNull(message = "等级不能为空")
    private LogLevel level;

    @NotNull(message = "是否通知不能为空")
    private Boolean notifiable;

    @NotNull(message = "是否实时通知不能为空")
    private Boolean realtime;

    @NotNull(message = "是否记录日志不能为空")
    private Boolean loggable;

    @NotEmpty(message = "产生原因不能为空")
    private String causes;

    private String solutions;

    @NotNull(message = "业务类型不能为空")
    private MonitorBussType bussType;

    private Integer limitPeriod;

    /**
     * pc端连接
     */
    private String url;
    /**
     * 移动端连接
     */
    private String mobileUrl;

    private String remark;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public Boolean getNotifiable() {
        return notifiable;
    }

    public void setNotifiable(Boolean notifiable) {
        this.notifiable = notifiable;
    }

    public Boolean getRealtime() {
        return realtime;
    }

    public void setRealtime(Boolean realtime) {
        this.realtime = realtime;
    }

    public Boolean getLoggable() {
        return loggable;
    }

    public void setLoggable(Boolean loggable) {
        this.loggable = loggable;
    }

    public String getCauses() {
        return causes;
    }

    public void setCauses(String causes) {
        this.causes = causes;
    }

    public String getSolutions() {
        return solutions;
    }

    public void setSolutions(String solutions) {
        this.solutions = solutions;
    }

    public MonitorBussType getBussType() {
        return bussType;
    }

    public void setBussType(MonitorBussType bussType) {
        this.bussType = bussType;
    }

    public Integer getLimitPeriod() {
        return limitPeriod;
    }

    public void setLimitPeriod(Integer limitPeriod) {
        this.limitPeriod = limitPeriod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
