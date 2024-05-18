package cn.mulanbay.schedule;

import cn.mulanbay.schedule.domain.TaskLog;
import cn.mulanbay.schedule.domain.TaskServer;
import cn.mulanbay.schedule.domain.TaskTrigger;
import cn.mulanbay.schedule.enums.CostTimeCalcType;
import cn.mulanbay.schedule.enums.TriggerStatus;

import java.util.Date;
import java.util.List;

/**
 * 调度持久层操作接口定义
 *
 * @author fenghong
 * @create 2017-10-19 21:26
 **/
public interface SchedulePersistentProcessor {

    /**
     * 更新调度（新的调度执行后）
     * @param taskTrigger
     */
    void updateTaskTriggerForNewJob(TaskTrigger taskTrigger);

    /**
     * 保存调度日志
     * @param taskLog
     */
    void saveTaskLog(TaskLog taskLog);

    /**
     * 获取调度日志
     * @param logId
     * @return
     */
    TaskLog getTaskLog(Long logId);

    /**
     * 查询调度
     * @param triggerId
     * @return
     */
    TaskTrigger getTaskTrigger(Long triggerId);

    /**
     * 查询调度
     * @param taskClass
     * @return
     */
    public TaskTrigger getTaskTrigger(String taskClass);

    /**
     * 更新调度日志
     * @param taskLog
     */
    void updateTaskLog(TaskLog taskLog);

    /**
     * 重做调度后更新调度信息
     * @param taskTrigger
     */
    void updateTaskTriggerForRedoJob(TaskTrigger taskTrigger);

    /**
     * 更新调度状态
     * @param triggerId
     * @param status
     */
    void updateTaskTriggerStatus(Long triggerId, TriggerStatus status);

    /**
     * 获取当前有效的调度列表
     * @param deployId
     * @param distriable 必须要有锁机制
     * @return
     */
    List<TaskTrigger> getActiveTaskTrigger(String deployId, boolean distriable);

    /**
     * 判断调度日志是否存在了，针对周期为日类型以上的
     * @param taskTriggerId
     * @param bussDate
     * @return
     */
    boolean isTaskLogExit(Long taskTriggerId, Date bussDate);

    /**
     * 判断调度日志是否存在了
     * @param scheduleIdentityId
     * @return
     */
    boolean isTaskLogExit(String scheduleIdentityId);

    /**
     * 获取自动重做的任务列表
     * @param deployId
     * @param distriable
     * @param startDate 最小开始时间
     * @param endDate 最大开始时间
     * @return
     */
    List<TaskLog> getAutoRedoTaskLogs(String deployId, boolean distriable,Date startDate,Date endDate);

    /**
     * 更新调度服务器
     * @param taskServer
     */
    void updateTaskServer(TaskServer taskServer);

    /**
     * 获取调度服务器
     * @param deployId
     * @return
     */
    TaskServer getTaskServer(String deployId);

    /**
     * 获取耗时
     * @param taskTriggerId
     * @param days 多少天内
     * @return
     */
    Long getCostTime(Long taskTriggerId, int days, CostTimeCalcType type);
}
