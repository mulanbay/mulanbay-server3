package cn.mulanbay.pms.handler.job;

import cn.mulanbay.schedule.para.AbstractTriggerPara;
import cn.mulanbay.schedule.para.EditType;
import cn.mulanbay.schedule.para.JobParameter;

public class ScheduleMonitorJobPara extends AbstractTriggerPara {

    @JobParameter(name = "记录保留条数", dataType = Integer.class, desc = "条", editType = EditType.NUMBER)
    private int queueSize = 1440;

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

}
