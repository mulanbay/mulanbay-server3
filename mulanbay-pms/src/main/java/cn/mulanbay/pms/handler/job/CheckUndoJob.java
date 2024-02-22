package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.pms.persistent.service.PmsScheduleService;
import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.domain.TaskLog;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;

import java.util.Date;
import java.util.List;

/**
 * 检查没有执行的任务
 * @author fenghong
 * @date 2024/2/21
 */
public class CheckUndoJob extends AbstractBaseJob {

    CheckUndoJobPara para;

    @Override
    public TaskResult doTask() {
        TaskResult tr = new TaskResult();
        PmsScheduleService service = BeanFactoryUtil.getBean(PmsScheduleService.class);
        Date minDate = DateUtil.getDate(-para.getDays(),new Date());
        List<TaskLog> list = service.getCheckLogList(minDate);
        //检查缺失的
        tr.setResult(JobResult.SUCCESS);
        return tr;
    }

    @Override
    public ParaCheckResult checkTriggerPara() {
        para = this.getTriggerParaBean();
        if (para == null) {
            para = new CheckUndoJobPara();
        }
        return DEFAULT_SUCCESS_PARA_CHECK;
    }

    @Override
    public Class getParaDefine() {
        return CheckUndoJobPara.class;
    }
}
