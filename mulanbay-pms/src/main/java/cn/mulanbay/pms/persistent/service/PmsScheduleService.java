package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.web.bean.req.schedule.taskTrigger.TaskTriggerCategorySH;
import cn.mulanbay.schedule.domain.TaskLog;
import cn.mulanbay.schedule.domain.TaskServer;
import cn.mulanbay.schedule.domain.TaskTrigger;
import cn.mulanbay.schedule.enums.TriggerStatus;
import cn.mulanbay.schedule.impl.HibernatePersistentProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 调度
 * 需要全部复写父类的方法，否则会报：Could not obtain transaction-synchronized Session for current thread
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
@Service
@Transactional
public class PmsScheduleService extends HibernatePersistentProcessor {
    @Override
    public void updateTaskTriggerForNewJob(TaskTrigger taskTrigger) {
        super.updateTaskTriggerForNewJob(taskTrigger);
    }

    @Override
    public void saveTaskLog(TaskLog taskLog) {
        super.saveTaskLog(taskLog);
    }

    @Override
    public TaskLog getTaskLog(Long logId) {
        return super.getTaskLog(logId);
    }

    @Override
    public TaskTrigger getTaskTrigger(Long triggerId) {
        return super.getTaskTrigger(triggerId);
    }

    @Override
    public TaskTrigger getTaskTrigger(String taskClass) {
        return super.getTaskTrigger(taskClass);
    }

    @Override
    public void updateTaskLog(TaskLog taskLog) {
        super.updateTaskLog(taskLog);
    }

    @Override
    public void updateTaskTriggerForRedoJob(TaskTrigger taskTrigger) {
        super.updateTaskTriggerForRedoJob(taskTrigger);
    }

    @Override
    public void updateTaskTriggerStatus(Long triggerId, TriggerStatus status) {
        super.updateTaskTriggerStatus(triggerId, status);
    }

    @Override
    public List<TaskTrigger> getActiveTaskTrigger(String deployId, boolean distriable) {
        return super.getActiveTaskTrigger(deployId, distriable);
    }

    @Override
    public boolean isTaskLogExit(Long triggerId, Date bussDate) {
        return super.isTaskLogExit(triggerId, bussDate);
    }

    @Override
    public boolean isTaskLogExit(String scheduleIdentityId) {
        return super.isTaskLogExit(scheduleIdentityId);
    }

    @Override
    public List<TaskLog> getAutoRedoTaskLogs(String deployId, boolean distriable, Date startDate, Date endDate) {
        return super.getAutoRedoTaskLogs(deployId, distriable, startDate, endDate);
    }

    @Override
    public void updateTaskServer(TaskServer taskServer) {
        super.updateTaskServer(taskServer);
    }

    @Override
    public TaskServer getTaskServer(String deployId) {
        return super.getTaskServer(deployId);
    }

    /**
     * 更新调度参数
     *
     * @param triggerId
     * @param triggerParas
     */
    public void updateTaskTriggerPara(Long triggerId, String triggerParas) {
        try {
            TaskTrigger tt = this.getTaskTrigger(triggerId);
            tt.setTriggerParas(triggerParas);
            tt.setModifyTime(new Date());
            this.updateEntity(tt);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "更新调度参数异常", e);
        }
    }

    /**
     * 获取最近一次的调度日志
     *
     * @param taskTriggerId
     */
    public TaskLog getLastTaskLog(Long taskTriggerId) {
        try {
            String hql = "from TaskLog where taskTrigger.id=?1 order by startTime desc";
            return this.getEntity(hql,TaskLog.class, taskTriggerId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取最近一次的调度日志异常", e);
        }
    }

    /**
     * 获取分类列表，统计聚合
     *
     * @return
     */
    public List<String> getTaskTriggerCategoryList(TaskTriggerCategorySH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            StringBuffer sb = new StringBuffer();
            sb.append("select distinct ");
            sb.append(sf.getGroupField());
            sb.append(" from TaskTrigger ");
            sb.append(pr.getParameterString());
            sb.append(" order by ");
            sb.append(sf.getGroupField());
            return this.getEntityListHI(sb.toString(), NO_PAGE, NO_PAGE_SIZE,String.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取分类列表异常", e);
        }
    }

    /**
     * 最近的调度(24小时内)
     *
     * @return
     */
    public List<TaskTrigger> getRecentSchedules() {
        try {
            Date now = new Date();
            Date end = new Date(now.getTime()+24*3600*1000L);
            String hql ="from TaskTrigger where nextExecuteTime>=?1 and nextExecuteTime<=?2 and triggerStatus=?3 and triggerType in (4,5,6,7,8) order by nextExecuteTime ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,TaskTrigger.class,now,end,TriggerStatus.ENABLE);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "最近的调度异常", e);
        }
    }

    /**
     * 获取需要检查的日志列表
     *
     * @return
     */
    public List<TaskLog> getCheckLogList(Date minDate) {
        try {
            String hql ="from TaskLog where bussDate<=?1 order by taskTrigger.triggerId,bussDate ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,TaskLog.class,minDate);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取需要检查的日志列表异常", e);
        }
    }
}
