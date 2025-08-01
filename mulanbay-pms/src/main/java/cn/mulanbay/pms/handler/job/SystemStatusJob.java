package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.LogHandler;
import cn.mulanbay.pms.handler.SystemStatusHandler;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;
import cn.mulanbay.schedule.para.ParaCheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author fenghong
 * @title: 系统状态
 * @description: TODO
 * @date 2019/4/24 12:10 PM
 */
public class SystemStatusJob extends AbstractBaseJob {

    private static final Logger logger = LoggerFactory.getLogger(SystemStatusJob.class);

    private SystemStatusJobPara para;

    @Override
    public TaskResult doTask() {
        TaskResult tr = new TaskResult();
        SystemStatusHandler systemStatusHandler = BeanFactoryUtil.getBean(SystemStatusHandler.class);
        int stopStatus = para.getStopStatus();
        int status = systemStatusHandler.getStatus();
        boolean ns = this.needStop();
        Boolean res = null;
        String msg = null;
        int sysCode = 0;
        if(ns){
            if(status!=stopStatus){
                String cc = "系统定时关闭时间:"+para.getStopPeriod()+","+para.getMessage();
                res = systemStatusHandler.lock(stopStatus,cc,null,null);
                msg = "关闭系统，stopStatus = "+stopStatus+",执行结果:"+res;
                sysCode = PmsCode.SYSTEM_LOCK;
            }
        }else{
            if(status!= ErrorCode.SUCCESS){
                res = systemStatusHandler.revert(para.getStopStatus());
                sysCode = PmsCode.SYSTEM_UNLOCK;
            }
        }
        if(res!=null){
            tr.setComment(msg);
            if(res){
                tr.setResult(JobResult.SUCCESS);
                //写系统日志
                LogHandler logHandler = BeanFactoryUtil.getBean(LogHandler.class);
                logHandler.addSysLog("系统状态操作","系统状态调度器设置系统状态,"+msg, sysCode);
            }else{
                tr.setResult(JobResult.FAIL);
                tr.setComment(msg);
            }
        }
        return tr;
    }

    @Override
    public ParaCheckResult checkTriggerPara() {
        para = this.getTriggerParaBean();
        if (para == null) {
            para = new SystemStatusJobPara();
        }
        return DEFAULT_SUCCESS_PARA_CHECK;
    }

    /**
     * 是否需要停止
     * @return
     */
    private boolean needStop(){
        String sp = para.getStopPeriod();
        if(StringUtil.isEmpty(sp)){
            return false;
        }
        return this.checkTimeExec(new Date(),para.getStopPeriod());
    }

    @Override
    public Class getParaDefine() {
        return SystemStatusJobPara.class;
    }
}
