package cn.mulanbay.pms.handler.bean.calendar;

import cn.mulanbay.pms.persistent.domain.UserCalendar;

public class UserCalendarBean extends UserCalendar {

    /**
     * 因为有很多的来源，这里的ID=sourceType+sourceId
     */
    private String id;

    private String value;

    private String unit;

    /**
     * 日志流水是否和原日历设置一样
     */
    private boolean match = true;

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

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }
}
