package cn.mulanbay.schedule.impl;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.schedule.ScheduleCode;
import cn.mulanbay.schedule.SchedulePersistentProcessor;
import cn.mulanbay.schedule.domain.TaskLog;
import cn.mulanbay.schedule.domain.TaskServer;
import cn.mulanbay.schedule.domain.TaskTrigger;
import cn.mulanbay.schedule.enums.JobExecuteResult;
import cn.mulanbay.schedule.enums.RedoType;
import cn.mulanbay.schedule.enums.TriggerStatus;

import java.util.Date;
import java.util.List;

/**
 * 调度持久层的hibernate实现
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class HibernatePersistentProcessor  extends BaseHibernateDao implements SchedulePersistentProcessor {

    /**
     * 更新新的调度执行后的调度
     *
     * @param taskTrigger
     */
    @Override
    public void updateTaskTriggerForNewJob(TaskTrigger taskTrigger) {
        try {
            TaskTrigger dbBean = this.selectTaskTrigger(taskTrigger.getTriggerId());
            dbBean.setTotalCount(taskTrigger.getTotalCount());
            dbBean.setFailCount(taskTrigger.getFailCount());
            dbBean.setLastExecuteResult(taskTrigger.getLastExecuteResult());
            dbBean.setLastExecuteTime(taskTrigger.getLastExecuteTime());
            dbBean.setNextExecuteTime(taskTrigger.getNextExecuteTime());
            this.updateEntity(dbBean);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,"更新新的调度执行后的调度失败！",e);
        }
    }

    /**
     * 保存调度日志
     * @param taskLog
     */
    @Override
    public void saveTaskLog(TaskLog taskLog) {
        try {
            this.saveEntity(taskLog);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,"保存调度日志失败！",e);
        }
    }

    /**
     * 获取调度日志
     * @param logId
     * @return
     */
    @Override
    public TaskLog selectTaskLog(Long logId) {
        try {
            return this.getEntityById(TaskLog.class,logId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,"获取调度日志失败！",e);
        }
    }

    /**
     * 获取调度器
     * @param triggerId
     * @return
     */
    @Override
    public TaskTrigger selectTaskTrigger(Long triggerId) {
        try {
            return (TaskTrigger) this.getEntityById(TaskTrigger.class,triggerId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,"获取调度器失败！",e);
        }
    }

    /**
     * 更新调度日志
     * @param taskLog
     */
    @Override
    public void updateTaskLog(TaskLog taskLog) {
        try {
            this.updateEntity(taskLog);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,"更新调度日志失败！",e);
        }
    }

    /**
     * 更新重做后的调度执行后的调度
     * @param taskTrigger
     */
    @Override
    public void updateTaskTriggerForRedoJob(TaskTrigger taskTrigger) {
        this.updateTaskTriggerForNewJob(taskTrigger);
    }

    /**
     * 更新调度状态
     * @param triggerId
     * @param status
     */
    @Override
    public void updateTaskTriggerStatus(Long triggerId, TriggerStatus status) {
        try {
            String hql="update TaskTrigger set status=?1 where triggerId=?2";
            this.updateEntities(hql,status,triggerId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,"更新调度状态失败！",e);

        }
    }

    /**
     * 获取有效的调度器
     * @param deployId
     * @param distriable
     * @return
     */
    @Override
    public List<TaskTrigger> getActiveTaskTrigger(String deployId, boolean distriable) {
        try {
            String hql="from TaskTrigger where triggerStatus=?1 ";
            if(distriable){
                hql+="and (deployId=?2 or distriable=true)";
            }else {
                hql+="and deployId=?2 ";
            }
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,TaskTrigger.class,TriggerStatus.ENABLE,deployId);
        } catch (BaseException e) {
            throw new PersistentException(ScheduleCode.TRIGGER_GET_ACTIVE_LIST_ERROR,"获取有效的调度器失败！",e);
        }
    }

    /**
     * 检查调度日志是否存在
     * @param triggerId
     * @param bussDate
     * @return
     */
    @Override
    public boolean isTaskLogExit(Long triggerId, Date bussDate) {
        try {
            String hql="select count(0) from TaskLog where taskTrigger.triggerId=?1 and bussDate=?2 ";
            long n = this.getCount(hql,triggerId,bussDate);
            return n>0 ? true : false;
        } catch (BaseException e) {
            throw new PersistentException(ScheduleCode.TRIGGER_LOG_CHECK_ERROR,"检查调度日志是否存在失败！",e);
        }
    }

    @Override
    public boolean isTaskLogExit(String scheduleIdentityId) {
        try {
            String hql="select count(0) from TaskLog where scheduleIdentityId=?1";
            long n = this.getCount(hql,scheduleIdentityId);
            return n>0 ? true : false;
        } catch (BaseException e) {
            throw new PersistentException(ScheduleCode.TRIGGER_LOG_CHECK_ERROR,"检查调度日志是否存在失败！",e);
        }
    }

    /**
     * 获取自动重做的调度任务
     * @param deployId 部署点
     * @param distriable 是否支持分布式
     * @param startDate 最小开始时间
     * @param endDate 最大开始时间
     * @return
     */
    @Override
    public List<TaskLog> getAutoRedoTaskLogs(String deployId, boolean distriable, Date startDate,Date endDate) {
        try {
            String hql = """
                    select tl from TaskLog tl,TaskTrigger tt where tl.taskTrigger.triggerId = tt.triggerId and tl.executeResult=?1 \n
                    and (tt.redoType=?2 or tt.redoType=?3) \n
                    and tl.startTime>=?4 and tl.startTime<=?5 \n
                    and tl.redoTimes < tt.allowedRedoTimes \n
                    """;
            if(distriable){
                hql+=" and (tt.deployId=?6 or tt.distriable=1) ";
            }else{
                hql+=" and tt.deployId=?6 ";
            }
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,TaskLog.class, JobExecuteResult.FAIL,
                    RedoType.AUTO_REDO,RedoType.ALL_REDO,startDate,endDate,deployId);
        } catch (BaseException e) {
            throw new PersistentException(ScheduleCode.TRIGGER_GET_AUTO_REDO_LOG_ERROR,"获取自动重做的调度任务失败！",e);

        }
    }

    /**
     * 更新获取保存
     * @param taskServer
     */
    @Override
    public void updateTaskServer(TaskServer taskServer) {
        try {
            this.mergeEntity(taskServer);
        } catch (BaseException e) {
            throw new PersistentException(ScheduleCode.UPDATE_TASK_SERVER_ERROR,"更新调度服务器信息异常！",e);
        }
    }

    @Override
    public TaskServer selectTaskServer(String deployId) {
        try {
            String hql="from TaskServer where deployId=?1 ";
            return this.getEntity(hql,TaskServer.class,deployId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,"获取调度器服务器信息ß失败！",e);
        }
    }
}
