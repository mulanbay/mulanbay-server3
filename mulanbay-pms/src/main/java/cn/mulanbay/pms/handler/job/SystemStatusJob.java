package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.LogHandler;
import cn.mulanbay.pms.handler.SystemStatusHandler;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobExecuteResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;
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
        int code = para.getCode();
        int status = systemStatusHandler.getStatus();
        boolean ns = this.needStop();
        Boolean res = null;
        String msg = null;
        if(ns){
            if(status!=code){
                res = systemStatusHandler.setStatus(code,para.getMessage(),null);
                msg = "关闭系统，code = "+code+",执行结果:"+res;
            }
        }else{
            if(status!= ErrorCode.SUCCESS){
                res = systemStatusHandler.revert(para.getCode());
            }
        }
        if(res!=null){
            tr.setComment(msg);
            if(res){
                tr.setExecuteResult(JobExecuteResult.SUCCESS);
            }else{
                tr.setExecuteResult(JobExecuteResult.FAIL);
            }
            //写系统日志
            LogHandler logHandler = BeanFactoryUtil.getBean(LogHandler.class);
            logHandler.addSysLog(LogLevel.WARNING,"系统状态操作","系统状态调度器设置系统状态,"+msg, PmsCode.SYSTEM_STATUS_CHANGE);

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
        boolean inPeriod = this.checkTimeExec(new Date(),para.getStartPeriod());
        return !inPeriod;
    }

    @Override
    public Class getParaDefine() {
        return SystemStatusJobPara.class;
    }
}
