package cn.mulanbay.schedule.thread;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.thread.EnhanceThread;
import cn.mulanbay.common.thread.ThreadBean;
import cn.mulanbay.common.util.IPAddressUtil;
import cn.mulanbay.schedule.QuartzSource;
import cn.mulanbay.schedule.SchedulePersistentProcessor;
import cn.mulanbay.schedule.domain.TaskServer;
import cn.mulanbay.schedule.handler.ScheduleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 调度监控线程（总入口）
 * 
 * @author fh
 * 
 */
public class QuartzMonitorThread extends EnhanceThread {

	private static final Logger logger = LoggerFactory.getLogger(QuartzMonitorThread.class);

	private boolean isCheck = true;

	public QuartzMonitorThread() {
		super("调度监控线程");
	}

	public QuartzMonitorThread(long interval) {
		super("调度监控线程");
		this.interval=interval;
	}

	@Override
	public void doTask() {
		if (isCheck) {
			ScheduleHandler scheduleHandler = this.getScheduleHandler();
			scheduleHandler.checkAndRefreshSchedule(false);
		} else {
			logger.debug("当前设置为不检查调度");
		}
		updateTaskServer();
	}

	/**
	 * 更新调度服务器信息
	 * 目前基于数据库实现，后期可以通过redis缓存实现模式
	 */
	private void updateTaskServer(){
		try {
			ScheduleHandler scheduleHandler = this.getScheduleHandler();
			QuartzSource quartzSource = scheduleHandler.getQuartzSource();
			SchedulePersistentProcessor persistentProcessor = quartzSource.getSchedulePersistentProcessor();
			TaskServer taskServer = persistentProcessor.getTaskServer(quartzSource.getDeployId());
			if(taskServer==null){
				taskServer = new TaskServer();
				taskServer.setStartTime(new Date());
			}
			taskServer.setIpAddress(IPAddressUtil.getLocalIpAddress());
			taskServer.setDistriable(quartzSource.getDistriable());
			taskServer.setCejc(scheduleHandler.getCurrentlyExecutingJobsCount());
			taskServer.setSjc(scheduleHandler.getScheduleJobsCount());
			taskServer.setDeployId(quartzSource.getDeployId());
			taskServer.setModifyTime(new Date());
			taskServer.setStatus(scheduleHandler.getScheduleStatus());
			taskServer.setShutdownTime(null);
			persistentProcessor.updateTaskServer(taskServer);
		} catch (Exception e) {
			logger.error("更新调度服务器信息异常",e);
		}
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public boolean isCheck() {
		return isCheck;
	}

	private ScheduleHandler getScheduleHandler(){
		return BeanFactoryUtil.getBean(ScheduleHandler.class);
	}

	@Override
	public void stopThread() {
		try {
			this.stop = true;
			this.interrupt();
		} catch (Exception e) {
			logger.error("调度检查线程停止异常", e);
		}
	}

	@Override
	public List<ThreadBean> getThreadInfo() {
		List<ThreadBean> list = new ArrayList<>();
		list.add(new ThreadBean("检查周期",interval+"秒"));
		list.add(new ThreadBean("是否检查",isCheck+""));
		return list;
	}
}
