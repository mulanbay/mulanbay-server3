package cn.mulanbay.schedule;

import java.io.Serializable;
import java.util.Date;

/**
 * 调度详情
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class ScheduleInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 部署点
	 */
	private String deployId;

	/**
	 * 是否定期检测
	 */
	private boolean check;

	/**
	 * 调度是否开启
	 */
	private boolean enabled;
	
	private long interval;

	private boolean distriable=false;

	private int scheduleJobsCount;
	
	private int currentlyExecutingJobsCount;

	private int threadPoolActiveCount;

	private long threadPoolCompletedTaskCount;

	private Date date;

	public String getDeployId() {
		return deployId;
	}

	public void setDeployId(String deployId) {
		this.deployId = deployId;
	}

	public boolean getCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public boolean getDistriable() {
		return distriable;
	}

	public void setDistriable(boolean distriable) {
		this.distriable = distriable;
	}

	public int getScheduleJobsCount() {
		return scheduleJobsCount;
	}

	public void setScheduleJobsCount(int scheduleJobsCount) {
		this.scheduleJobsCount = scheduleJobsCount;
	}

	public int getCurrentlyExecutingJobsCount() {
		return currentlyExecutingJobsCount;
	}

	public void setCurrentlyExecutingJobsCount(int currentlyExecutingJobsCount) {
		this.currentlyExecutingJobsCount = currentlyExecutingJobsCount;
	}

	public int getThreadPoolActiveCount() {
		return threadPoolActiveCount;
	}

	public void setThreadPoolActiveCount(int threadPoolActiveCount) {
		this.threadPoolActiveCount = threadPoolActiveCount;
	}

	public long getThreadPoolCompletedTaskCount() {
		return threadPoolCompletedTaskCount;
	}

	public void setThreadPoolCompletedTaskCount(long threadPoolCompletedTaskCount) {
		this.threadPoolCompletedTaskCount = threadPoolCompletedTaskCount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
