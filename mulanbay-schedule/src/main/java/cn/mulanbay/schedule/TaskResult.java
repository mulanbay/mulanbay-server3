package cn.mulanbay.schedule;

import cn.mulanbay.schedule.enums.JobResult;

/**
 * 调度执行结果
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class TaskResult {

	/**
	 * 调度任务执行结果
	 */
	private JobResult result = JobResult.SKIP;

	/**
	 * 子任务结果，目前没有使用
	 */
	private String subResults;
	
	private String comment;

	public TaskResult() {
	}

	public TaskResult(JobResult executeResult) {
		this.result = executeResult;
	}

	public JobResult getResult() {
		return result;
	}

	public void setResult(JobResult result) {
		this.result = result;
	}

	public String getSubResults() {
		return subResults;
	}

	public void setSubResults(String subResults) {
		this.subResults = subResults;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
