package cn.mulanbay.pms.web.bean.res.behavior;

import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.BussType;
import jakarta.persistence.Transient;

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

    private BussSource source;

    private Date bussDay;

    private Date expireTime;

    private Boolean allDay;

    private String title;

    private String content;

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

    public BussSource getSource() {
        return source;
    }

    public void setSource(BussSource source) {
        this.source = source;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBussTypeName() {
        return bussType==null ? null:bussType.getName();
    }

    /**
     * 页面颜色分类使用
     * @return
     */
    public int getSourceTypeIndex() {
        return source == null ? 0 : source.getValue();
    }

}
