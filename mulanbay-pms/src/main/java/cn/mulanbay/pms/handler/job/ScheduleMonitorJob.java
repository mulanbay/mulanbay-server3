package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.queue.LimitQueue;
import cn.mulanbay.pms.handler.PmsScheduleHandler;
import cn.mulanbay.schedule.para.ParaCheckResult;
import cn.mulanbay.schedule.ScheduleInfo;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;

import java.util.Date;

/**
 * 调度监控任务
 * @author fenghong
 * @date 2024/3/5
 */
public class ScheduleMonitorJob extends AbstractBaseJob {

    private ScheduleMonitorJobPara para;

    @Override
    public TaskResult doTask() {
        TaskResult tr = new TaskResult();
        PmsScheduleHandler scheduleHandler = BeanFactoryUtil.getBean(PmsScheduleHandler.class);
        LimitQueue<ScheduleInfo> queue = scheduleHandler.getMonitorQueue();
        if (queue == null) {
            queue = new LimitQueue<ScheduleInfo>(para.getQueueSize());
        }
        ScheduleInfo si = scheduleHandler.getScheduleInfo();
        si.setDate(new Date());
        queue.offer(si);
        scheduleHandler.setMonitorQueue(queue);
        return tr;
    }

    @Override
    public ParaCheckResult checkTriggerPara() {
        para = this.getTriggerParaBean();
        if (para == null) {
            para = new ScheduleMonitorJobPara();
        }
        return DEFAULT_SUCCESS_PARA_CHECK;
    }

    @Override
    public Class getParaDefine() {
        return ScheduleMonitorJobPara.class;
    }
}
