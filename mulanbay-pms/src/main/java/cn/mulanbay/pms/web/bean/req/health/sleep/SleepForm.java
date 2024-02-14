package cn.mulanbay.pms.web.bean.req.health.sleep;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class SleepForm implements BindUser {

    private Long sleepId;

    private Long userId;

    //睡觉时间
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date sleepTime;

    //起床时间
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date getUpTime;

    //首次醒来时间
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date fwpTime;

    //最后一次醒来时间
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date lwpTime;

    //醒来次数
    private Integer wps;

    //浅睡时长（分钟）
    private Integer lightSleep;

    //深睡时长（分钟）
    private Integer deepSleep;

    //睡眠质量（0-10分）
    private Integer quality;

    private String remark;

    public Long getSleepId() {
        return sleepId;
    }

    public void setSleepId(Long sleepId) {
        this.sleepId = sleepId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Date sleepTime) {
        this.sleepTime = sleepTime;
    }

    public Date getGetUpTime() {
        return getUpTime;
    }

    public void setGetUpTime(Date getUpTime) {
        this.getUpTime = getUpTime;
    }

    public Date getFwpTime() {
        return fwpTime;
    }

    public void setFwpTime(Date fwpTime) {
        this.fwpTime = fwpTime;
    }

    public Date getLwpTime() {
        return lwpTime;
    }

    public void setLwpTime(Date lwpTime) {
        this.lwpTime = lwpTime;
    }

    public Integer getWps() {
        return wps;
    }

    public void setWps(Integer wps) {
        this.wps = wps;
    }

    public Integer getLightSleep() {
        return lightSleep;
    }

    public void setLightSleep(Integer lightSleep) {
        this.lightSleep = lightSleep;
    }

    public Integer getDeepSleep() {
        return deepSleep;
    }

    public void setDeepSleep(Integer deepSleep) {
        this.deepSleep = deepSleep;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
