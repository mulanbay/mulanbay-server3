package cn.mulanbay.pms.web.bean.req.schedule.taskTrigger;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.schedule.enums.RedoType;
import cn.mulanbay.schedule.enums.TaskUniqueType;
import cn.mulanbay.schedule.enums.TriggerStatus;
import cn.mulanbay.schedule.enums.TriggerType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class TaskTriggerForm implements BindUser {

    private Long triggerId;

    @NotEmpty(message = "调度名称不能为空")
    private String triggerName;

    //目前没有什么用
    private Long userId;

    @NotEmpty(message = "部署点不能为空")
    private String deployId;

    @NotEmpty(message = "调度类不能为空")
    private String taskClass;

    @NotNull(message = "是否支持分布式不能为空")
    private Boolean distriable;

    @NotNull(message = "重做类型不能为空")
    private RedoType redoType;

    @NotNull(message = "允许重做次数不能为空")
    private Integer allowedRedoTimes;

    @NotNull(message = "超时时间不能为空")
    private Long timeout;

    @NotEmpty(message = "组名不能为空")
    private String groupName;

    @NotNull(message = "触发类型类型不能为空")
    private TriggerType triggerType;

    @NotNull(message = "触发周期不能为空")
    private Integer triggerInterval;

    private String triggerParas;
    private String cronExpression;

    @NotNull(message = "日期偏移量不能为空")
    private Integer offsetDays;

    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @NotNull(message = "首次执行时间不能为空")
    private Date firstExecuteTime;

    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date nextExecuteTime;

    @NotNull(message = "状态不能为空")
    private TriggerStatus triggerStatus;

    @NotNull(message = "是否需要检查唯一性不能为空")
    private Boolean checkUnique;

    @NotNull(message = "唯一性类型不能为空")
    private TaskUniqueType uniqueType;

    @NotNull(message = "是否记录日志不能为空")
    private Boolean loggable;

    @NotNull(message = "是否需要通知不能为空")
    private Boolean notifiable;

    private String execTimePeriods;

    private String comment;

    public Long getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(Long triggerId) {
        this.triggerId = triggerId;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDeployId() {
        return deployId;
    }

    public void setDeployId(String deployId) {
        this.deployId = deployId;
    }

    public String getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(String taskClass) {
        this.taskClass = taskClass;
    }

    public Boolean getDistriable() {
        return distriable;
    }

    public void setDistriable(Boolean distriable) {
        this.distriable = distriable;
    }

    public RedoType getRedoType() {
        return redoType;
    }

    public void setRedoType(RedoType redoType) {
        this.redoType = redoType;
    }

    public Integer getAllowedRedoTimes() {
        return allowedRedoTimes;
    }

    public void setAllowedRedoTimes(Integer allowedRedoTimes) {
        this.allowedRedoTimes = allowedRedoTimes;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public Integer getTriggerInterval() {
        return triggerInterval;
    }

    public void setTriggerInterval(Integer triggerInterval) {
        this.triggerInterval = triggerInterval;
    }

    public String getTriggerParas() {
        return triggerParas;
    }

    public void setTriggerParas(String triggerParas) {
        this.triggerParas = triggerParas;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Integer getOffsetDays() {
        return offsetDays;
    }

    public void setOffsetDays(Integer offsetDays) {
        this.offsetDays = offsetDays;
    }

    public Date getFirstExecuteTime() {
        return firstExecuteTime;
    }

    public void setFirstExecuteTime(Date firstExecuteTime) {
        this.firstExecuteTime = firstExecuteTime;
    }

    public Date getNextExecuteTime() {
        return nextExecuteTime;
    }

    public void setNextExecuteTime(Date nextExecuteTime) {
        this.nextExecuteTime = nextExecuteTime;
    }

    public TriggerStatus getTriggerStatus() {
        return triggerStatus;
    }

    public void setTriggerStatus(TriggerStatus triggerStatus) {
        this.triggerStatus = triggerStatus;
    }

    public Boolean getCheckUnique() {
        return checkUnique;
    }

    public void setCheckUnique(Boolean checkUnique) {
        this.checkUnique = checkUnique;
    }

    public TaskUniqueType getUniqueType() {
        return uniqueType;
    }

    public void setUniqueType(TaskUniqueType uniqueType) {
        this.uniqueType = uniqueType;
    }

    public Boolean getLoggable() {
        return loggable;
    }

    public void setLoggable(Boolean loggable) {
        this.loggable = loggable;
    }

    public Boolean getNotifiable() {
        return notifiable;
    }

    public void setNotifiable(Boolean notifiable) {
        this.notifiable = notifiable;
    }

    public String getExecTimePeriods() {
        return execTimePeriods;
    }

    public void setExecTimePeriods(String execTimePeriods) {
        this.execTimePeriods = execTimePeriods;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
