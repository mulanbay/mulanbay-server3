package cn.mulanbay.schedule.domain;

import cn.mulanbay.schedule.QuartzConstant;
import cn.mulanbay.schedule.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 调度触发器
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
@Entity
@Table(name = "task_trigger")
@DynamicInsert
@DynamicUpdate
public class TaskTrigger implements java.io.Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 7821258290163948795L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "trigger_id", unique = true, nullable = false)
	private Long triggerId;

	/**
	 * 调度名称
	 */
	@Column(name = "trigger_name", nullable = false, length = 32)
	private String triggerName;

	/**
	 * 部署点，不同的调度可以部署在不同的服务器上
	 * distriable=true，该调度不支持分布式执行，那么该调度只能在该台服务器执行
	 * distriable=false，该调度可以在所有的服务器上执行，deployId没什么作用
	 */
	@Column(name = "deploy_id", nullable = false, length = 32)
	private String deployId;

	/**
	 * 具体的调度类,类的全路径
	 */
	@Column(name = "task_class", nullable = false, length = 100)
	private String taskClass;

	/**
	 * 是否支持分布式
	 * 参见deployId说明
	 */
	@Column(name = "distriable", nullable = false)
	private Boolean distriable;

	/**
	 * 调度重做类型
	 */
	@Column(name = "redo_type", nullable = false)
	private RedoType redoType;

	/**
	 * 重做最大的支持次数
	 */
	@Column(name = "allowed_redo_times")
	private Integer allowedRedoTimes;

	/**
	 * 超时时间,单位:毫秒
	 */
	@Column(name = "timeout")
	private Long timeout;

	/**
	 * 调度分组名称
	 * 英文名，内部分组使用
	 */
	@Column(name = "group_name")
	private String groupName;

	/**
	 * 调度周期类型
	 */
	@Column(name = "trigger_type")
	private TriggerType triggerType;

	/**
	 * 调度的频率，通常和triggerType一起使用
	 * 例如：triggerType=SECOND，triggerInterval=1表示每秒执行一次
	 */
	@Column(name = "trigger_interval")
	private Integer triggerInterval;

	/**
	 * 调度的参数，json格式，具体参考taskClass所对应的para的定义
	 */
	@Column(name = "trigger_paras")
	private String triggerParas;

	/**
	 * 当triggerType=WEEK时，该值为周的序号，值为1-7，代表周日到周六，中间以英文逗号分隔
	 * 例如:1,2代表周日及周一执行
	 * 当triggerType=CRON时，该值为cron表达式
	 */
	@Column(name = "cron_expression")
	private String cronExpression;

	/**
	 * 业务偏移量，单位是天
	 * 比如一个调度每天执行统计的昨天的数据，那么这里的值=-1
	 */
	@Column(name = "offset_days")
	private Integer offsetDays;

	/**
	 * 首次执行时间
	 */
	@JsonFormat(pattern = QuartzConstant.DATE_TIME_FORMAT)
	@Column(name = "first_execute_time")
	private Date firstExecuteTime;

	/**
	 * 下一次执行时间,为空则使用firstExecuteTime
	 */
	@JsonFormat(pattern = QuartzConstant.DATE_TIME_FORMAT)
	@Column(name = "next_execute_time")
	private Date nextExecuteTime;

	/**
	 * 调度状态,调度刷新时判断使用
	 */
	@Column(name = "trigger_status")
	private TriggerStatus triggerStatus;

	/**
	 * 最近一次调度执行结果
	 */
	@Column(name = "last_execute_result")
	private JobExecuteResult lastExecuteResult;

	/**
	 * 最近一次调度执行时间
	 */
	@JsonFormat(pattern = QuartzConstant.DATE_TIME_FORMAT)
	@Column(name = "last_execute_time")
	private Date lastExecuteTime;

	/**
	 * 总的执行次数
	 */
	@Column(name = "total_count")
	private Long totalCount;

	/**
	 * 总的执行失败次数
	 */
	@Column(name = "fail_count")
	private Long failCount;

	/**
	 * 调度执行是否检查唯一性，避免重复执行
	 * 避免多台调度服务器时钟不同步的问题
	 */
	@Column(name = "check_unique")
	private Boolean checkUnique;

	@Column(name = "unique_type")
	private TaskUniqueType uniqueType;

	/**
	 * 调度是否记录调度日志
	 */
	@Column(name = "loggable")
	private Boolean loggable;

	/**
	 * 调度在执行失败时是否需要发起通知
	 */
	@Column(name = "notifiable")
	private Boolean notifiable;

	/**
	 * 调度的执行时间段
	 * 空表示不判断
	 */
	@Column(name = "exec_time_periods")
	private String execTimePeriods;

	@JsonFormat(pattern = QuartzConstant.DATE_TIME_FORMAT)
	@Column(name = "created_time")
	private Date createdTime;

	/**
	 * 该字段表示调度配置有无修改过
	 * 调度刷新时判断使用
	 */
	@JsonFormat(pattern = QuartzConstant.DATE_TIME_FORMAT)
	@Column(name = "modify_time")
	//@Version
	private Date modifyTime;

	@Column(name = "comment")
	private String comment;

	/**
	 * 调度的版本号，乐观锁实现
	 */
	@Version
	private long version;

	// Constructors

	/** default constructor */
	public TaskTrigger() {
	}

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

	public JobExecuteResult getLastExecuteResult() {
		return lastExecuteResult;
	}

	public void setLastExecuteResult(JobExecuteResult lastExecuteResult) {
		this.lastExecuteResult = lastExecuteResult;
	}

	public Date getLastExecuteTime() {
		return lastExecuteTime;
	}

	public void setLastExecuteTime(Date lastExecuteTime) {
		this.lastExecuteTime = lastExecuteTime;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public Long getFailCount() {
		return failCount;
	}

	public void setFailCount(Long failCount) {
		this.failCount = failCount;
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

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}


	@Transient
	public String getLastExecuteResultName(){
		return lastExecuteResult == null ? null : lastExecuteResult.getName();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof TaskTrigger bean) {
			return bean.getTriggerId().equals(this.getTriggerId());
		}else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
