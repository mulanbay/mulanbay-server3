package cn.mulanbay.schedule.job;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.IPAddressUtil;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.schedule.*;
import cn.mulanbay.schedule.domain.TaskLog;
import cn.mulanbay.schedule.domain.TaskTrigger;
import cn.mulanbay.schedule.enums.*;
import cn.mulanbay.schedule.lock.LockStatus;
import cn.mulanbay.schedule.lock.ScheduleLocker;
import cn.mulanbay.schedule.para.TriggerExecTimePeriods;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 所有调度任务的基类，定义调度的流程
 * 同时支持新建的调度和重做的调度
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public abstract class AbstractBaseJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(AbstractBaseJob.class);

	protected final static ParaCheckResult DEFAULT_SUCCESS_PARA_CHECK =new ParaCheckResult();

	/**
	 * 调度资源(持久层处理器、分布式支持等)
	 */
	protected QuartzSource quartzSource;

	/**
	 * 调度触发器（无论新建还是重做，都必须有该对象）
	 */
	private TaskTrigger taskTrigger;

	/**
	 * 是否重做调度
	 */
	private boolean isRedo = false;

	/**
	 * 只有重做的情况下才会有该对象
	 */
	private TaskLog taskLog;

	/**
	 * 该调度是否正在执行，解决调度的执行时长大于调度周期值
	 * 比如：该调度的执行周期值为5秒，而调度的逻辑功能执行了5秒，导致本次调度还没执行完就开始下一次的调度周期了
	 */
	private boolean isDoing = false;

	/**
	 * 是否检查过参数
	 */
	private boolean isParaChecked = false;

	/**
	 * 是否要更新调度（重做情况下有效）
	 * 重做分为两种：A 已经执行过，但是执行失败了
	 *             B 该运营日没有执行(程序挂了等原因)，需要手动进行执行
	 */
	private boolean isUpdateTrigger;

	private JobExecutionContext jobExecutionContext;

	/**
	 * 调度的触发时间
	 */
	private Date scheduledFireTime;

	/**
	 * 额外参数，针对手动执行的
	 */
	private Object extraPara;

	public TaskTrigger getTaskTrigger() {
		if (isRedo) {
			return taskLog.getTaskTrigger();
		}
		return taskTrigger;
	}

	public Object getExtraPara() {
		return extraPara;
	}

	public void setExtraPara(Object extraPara) {
		this.extraPara = extraPara;
	}

	public void setScheduledFireTime(Date scheduledFireTime) {
		this.scheduledFireTime = scheduledFireTime;
	}

	public void setQuartzSource(QuartzSource quartzSource) {
		this.quartzSource = quartzSource;
	}

	public TaskLog getTaskLog() {
		return taskLog;
	}

	public void setTaskLog(TaskLog taskLog) {
		this.taskLog = taskLog;
	}

	public boolean isRedo() {
		return isRedo;
	}

	public void setRedo(boolean isRedo) {
		this.isRedo = isRedo;
	}

	public void setUpdateTrigger(boolean isUpdateTrigger) {
		this.isUpdateTrigger = isUpdateTrigger;
	}

	public AbstractBaseJob() {
		super();
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		if(arg0!=null){
			scheduledFireTime=arg0.getScheduledFireTime();
		}
		if (isRedo) {
			redoJob(arg0);
		} else {
			newJob(arg0);
		}
	}

	/**
	 * 新的任务调度
	 *
	 * @param arg0
	 * @throws JobExecutionException
	 */
	private void newJob(JobExecutionContext arg0) throws JobExecutionException {
		LockStatus lockStatus = null;
		try {
			jobExecutionContext = arg0;
			// 获取调度器
			taskTrigger = (TaskTrigger) arg0.getMergedJobDataMap().get(
					QuartzConstant.SCHEDULE_TASK_TRIGGER);
			quartzSource = (QuartzSource) arg0.getMergedJobDataMap().get(
					QuartzConstant.SCHEDULE_QUARTZ_SOURCE);
			boolean checkUnique = this.checkScheduleExecUnique();
			if(!checkUnique){
				logger.error("调度任务id="+taskTrigger.getTriggerId()+"重复执行");
				return;
			}
			lockStatus = this.lock();
			if(lockStatus!=LockStatus.SUCCESS){
				return;
			}
			logger.debug("开始执行[" + taskTrigger.getTriggerName() + "]");
			taskLog = new TaskLog();
			// TODO线程同步
			if (isDoing) {
				logger.debug("任务[" + taskTrigger.getTriggerName()
						+ "]已经在执行");
				taskLog.setLogComment("任务[" + taskTrigger.getTriggerName()
						+ "]已经在执行，跳过本次执行。");
				taskTrigger.setLastExecuteResult(JobResult.DUPLICATE);
				return;
			}
			isDoing = true;
			Date startTime = new Date();
			// 新的
			logger.debug("新的JOB[" + taskTrigger.getTriggerName() + "]");
			taskLog.setStartTime(startTime);
			taskLog.setTaskTrigger(taskTrigger);
			//taskTrigger.setLastExecuteTime(startTime);

			// 执行任务
			TaskResult ar = checkParaAndDoTask();

			taskLog.setExecuteResult(ar.getResult());
			taskLog.setSubTaskExecuteResults(ar.getSubResults());
			taskLog.setLogComment(appendComment(taskLog.getLogComment(),ar.getComment()));

			// TODO 需要重新加载taskTrigger
			taskTrigger.setLastExecuteResult(ar.getResult());
			logger.debug("[" + taskTrigger.getTriggerName() + "]执行结束");
		} catch (ApplicationException ae) {
			logger.error("[" + taskTrigger.getTriggerName() + "]执行异常", ae);
			taskTrigger.setLastExecuteResult(JobResult.FAIL);
			taskLog.setExecuteResult(JobResult.FAIL);
			taskLog.setLogComment(appendComment(taskLog.getLogComment(),
					"调度执行遇到ApplicationException。"+ae.getMessageDetail()));
		} catch (Exception e) {
			logger.error("[" + taskTrigger.getTriggerName() + "]执行异常", e);
			taskTrigger.setLastExecuteResult(JobResult.FAIL);
			taskLog.setExecuteResult(JobResult.FAIL);
			taskLog.setLogComment(appendComment(taskLog.getLogComment(),e.getMessage()));
		} finally {
			long costTime=0L;
			if(lockStatus ==LockStatus.SUCCESS){
				try {
					isDoing = false;
					Date endTime = new Date();
					costTime = endTime.getTime()-taskLog
							.getStartTime().getTime();
					SchedulePersistentProcessor processor = this.getPersistentProcessor();

					taskTrigger.setNextExecuteTime(arg0.getNextFireTime());
					taskTrigger.setTotalCount(taskTrigger.getTotalCount() + 1);
					if (taskTrigger.getLastExecuteResult() == JobResult.FAIL) {
						taskTrigger.setFailCount(taskTrigger.getFailCount() + 1);
					}
					taskTrigger.setLastExecuteTime(new Date());
					if(taskTrigger.getTriggerType()== TriggerType.NOW){
						//一次类型的设为无效
						taskTrigger.setTriggerStatus(TriggerStatus.DISABLE);
					}
					// 更新执行调度
					processor.updateTaskTriggerForNewJob(taskTrigger);
					//需要记录日志或者没运行成功的都需要记录
					if(taskTrigger.getLoggable()|| JobResult.FAIL==taskLog.getExecuteResult()){
						// 新的
						taskLog.setEndTime(endTime);
						taskLog.setBussDate(getBussDay());
						taskLog.setCostTime(costTime);
						taskLog.setRedoTimes((short) 0);
						// 保存执行日志
						taskLog.setIpAddress(getIpAddress());
						taskLog.setLogComment(appendComment(taskLog.getLogComment(),""));
						taskLog.setDeployId(quartzSource.getDeployId());
						String scheduleIdentityId = this.generateScheduleId();
						taskLog.setScheduleIdentityId(scheduleIdentityId);
						processor.saveTaskLog(taskLog);
					}
					notifyMessage();
				} catch (Exception e) {
					logger.error("保存执行记录异常", e);
				}
				// 解锁
				unlock(costTime);
			}
		}
	}

	/**
	 * 重做任务调度
	 *
	 * @param arg0
	 * @throws JobExecutionException
	 */
	private void redoJob(JobExecutionContext arg0) throws JobExecutionException {
		LockStatus lockStatus = null;
		try {
			jobExecutionContext = arg0;
			taskTrigger = taskLog.getTaskTrigger();
			lockStatus = this.lock();
			if(lockStatus!=LockStatus.SUCCESS){
				return;
			}
			logger.debug("开始重做任务[" + taskTrigger.getTriggerName() + "]");
			Date startTime = new Date();
			if (taskLog.getLogId() == null) {
				taskLog.setStartTime(startTime);
			} else {
				taskLog.setLastStartTime(startTime);
			}
			// 执行任务
			TaskResult ar = checkParaAndDoTask();

			taskLog.setExecuteResult(ar.getResult());
			taskLog.setSubTaskExecuteResults(ar.getSubResults());
			taskLog.setLogComment(ar.getComment());
			logger.debug("[" + taskLog.getTaskTrigger().getTriggerName()
					+ "]执行结束");
		} catch (ApplicationException ae) {
			logger.error("[" + taskTrigger.getTriggerName() + "]执行异常", ae);
			taskTrigger.setLastExecuteResult(JobResult.FAIL);
			taskLog.setExecuteResult(JobResult.FAIL);
			taskLog.setLogComment(appendComment(taskLog.getLogComment(),
					"调度执行遇到ApplicationException。"+ae.getMessageDetail()));
		} catch (Exception e) {
			logger.error("[" + taskLog.getTaskTrigger().getTriggerName()
					+ "]执行异常", e);
			taskLog.setExecuteResult(JobResult.FAIL);
			taskLog.setLogComment(appendComment(taskLog.getLogComment(),e.getMessage()));
		} finally {
			if(lockStatus ==LockStatus.SUCCESS){
				try {
					Date endTime = new Date();
					SchedulePersistentProcessor processor = this.getPersistentProcessor();
					taskLog.setIpAddress(getIpAddress());
					taskLog.setDeployId(quartzSource.getDeployId());
					if (taskLog.getLogId() == null) {
						String lc = taskLog.getLogComment() == null ? "" : taskLog
								.getLogComment();
						taskLog.setLogComment(lc + "[未执行，手动重做]");
						taskLog.setEndTime(endTime);
						taskLog.setRedoTimes((short) 0);
						taskLog.setCostTime((taskLog.getEndTime().getTime() - taskLog
								.getStartTime().getTime()));
						taskLog.setLogComment(appendComment(taskLog.getLogComment(),""));
						String scheduleIdentityId = this.generateScheduleId();
						taskLog.setScheduleIdentityId(scheduleIdentityId);
						processor.saveTaskLog(taskLog);
					} else {
						taskLog.setLastEndTime(endTime);
						short redoTime = (taskLog.getRedoTimes() == null ? 0
								: taskLog.getRedoTimes().shortValue());
						taskLog.setRedoTimes((short) (redoTime + 1));
						taskLog.setCostTime((taskLog.getLastEndTime().getTime() - taskLog
								.getLastStartTime().getTime()));
						taskLog.setLogComment(appendComment(taskLog.getLogComment(),""));
						processor.updateTaskLog(taskLog);
					}
					if (isUpdateTrigger) {
						taskTrigger.setTotalCount(taskTrigger.getTotalCount() + 1);
						if (taskLog.getExecuteResult() == JobResult.FAIL) {
							taskTrigger
									.setFailCount(taskTrigger.getFailCount() + 1);
						}
						taskTrigger
								.setLastExecuteResult(taskLog.getExecuteResult());
						taskTrigger.setLastExecuteTime(endTime);
						// 通知调度器更新
						//taskTrigger.setModifyTime(new Date());
						processor.updateTaskTriggerForRedoJob(taskTrigger);
					}
				} catch (Exception e) {
					logger.error("保存执行记录异常", e);
				}
				notifyMessage();
				unlock(taskLog.getCostTime());
			}
		}
	}

	/**
	 * 是否关闭
	 * @return
	 */
	protected boolean isShutDown(){
		try {
			if(jobExecutionContext==null){
				return false;
			}
			return jobExecutionContext.getScheduler().isShutdown();
		} catch (Exception e) {
			logger.error("获取调度是否关闭异常",e);
			return false;
		}
	}

	/**
	 * 生成唯一的调度编号
	 * @return
	 */
	private String generateScheduleId(){
		TriggerType triggerType = taskTrigger.getTriggerType();
		String dateFormat = null;
		Date date = null;
		switch (triggerType){
			case NOW,SECOND,CRON-> {
				dateFormat="yyyyMMddHHmmss";
				date = scheduledFireTime;
			}
			case MINUTE-> {
				dateFormat="yyyyMMddHHmm";
				date = scheduledFireTime;
			}
			case HOUR-> {
				dateFormat="yyyyMMddHH";
				date = scheduledFireTime;
			}
			case DAY,WEEK-> {
				dateFormat="yyyyMMdd";
				date = this.getBussDay();
			}
			case MONTH,SEASON-> {
				dateFormat="yyyyMM";
				date = this.getBussDay();
			}
			case YEAR-> {
				dateFormat="yyyy";
				date = this.getBussDay();
			}
		}
		if(date==null){
			date = new Date();
		}
		String dateTimeString = DateUtil.getFormatDate(date,dateFormat);
		return taskTrigger.getTriggerId()+"_"+dateTimeString;
	}

	/**
	 * 上锁，只针对分布式调度的有效
	 * @return
	 */
	private LockStatus lock(){
		if(!taskTrigger.getDistriable()){
			//不支持分布式的不需要上锁
			return LockStatus.SUCCESS;
		}
		ScheduleLocker scheduleLocker = quartzSource.getScheduleLocker();
		if(scheduleLocker==null){
			logger.debug("没有调度锁配置，无法进行锁");
			return LockStatus.SUCCESS;
		}else{
			String lockKey = getLockKey();
			LockStatus lockStatus= scheduleLocker.lock(lockKey,this.getTimeout());
			logger.debug("调度锁key="+lockKey+",上锁结果:"+lockStatus);
			return lockStatus;
		}

	}

	/**
	 * 解锁，只针对分布式调度的有效
	 * (1)这里其实会涉及到一个时钟同步问题，不同的主机时钟差别很大，那么时间很难控制。
	 * 支持分布式的任务如果其任务执行时间很快最好在同一台主机不同的应用上跑
	 * (2)另外一种identifyKey通过JobExecutionContext的ScheduleFireTime来确定唯一性(精确到秒)
	 * 可以解决时钟不同步的问题
	 * (3)目前实现方式：添加scheduleIdentityId检查，即使时钟不同步也无问题
	 * @param costTime 实际任务花费时间
	 * @return
	 */
	private LockStatus unlock(long costTime){
		if(!taskTrigger.getDistriable()){
			//不支持分布式的不需要上锁
			return LockStatus.SUCCESS;
		}
		ScheduleLocker scheduleLocker = quartzSource.getScheduleLocker();
		if(scheduleLocker==null){
			return LockStatus.SUCCESS;
		}else{
			String lockKey = getLockKey();
			LockStatus lockStatus =  scheduleLocker.unlock(lockKey);
			logger.debug("调度锁key="+lockKey+",解锁结果:"+lockStatus);
			return lockStatus;
		}

	}

	/**
	 * 每次调度任务执行的锁编号
	 * 新任务：每一个进程里面只允许一个
	 * 重做任务：可以并发运行
	 * @return
	 */
	private String getLockKey(){
		String key = generateLockKeyPrefix(taskTrigger.getGroupName(),taskTrigger.getTriggerId(),isRedo);
		if(isRedo){
			key +=":"+this.generateScheduleId();
		}
		return key;
	}

	/**
	 * 产生锁前缀
	 * @param groupName
	 * @param triggerId
	 * @param rd
	 * @return
	 */
	public static String generateLockKeyPrefix(String groupName,Long triggerId,boolean rd){
		String type= (rd ? "redo":"new");
		String key = "scheduleLock"+":"+groupName+":"+triggerId+":"+type;
		return key;
	}

	/**
	 * 获取超时时间
	 *
	 * @return
	 */
	private long getTimeout(){
		try {
			long t = taskTrigger.getTimeout();
			if(t<0){
				SchedulePersistentProcessor spp = quartzSource.getSchedulePersistentProcessor();
				CostTimeCalcType costTimeCalcType = quartzSource.getCostTimeCalcType();
				Long ct = spp.getCostTime(taskTrigger.getTriggerId(), quartzSource.getCostTimeDays(), costTimeCalcType);
				if(ct==null){
					logger.warn("未能获取到taskTrigger={}的花费时间计算结果，JOB超时时间采用系统默认:{}毫秒",taskTrigger.getTriggerId(),quartzSource.getDistriTaskMinCost());
					return quartzSource.getDistriTaskMinCost();
				}else{
					return (long) (ct * quartzSource.getCostTimeRate());
				}
			}else{
				return t;
			}
		} catch (Exception e) {
			logger.error("获取超时时间异常",e);
			return quartzSource.getDistriTaskMinCost();
		}
	}

	/**
	 * 消息通知
	 * 针对执行失败操作
	 */
	private void notifyMessage(){
		if(quartzSource.getNotifiableProcessor()!=null){
			JobResult jer = taskLog.getExecuteResult();
			if(jer== JobResult.FAIL||jer== JobResult.DUPLICATE){
				Long taskTriggerId = taskTrigger.getTriggerId();
				String title="调度器["+taskTrigger.getTriggerName()+"]调度执行异常";
				String content="调度器["+taskTrigger.getTriggerName()+"]调度在"+
						DateUtil.getFormatDate(new Date(),DateUtil.Format24Datetime)+"执行异常,执行结果:"+jer.getName()+"，错误信息:"+
						taskLog.getLogComment();
				this.notifyMessage(taskTriggerId, ScheduleCode.TRIGGER_EXEC_ERROR,title,content);
			}
		}
	}

	private void notifyMessage(Long taskTriggerId,int code, String title, String message){
		if(quartzSource.getNotifiableProcessor()!=null){
			quartzSource.getNotifiableProcessor().notifyMessage(taskTriggerId,code,title,message);
		}else {
			logger.warn("系统没有配置提醒处理器，无法发送消息");
		}
	}
	/**
	 * 业务操作，具体的业务逻辑
	 * 由子类来实现
	 * @return
	 */
	public abstract TaskResult doTask();

	/**
	 * 检查调度执行是否唯一
	 * @return
	 */
	private boolean checkScheduleExecUnique(){
		if(taskTrigger.getCheckUnique()){
			if(taskTrigger.getUniqueType()== TaskUniqueType.IDENTITY){
				String scheduleIdentityId = this.generateScheduleId();
				boolean isExit = this.getPersistentProcessor().isTaskLogExit(scheduleIdentityId);
				if(isExit){
					return false;
				}
			}else{
				boolean isExit = this.getPersistentProcessor().isTaskLogExit(taskTrigger.getTriggerId(),this.getBussDay());
				if(isExit){
					return false;
				}
			}
		}
		return true;
	}


	private TaskResult checkParaAndDoTask() {
		if(!checkNeedExec()){
			return new TaskResult(JobResult.SKIP);
		}
		if (isParaChecked) {
			// 已经检查过
			return doTask();
		} else {
			logger.debug("检查[" + getTaskTrigger().getTriggerName() + "]的参数:"
					+ getTaskTrigger().getTriggerParas());
			ParaCheckResult pcr = checkTriggerPara();
			if (pcr.getErrorCode() != ErrorCode.SUCCESS) {
				TaskResult tr = new TaskResult();
				tr.setResult(JobResult.FAIL);
				tr.setComment("检查参数异常，错误代码：" + pcr.getErrorCode() + ","
						+ pcr.getMessage());
				return tr;
			} else {
				isParaChecked = true;
				return doTask();
			}
		}
	}

	/**
	 * 获取业务统计的日期,只精确到天
	 *
	 * @return
	 */
	public Date getBussDay() {
		if (isRedo) {
			return taskLog.getBussDate();
		} else {
			int offsetDays = taskTrigger.getOffsetDays().intValue();
			//需要根据调度时间来计算，因为有可能很多年没有执行，quartz会从最近的一次开始调度
			Date d = DateUtil.getDate(offsetDays,scheduledFireTime);
			return d;
		}
	}

	private String getIpAddress() {
		return IPAddressUtil.getLocalIpAddress();
	}

	private SchedulePersistentProcessor getPersistentProcessor() {
		return quartzSource.getSchedulePersistentProcessor();
	}

	/**
	 * 检查是否需要执行
	 * 参数格式：01:00-02:20,03:30-17:40
	 * @return
	 */
	private boolean checkNeedExec(){
		try {
			String execTimePeriods = taskTrigger.getExecTimePeriods();
			if(StringUtil.isEmpty(execTimePeriods)){
				return true;
			}else{
				TriggerExecTimePeriods etp = (TriggerExecTimePeriods) JsonUtil.jsonToBean(execTimePeriods,TriggerExecTimePeriods.class);
				if(etp==null){
					return true;
				}
				Date now = new Date();
				//星期判断
				boolean we = checkWeekExec(now,etp.getWeeks());
				//时间段判断
				boolean te = checkTimeExec(now,etp.getTimes());
				if(we&&te){
					return true;
				}else{
					logger.debug("ID["+taskTrigger.getTriggerId()+"],name["+taskTrigger.getTriggerName()+"]的调度在当前时间配置为不执行,星期判断:"+we+",时间段判断:"+te);
					return false;
				}
			}
		} catch (Exception e) {
			logger.error("检查调度是否需要执行异常",e);
			this.notifyMessage(taskTrigger.getTriggerId(), ScheduleCode.TRIGGER_EXEC_PARA_CHECK_ERROR,
					"检查调度是否需要执行异常",e.getMessage());
			return false;
		}
	}

	/**
	 * 检查星期的执行判断
	 * @param now
	 * @param conf
	 * @return
	 */
	private boolean checkWeekExec(Date now,int[] conf){
		if(conf!=null&&conf.length>0){
			int weekIndex = DateUtil.getDayIndexInWeek(now);
			for(int w : conf){
				if(weekIndex==w){
					return true;
				}
			}
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 检查时间段的执行判断
	 * @param now
	 * @param conf
	 * @return
	 */
	protected boolean checkTimeExec(Date now,String conf){
		if(StringUtil.isEmpty(conf)){
			return true;
		}else{
			String today = DateUtil.getToday();
			for(String s : conf.split(",")){
				String[] hms = s.split("-");
				Date begin = DateUtil.getDate(today+" "+hms[0]+":00",DateUtil.Format24Datetime);
				Date end = DateUtil.getDate(today+" "+hms[1]+":00",DateUtil.Format24Datetime);
				if(now.after(begin)&&now.before(end)){
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * 设置备注
	 *
	 * @param oc 原来的备注
	 * @param as 需要添加的备注
	 * @return
	 */
	private String appendComment(String oc,String as) {
		String s = (oc ==null ? "":oc);
		s+="。"+as;
		if (s == null || s.isEmpty()) {
			return s;
		} else {
			if (s.length() > 255) {
				// 避免过长
				s = s.substring(0, 255);
			}
			return s;
		}
	}

	/**
	 * 调度是否正在被执行
	 * @return
	 */
	public boolean isJobDoing(){
		return isDoing;
	}

	/**
	 * 检查调度的参数
	 *
	 * @return
	 */
	public abstract ParaCheckResult checkTriggerPara();

	/**
	 * 调度参数（json格式数据）对应的实体类类，如果没有则为空
	 * @return
	 */
	public abstract Class getParaDefine();

	/**
	 * 获取调度参数的实体类
	 * @return
	 */
	public <T> T getTriggerParaBean(){
		try {
			Class c = this.getParaDefine();
			if(c==null){
				return null;
			}else{
				String triggerPara = getTaskTrigger().getTriggerParas();
				if(StringUtil.isEmpty(triggerPara)){
					return null;
				}else{
					return (T) JsonUtil.jsonToBean(triggerPara,c);
				}
			}
		} catch (Exception e) {
			logger.error("获取调度参数异常",e);
			throw new ApplicationException(ScheduleCode.JOB_PARA_PARSE_ERROR,
					"获取调度参数异常:"+e.getMessage());
		}
	}
}
