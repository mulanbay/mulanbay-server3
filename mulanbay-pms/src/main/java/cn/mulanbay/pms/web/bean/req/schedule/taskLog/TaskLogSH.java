package cn.mulanbay.pms.web.bean.req.schedule.taskLog;

import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class TaskLogSH extends PageSearch implements FullEndDateTime {

    @Query(fieldName = "taskTrigger.triggerId", op = Parameter.Operator.EQ)
    private Long triggerId;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "startTime", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "startTime", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "executeResult", op = Parameter.Operator.EQ)
    private JobResult executeResult;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(Long triggerId) {
        this.triggerId = triggerId;
    }

    public JobResult getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(JobResult executeResult) {
        this.executeResult = executeResult;
    }
}
