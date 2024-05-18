package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.dto.schedule.CheckLogDTO;
import cn.mulanbay.pms.persistent.dto.schedule.TaskTriggerStat;
import cn.mulanbay.pms.persistent.enums.TriggerGroupField;
import cn.mulanbay.pms.web.bean.req.schedule.taskTrigger.TaskTriggerCategorySH;
import cn.mulanbay.pms.web.bean.req.schedule.taskTrigger.TaskTriggerStatSH;
import cn.mulanbay.schedule.domain.TaskLog;
import cn.mulanbay.schedule.domain.TaskServer;
import cn.mulanbay.schedule.domain.TaskTrigger;
import cn.mulanbay.schedule.enums.*;
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

    @Override
    public Long getCostTime(Long taskTriggerId, int days, CostTimeCalcType type) {
        return super.getCostTime(taskTriggerId, days,type);
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
    public List<TaskTrigger> getRecentSchedules(Date start,Date end,int page,int pageSize,boolean period) {
        try {
            String hql ="from TaskTrigger where nextExecuteTime>=?1 and nextExecuteTime<=?2 and triggerStatus=?3 ";
            if(period){
                hql +="and triggerType in (4,5,6,7,8) ";
            }
            hql +="order by nextExecuteTime";

            return this.getEntityListHI(hql,page,pageSize,TaskTrigger.class,start,end,TriggerStatus.ENABLE);
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
    public List<CheckLogDTO> getCheckLogList(Date startDate, Date endDate) {
        try {
            String sql = """
                    select tl.log_id as logId,tl.task_trigger_id as triggerId,tl.buss_date as bussDate,tt.trigger_type as triggerType
                    from task_log tl,task_trigger tt
                    where tl.task_trigger_id = tt.trigger_id
                    and buss_date>=?1 and buss_date<=?2
                    and tt.undo_check=1
                    """;
            return this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,CheckLogDTO.class,startDate,endDate);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取需要检查的日志列表异常", e);
        }
    }

    /**
     * 获取需要检查的触发器编号列表
     *
     * @return
     */
    public List<Long> getCheckTriggerIdList() {
        try {
            String sql = """
                    select trigger_id from task_trigger where undo_check =1
                    """;
            return this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,Long.class);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取需要检查的触发器编号列表异常", e);
        }
    }

    /**
     * 统计触发器
     *
     * @return
     */
    public List<TaskTriggerStat> getTaskTriggerStat(TaskTriggerStatSH sf) {
        try {
            String sql = """
                    select {group_field} as id,count(0) as totalCount from task_trigger group by {group_field}
                    """;
            TriggerGroupField groupField = sf.getGroupField();
            sql = sql.replace("{group_field}", groupField.getField());
            List<TaskTriggerStat> list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,TaskTriggerStat.class);
            for(TaskTriggerStat stat : list){
                String id = stat.getId().toString();
                switch (groupField){
                    case DEPLOY_ID,GROUP_NAME -> stat.setName(id);
                    case LOGGABLE,DISTRIABLE,NOTIFIABLE -> {
                        stat.setName(id.equals("1")? "是":"否");
                    }
                    case REDO_TYPE -> {
                        RedoType type = RedoType.getType(Integer.parseInt(id));
                        stat.setName(type.getName());
                    }
                    case TRIGGER_TYPE -> {
                        TriggerType type = TriggerType.getType(Integer.parseInt(id));
                        stat.setName(type.getName());
                    }
                    case UNIQUE_TYPE -> {
                        TaskUniqueType type = TaskUniqueType.getType(Integer.parseInt(id));
                        stat.setName(type.getName());
                    }
                    case LAST_EXECUTE_RESULT->{
                        JobResult type = JobResult.getType(Integer.parseInt(id));
                        stat.setName(type==null ? "无": type.getName());
                    }
                }
            }
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "统计触发器异常", e);
        }
    }

}
