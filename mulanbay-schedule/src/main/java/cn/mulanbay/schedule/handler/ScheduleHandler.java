package cn.mulanbay.schedule.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.HandlerInfo;
import cn.mulanbay.business.handler.HandlerMethod;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.IPAddressUtil;
import cn.mulanbay.schedule.*;
import cn.mulanbay.schedule.domain.TaskLog;
import cn.mulanbay.schedule.domain.TaskServer;
import cn.mulanbay.schedule.domain.TaskTrigger;
import cn.mulanbay.schedule.enums.RedoType;
import cn.mulanbay.schedule.enums.TriggerStatus;
import cn.mulanbay.schedule.thread.QuartzMonitorThread;
import cn.mulanbay.schedule.thread.RedoThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 调度维护总入口
 * 如果自动发布的程序中需要关闭web容器（如tomcat），在启动前先要等待destroy方法中的时长，
      这样保证程序可以正常关闭、启动
 * 如果直接采用kill -9模式关闭程序，可能会导致正在执行的job异常。
 * 调度服务最好能独立出服务器，因为涉及到部署节点ScheduleHandler问题。
 * @author fenghong
 * @create 2017-10-19 21:43
 **/
public class ScheduleHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleHandler.class);

    /**
     * 调度触发器监控
     */
    private static QuartzMonitorThread quartzMonitorThread;

    /**
     * 调度服务
     */
    private static QuartzServer quartzServer;

    /**
     * 线程池(重做使用)
     */
    private static ThreadPoolExecutor scheduledThreadPool;

    /**
     * 系统是否已经开启调度功能，由配置文件决定，无法手动重新开启
     */
    @Value("${mulanbay.schedule.enable:false}")
    private boolean enableSchedule;

    /**
     * 调度资源
     */
    @Autowired
    private QuartzSource quartzSource;

    /**
     * 部署点（针对不支持分布式的任务）
     */
    @Value("${mulanbay.nodeId:mulanbay}")
    private String deployId;

    @Value("${mulanbay.schedule.threadPool.corePoolSize:20}")
    int corePoolSize;

    @Value("${mulanbay.schedule.threadPool.queueSize:1024}")
    int queueSize;

    @Value("${mulanbay.schedule.threadPool.maximumPoolSize:200}")
    int maximumPoolSize;

    /**
     * 调度触发器检查周期(秒)
     */
    @Value("${mulanbay.schedule.monitorInterval:60}")
    long monitorInterval;

    /**
     * 调度服务器关闭时停止等待时间
     */
    @Value("${mulanbay.schedule.shutDownWaitSeconds:5}")
    long shutDownWaitSeconds;

    public ScheduleHandler() {
        super("调度处理");
    }

    @Override
    public void init() {
        super.init();
        if(this.isEnableSchedule()){
            //判断分布式支持
            if(quartzSource.getDistriable()&&quartzSource.getScheduleLocker()==null){
                throw new ApplicationException(ScheduleCode.DISTRIBUTE_LOCK_NOT_FOUND);
            }
            quartzServer = new QuartzServer();
            quartzServer.setQuartzSource(quartzSource);
            logger.debug("初始化调度服务");
            if(monitorInterval>0){
                quartzMonitorThread = new QuartzMonitorThread(monitorInterval);
                quartzMonitorThread.start();
                logger.debug("启动调度监控服务");
            }
            //调度线程的线程池采用： 丢弃任务并抛出RejectedExecutionException异常。 (默认)
            ThreadFactory threadFactory = new CustomizableThreadFactory("scheduleHandler");
            scheduledThreadPool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,10L,
                    TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(queueSize),
                    threadFactory,new ThreadPoolExecutor.AbortPolicy());
            //添加启动信息
            updateTaskServerStart();
        }else {
            logger.debug("该应用设置为不启动调度服务");
        }
    }

    /**
     * 添加/更新当前调度服务器信息
     */
    private void updateTaskServerStart(){
        try {
            SchedulePersistentProcessor persistentProcessor = quartzSource.getSchedulePersistentProcessor();
            TaskServer taskServer = persistentProcessor.getTaskServer(quartzSource.getDeployId());
            if(taskServer==null){
                taskServer = new TaskServer();
            }
            taskServer.setStartTime(new Date());
            taskServer.setIpAddress(IPAddressUtil.getLocalIpAddress());
            taskServer.setDistriable(quartzSource.getDistriable());
            taskServer.setCejc(this.getCurrentlyExecutingJobsCount());
            taskServer.setSjc(this.getScheduleJobsCount());
            taskServer.setDeployId(quartzSource.getDeployId());
            taskServer.setModifyTime(new Date());
            taskServer.setStatus(this.getScheduleStatus());
            taskServer.setShutdownTime(null);
            persistentProcessor.updateTaskServer(taskServer);
        } catch (Exception e) {
            logger.error("更新调度服务器信息异常",e);
        }
    }

    /**
     * 更新当前调度服务器停止信息
     */
    private void updateTaskServerShutdown(){
        try {
            SchedulePersistentProcessor persistentProcessor = quartzSource.getSchedulePersistentProcessor();
            TaskServer taskServer = persistentProcessor.getTaskServer(quartzSource.getDeployId());
            taskServer.setCejc(0);
            taskServer.setSjc(0);
            taskServer.setDistriable(quartzSource.getDistriable());
            taskServer.setDeployId(quartzSource.getDeployId());
            taskServer.setModifyTime(new Date());
            taskServer.setStatus(false);
            taskServer.setShutdownTime(new Date());
            persistentProcessor.updateTaskServer(taskServer);
        } catch (Exception e) {
            logger.error("更新调度服务器信息停止异常",e);
        }
    }

    @Override
    public Boolean selfCheck() {
        return super.selfCheck();
    }

    @Override
    public void destroy() {
        if(this.isEnableSchedule()){
            int activeJobs = quartzServer.getScheduleJobsCount();
            logger.warn("目前活跃的调度任务数:"+activeJobs);
            quartzServer.shutdown(shutDownWaitForJobsToComplete());
            quartzMonitorThread.stopThread();
            scheduledThreadPool.shutdown();
            try {
                if(shutDownWaitSeconds>0&&activeJobs>0){
                    Thread.sleep(shutDownWaitSeconds*1000);
                }
            } catch (InterruptedException e) {
                logger.error("destroy error:"+e.getMessage());
            }
            updateTaskServerShutdown();
        }
        super.destroy();
    }

    /**
     * 设置调度检查
     * @param check
     */
    public void setMonitorCheck(boolean check){
        quartzMonitorThread.setCheck(check);
    }

    public void setQuartzSource(QuartzSource quartzSource) {
        this.quartzSource = quartzSource;
    }

    public QuartzSource getQuartzSource() {
        return quartzSource;
    }

    /**
     * 根据 Job 调度日志来自动重做
     * @param logId
     * @param sync 是否同步
     */
    public void manualRedo(long logId,boolean sync) {
        TaskLog taskLog=quartzSource.getSchedulePersistentProcessor().getTaskLog(logId);
        if(taskLog.getTaskTrigger().getRedoType()== RedoType.CANNOT){
            throw new ApplicationException(ScheduleCode.TRIGGER_CANNOT_REDO);
        }
        startRedoJob(taskLog,sync,null);
    }

    /**
     * 手动执行一个配置的任务
     * @param triggerId 调度ID
     * @param bussDay 运营日
     * @param isSync 是否同步执行
     * @param extraPara 额外参数
     */
    public void manualStart(long triggerId, Date bussDay, boolean isSync, Object extraPara, String remark) {
        TaskTrigger taskTrigger = quartzSource.getSchedulePersistentProcessor().getTaskTrigger(triggerId);
        if(taskTrigger.getCheckUnique()){
            boolean b =quartzSource.getSchedulePersistentProcessor().isTaskLogExit(triggerId,bussDay);
            if(b){
                throw new ApplicationException(ScheduleCode.SCHEDULE_ALREADY_EXECED);
            }
        }
        TaskLog taskLog = new TaskLog();
        taskLog.setBussDate(bussDay);
        taskLog.setTaskTrigger(taskTrigger);
        taskLog.setLogComment(remark);
        startRedoJob(taskLog,isSync,extraPara);
    }

    /**
     * 开启重做任务
     * todo 多用户并发操作下会导致被重复执行
     * 解决方式：1.该方法增加锁机制，2. AbstractBaseJob中对于redo类型增加锁机制（第二种更好些）
     * @param taskLog 重做的调度日志
     * @param sync  是否同步执行
     * @param extraPara 额外参数
     */
    private void startRedoJob(TaskLog taskLog,boolean sync,Object extraPara){
        //如果更新TaskTrigger会有问题，因为最新的TaskTrigger数据在正常调度里面，这样会导致正常的调度更新TaskTrigger时异常
        RedoThread redoThread = new RedoThread(taskLog,false);
        redoThread.setExtraPara(extraPara);
        redoThread.setQuartzSource(quartzSource);
        if(!sync){
            scheduledThreadPool.execute(redoThread);
            logger.debug("启动一个调度日志重做线程任务");
        }else{
            redoThread.run();
            logger.debug("执行一个调度日志重做线程任务");
        }
    }

    /**
     * 关闭时是否等待任务完成
     * 默认是需要等待
     * @return
     */
    public boolean shutDownWaitForJobsToComplete(){
        return true;
    }

    public boolean isEnableSchedule() {
        return enableSchedule;
    }

    public void setEnableSchedule(boolean enableSchedule) {
        this.enableSchedule = enableSchedule;
    }

    /**
     * 当前正在运行的job数
     * @return
     */
    public int getCurrentlyExecutingJobsCount(){
        return quartzServer.getCurrentlyExecutingJobsCount();
    }

    /**
     * 返回正在调度执行的job
     *
     * @return
     */
    public List<TaskTrigger> getCurrentlyExecutingJobs() {
        return quartzServer.getCurrentlyExecutingJobs();
    }

    /**
     * 触发器是否正在被调度执行
     * @param triggerId
     * @return
     */
    public boolean isExecuting(Long triggerId) {
        return quartzServer.isExecuting(triggerId);
    }

    public int getScheduleJobsCount(){
        return quartzServer.getScheduleJobsCount();
    }

    /**
     * 是否已经被调度
     *
     * @param triggerId
     * @return
     */
    public boolean isTaskTriggerExecuting(long triggerId) {
        return quartzServer.isTaskTriggerExecuting(triggerId);
    }

    /**
     * 获取触发器被添加的时间
     *
     * @param triggerId
     * @return
     */
    public Date getAddTime(Long triggerId, String groupName) {
        return quartzServer.getAddTime(triggerId,groupName);
    }

    /**
     * 获取调度系统里的触发器信息
     *
     * @param triggerId
     * @return
     */
    public TaskTrigger getScheduledTaskTrigger(Long triggerId,String groupName) {
        return quartzServer.getScheduledTaskTrigger(triggerId,groupName);
    }

    /**
     * 设置调度状态
     * 只有enableSchedule=true情况，才可以设置调度状态
     * 这里的调度状态只是表明是否把job加入到调度队列中，
     * 无法ScheduleStatus=true或者false，调度都是在运行中
     * @param b
     */
    public void setScheduleStatus(boolean b){
        quartzServer.setScheduleStatus(b);
    }

    /**
     * 获取调度状态
     * @return
     */
    public boolean getScheduleStatus(){
        return quartzServer.getScheduleStatus();
    }

    /**
     * 获取调度信息列表
     * @return
     */
    public ScheduleInfo getScheduleInfo() {
        ScheduleInfo si = new ScheduleInfo();
        si.setDeployId(deployId);
        si.setCheck(monitorInterval>0);
        si.setInterval(monitorInterval);
        si.setSchedule(enableSchedule);
        if(isEnableSchedule()){
            si.setScheduleJobsCount(quartzServer.getScheduleJobsCount());
            si.setCurrentlyExecutingJobsCount(quartzServer
                    .getCurrentlyExecutingJobsCount());
            si.setThreadPoolActiveCount(scheduledThreadPool.getActiveCount());
            si.setThreadPoolCompletedTaskCount(scheduledThreadPool.getCompletedTaskCount());
        }
        si.setDistriable(quartzSource.getDistriable());
        return si;
    }

    /**
     * 刷新调度
     * @param isForce
     */
    @HandlerMethod(desc = "刷新调度")
    public boolean checkAndRefreshSchedule(boolean isForce) {
        try {
            synchronized (quartzServer) {
                quartzServer.refreshSchedule(isForce);
            }
            return true;
        } catch (Exception e) {
            logger.error("检查调度异常",e);
            return false;
        }

    }

    /**
     * 刷新调度
     * @param tt
     */
    public boolean refreshTask(TaskTrigger tt) {
        return quartzServer.refreshTask(tt);
    }

    /**
     * 刷新调度
     * @param triggerId
     */
    public boolean refreshTask(Long triggerId) {
        TaskTrigger tt = quartzSource.getSchedulePersistentProcessor().getTaskTrigger(triggerId);
        return this.refreshTask(tt);
    }


    /**
     * 检查是否可以运行
     * @param tt
     * @return
     */
    public boolean checkCanRun(TaskTrigger tt){
        if(tt.getTriggerStatus()!=TriggerStatus.ENABLE){
            return false;
        }
        String deployId = this.quartzSource.getDeployId();
        boolean sd = this.quartzSource.getDistriable();
        //如果是部署点一致，可以运行
        if(deployId.equals(tt.getDeployId())){
            return true;
        }else if(sd&&tt.getDistriable()){
            //部署点不一致，必须服务器和调度本身都需要支持分布式
            return true;
        }else{
            return false;
        }
    }

    @Override
    public HandlerInfo getHandlerInfo() {
        HandlerInfo hi = super.getHandlerInfo();
        hi.addDetail("EnableSchedule",String.valueOf(enableSchedule));
        if(enableSchedule){
            hi.addDetail("ScheduleStatus",String.valueOf(getScheduleStatus()));
        }
        hi.addDetail("ScheduleJobsCount",String.valueOf(getScheduleJobsCount()));
        hi.addDetail("CurrentlyExecutingJobsCount",String.valueOf(getCurrentlyExecutingJobsCount()));
        if(enableSchedule){
            hi.addDetail("ThreadPoolActiveCount", String.valueOf(scheduledThreadPool.getActiveCount()));
            hi.addDetail("ThreadPoolCompletedTaskCount", String.valueOf(scheduledThreadPool.getCompletedTaskCount()));
        }
        return hi;
    }
}
