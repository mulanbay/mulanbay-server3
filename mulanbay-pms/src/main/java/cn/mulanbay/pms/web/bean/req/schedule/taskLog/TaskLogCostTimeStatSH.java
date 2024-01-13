package cn.mulanbay.pms.web.bean.req.schedule.taskLog;

import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.schedule.enums.JobExecuteResult;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class TaskLogCostTimeStatSH extends PageSearch implements FullEndDateTime {

    @Query(fieldName = "taskTrigger.triggerId", op = Parameter.Operator.EQ)
    private Long triggerId;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "startTime", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "startTime", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "executeResult", op = Parameter.Operator.EQ)
    private JobExecuteResult executeResult;

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

    public JobExecuteResult getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(JobExecuteResult executeResult) {
        this.executeResult = executeResult;
    }
}
