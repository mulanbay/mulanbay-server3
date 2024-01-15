package cn.mulanbay.common.thread;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 线程基类
 * 自定义的线程继承此线程（主要针对周期性执行）
 * 统一交由ThreadManager处理
 * 在系统关闭时，由ThreadManager回收线程资源
 *
 * @see cn.mulanbay.web.servlet.SpringServlet
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class EnhanceThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(EnhanceThread.class);

	// 线程名称
	protected String threadName;

	protected boolean stop = false;

	// 如果是周期性线程，设置是先sleep再做业务还是先业务再sleep
	protected boolean beforeSleep = false;

	protected long interval;

	protected Date createdTime;

	protected Date lastExecuteTime;

	// 总执行次数
	protected long totalCount;

	// 总执行失败次数
	protected long failCount;

	public EnhanceThread(String threadName) {
		super();
		this.threadName = threadName;
		createdTime = new Date();
		ThreadManager.getInstance().addThread(this);
	}

	public EnhanceThread() {
		super();
		this.threadName = "未知线程";
		createdTime = new Date();
		ThreadManager.getInstance().addThread(this);
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public void setStop(boolean isStop) {
		this.stop = isStop;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public long getInterval() {
		return interval;
	}

	public void setBeforeSleep(boolean isBeforeSleep) {
		this.beforeSleep = isBeforeSleep;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public boolean getStop() {
		return stop;
	}

	public Date getLastExecuteTime() {
		return lastExecuteTime;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public long getFailCount() {
		return failCount;
	}

	/**
	 * 持续时间
	 * @return
	 */
	public long getDuration() {
		return System.currentTimeMillis() - createdTime.getTime();
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				if (beforeSleep && interval > 0) {
					sleep(interval * 1000);
				}
				doThreadTask();
				if (!beforeSleep && interval > 0) {
					sleep(interval * 1000);
				}
			} catch (Exception e) {
				logger.error(threadName + "执行异常", e);
				failCount++;
			} finally {
				if (interval == 0) {
					stop = true;
				}
				totalCount++;
				lastExecuteTime = new Date();
			}
		}
		ThreadManager.getInstance().removeThread(this);
	}

	/**
	 * 强制执行
	 *
	 * @return
	 */
	public void forceDoTask() {
		if (interval == 0) {
			// 如果线程没有周期性，说明只能执行一次
			throw new ApplicationException(ErrorCode.THREAD_CAN_ONLY_DO_ONCE);
		} else {
			doTask();
		}
	}

	private void doThreadTask() {
		try {
			doTask();
		} catch (Exception e) {
			logger.error(threadName + "执行操作异常", e);
		}
	}

	/**
	 * 业务操作
	 */
	public void doTask() {

	}

	public void stopThread() {
		try {
			this.stop = true;
			this.interrupt();
		} catch (Exception e) {
			logger.error(threadName + "停止异常", e);
		} finally {
			ThreadManager.getInstance().removeThread(this);
		}
	}

	public List<ThreadBean> getThreadInfo(){
		return null;
	}
}
