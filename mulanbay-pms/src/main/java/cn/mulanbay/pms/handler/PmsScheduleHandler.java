package cn.mulanbay.pms.handler;

import cn.mulanbay.common.queue.LimitQueue;
import cn.mulanbay.schedule.ScheduleInfo;
import cn.mulanbay.schedule.handler.ScheduleHandler;
import org.springframework.stereotype.Component;

/**
 * 调度处理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class PmsScheduleHandler extends ScheduleHandler {

    private LimitQueue<ScheduleInfo> monitorQueue;

    public LimitQueue<ScheduleInfo> getMonitorQueue() {
        return monitorQueue;
    }

    public void setMonitorQueue(LimitQueue<ScheduleInfo> monitorQueue) {
        this.monitorQueue = monitorQueue;
    }
}
