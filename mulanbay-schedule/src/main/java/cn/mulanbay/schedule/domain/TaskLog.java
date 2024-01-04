package cn.mulanbay.schedule.domain;

import cn.mulanbay.schedule.enums.JobExecuteResult;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 调度日志
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
@Entity
@Table(name = "task_log")
@DynamicInsert
@DynamicUpdate
public class TaskLog implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 2098844185080537512L;

	/**
	 * 日志ID
	 */
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	/**
	 * 外键：调度触发器
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "task_trigger_id")
	private TaskTrigger taskTrigger;

	/**
	 * 运营日
	 */
	@Column(name = "buss_date", nullable = false, length = 10)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date bussDate;

	/**
	 * 唯一性标志ID，避免重复执行
	 */
	@Column(name = "schedule_identity_id")
	private String scheduleIdentityId;

	/**
	 * 执行开始时间
	 */
	@Column(name = "start_time")
	private Date startTime;

	/**
	 * 执行结束时间
	 */
	@Column(name = "end_time")
	private Date endTime;

	/**
	 * 执行耗费时间
	 */
	@Column(name = "cost_time")
	private Long costTime;

	/**
	 * 任务执行结果
	 */
	@Column(name = "execute_result")
	private JobExecuteResult executeResult;

	/**
	 * 子任务执行结果
	 */
	@Column(name = "sub_task_execute_results")
	private String subTaskExecuteResults;

	/**
	 * 调度部署点
	 */
	@Column(name = "deploy_id")
	private String deployId;

	/**
	 * 服务器IP
	 */
	@Column(name = "ip_address")
	private String ipAddress;

	/**
	 * 重试次数
	 */
	@Column(name = "redo_times")
	private Short redoTimes;

	/**
	 * 最后一次重试时的开始执行时间
	 */
	@Column(name = "last_start_time")
	private Date lastStartTime;

	/**
	 * 最后一次重试时的结束执行时间
	 */
	@Column(name = "last_end_time")
	private Date lastEndTime;

	@Column(name = "log_comment")
	private String logComment;

	// Constructors

	/** default constructor */
	public TaskLog() {
	}

	/** minimal constructor */
	public TaskLog(Date bussDate, JobExecuteResult executeResult) {
		this.bussDate = bussDate;
		this.executeResult = executeResult;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TaskTrigger getTaskTrigger() {
		return taskTrigger;
	}

	public void setTaskTrigger(TaskTrigger taskTrigger) {
		this.taskTrigger = taskTrigger;
	}

	public Date getBussDate() {
		return bussDate;
	}

	public void setBussDate(Date bussDate) {
		this.bussDate = bussDate;
	}


	public String getScheduleIdentityId() {
		return scheduleIdentityId;
	}

	public void setScheduleIdentityId(String scheduleIdentityId) {
		this.scheduleIdentityId = scheduleIdentityId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Long getCostTime() {
		return costTime;
	}

	public void setCostTime(Long costTime) {
		this.costTime = costTime;
	}

	public JobExecuteResult getExecuteResult() {
		return executeResult;
	}

	public void setExecuteResult(JobExecuteResult executeResult) {
		this.executeResult = executeResult;
	}

	public String getSubTaskExecuteResults() {
		return subTaskExecuteResults;
	}

	public void setSubTaskExecuteResults(String subTaskExecuteResults) {
		this.subTaskExecuteResults = subTaskExecuteResults;
	}

	public String getDeployId() {
		return deployId;
	}

	public void setDeployId(String deployId) {
		this.deployId = deployId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Short getRedoTimes() {
		return redoTimes;
	}

	public void setRedoTimes(Short redoTimes) {
		this.redoTimes = redoTimes;
	}

	public Date getLastStartTime() {
		return lastStartTime;
	}

	public void setLastStartTime(Date lastStartTime) {
		this.lastStartTime = lastStartTime;
	}

	public Date getLastEndTime() {
		return lastEndTime;
	}

	public void setLastEndTime(Date lastEndTime) {
		this.lastEndTime = lastEndTime;
	}

	public String getLogComment() {
		return logComment;
	}

	public void setLogComment(String logComment) {
		this.logComment = logComment;
	}

	@Transient
	public String getExecuteResultName(){
		return executeResult==null ? null : executeResult.getName();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof TaskLog) {
			TaskLog log = (TaskLog) other;
			return log.getId().equals(this.getId());
		}else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}