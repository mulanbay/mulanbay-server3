package cn.mulanbay.pms.persistent.dto.calendar;

import java.io.Serializable;
import java.util.Date;

public class CalendarLogDTO implements Serializable {

    private static final long serialVersionUID = 2561383920772533581L;

    /**
     * 原始ID
     */
    private Long sourceId;

    private Date date;

    private String value;

    private String unit;

    private String name;

    private int days;

    private String content;

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
