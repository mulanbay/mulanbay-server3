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

	private String deployId;
	
	private boolean isCheck;
	
	private boolean isSchedule;
	
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

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public boolean isSchedule() {
		return isSchedule;
	}

	public void setSchedule(boolean isSchedule) {
		this.isSchedule = isSchedule;
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
