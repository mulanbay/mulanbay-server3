package cn.mulanbay.pms.web.bean.res.behavior;

import cn.mulanbay.pms.persistent.enums.BussType;

import java.util.Date;

/**
 * @author fenghong
 * @date 2024/3/6
 */
public class BehaviorCalendarVo {

    /**
     * 因为有很多的来源，这里的ID=sourceType+sourceId
     */
    private String id;

    private String value;

    private String unit;

    private Long sourceId;

    private BussType bussType;

    private Date bussDay;

    private Date expireTime;

    private Boolean allDay;

    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public BussType getBussType() {
        return bussType;
    }

    public void setBussType(BussType bussType) {
        this.bussType = bussType;
    }

    public Date getBussDay() {
        return bussDay;
    }

    public void setBussDay(Date bussDay) {
        this.bussDay = bussDay;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
