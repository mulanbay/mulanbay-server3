package cn.mulanbay.pms.web.bean.res.data.calendar;

import cn.mulanbay.pms.handler.bean.data.CommonDataBean;
import cn.mulanbay.pms.persistent.domain.Message;
import cn.mulanbay.pms.persistent.domain.UserCalendar;

/**
 * @author fenghong
 * @date 2024/3/4
 */
public class UserCalendarSourceVo {

    /**
     * 原信息
     */
    private CommonDataBean sourceData;

    /**
     * 源消息
     */
    private Message sourceMessage;

    /**
     * 完成时信息
     */
    private CommonDataBean finishSourceData;

    /**
     * 完成时消息
     */
    private Message finishSourceMessage;

    private UserCalendar calendarData;

    public CommonDataBean getSourceData() {
        return sourceData;
    }

    public void setSourceData(CommonDataBean sourceData) {
        this.sourceData = sourceData;
    }

    public Message getSourceMessage() {
        return sourceMessage;
    }

    public void setSourceMessage(Message sourceMessage) {
        this.sourceMessage = sourceMessage;
    }

    public CommonDataBean getFinishSourceData() {
        return finishSourceData;
    }

    public void setFinishSourceData(CommonDataBean finishSourceData) {
        this.finishSourceData = finishSourceData;
    }

    public Message getFinishSourceMessage() {
        return finishSourceMessage;
    }

    public void setFinishSourceMessage(Message finishSourceMessage) {
        this.finishSourceMessage = finishSourceMessage;
    }

    public UserCalendar getCalendarData() {
        return calendarData;
    }

    public void setCalendarData(UserCalendar calendarData) {
        this.calendarData = calendarData;
    }
}
