package cn.mulanbay.schedule;

/**
 * 调度执行后的通知
 * @author fenghong
 * @create 2017-11-15 21:33
 **/
public interface MessageProcessor {

    void handleScheduleMessage(Long taskTriggerId, int code, String title, String message);
}
