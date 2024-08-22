package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.CommonResult;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.CommandHandler;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.persistent.domain.Command;
import cn.mulanbay.schedule.para.ParaCheckResult;
import cn.mulanbay.schedule.ScheduleCode;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;


/**
 * 命令job，执行本地脚本文件或者命令
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class PmsCommandJob extends AbstractBaseJob {

    private PmsCommandJobPara para;

    @Override
    public TaskResult doTask() {
        TaskResult tr = new TaskResult();
        String cmd = this.getCmd();
        CommandHandler commandHandler = BeanFactoryUtil.getBean(CommandHandler.class);
        CommonResult cr = commandHandler.handleCmd(cmd,para.getOsType(),para.isSync());
        tr.setComment(cr.getInfo());
        tr.setResult(JobResult.SUCCESS);
        return tr;
    }

    private String getCmd() {
        String cmd = para.getCmd();
        if (StringUtil.isNotEmpty(cmd)) {
            return cmd;
        } else {
            BaseService baseService = BeanFactoryUtil.getBean(BaseService.class);
            Command cc = baseService.getObject(Command.class, para.getCode(), "code");
            if (cc == null) {
                throw new ApplicationException(ScheduleCode.TRIGGER_PARA_FORMAT_ERROR, "找不到code=" + para.getCode() + "的命令配置");
            } else {
                return cc.getUrl();
            }
        }
    }

    @Override
    public ParaCheckResult checkTriggerPara() {
        ParaCheckResult rb = new ParaCheckResult();
        para = this.getTriggerParaBean();
        if (para == null) {
            rb.setCode(ScheduleCode.TRIGGER_PARA_NULL);
        }
        return rb;
    }

    @Override
    public Class getParaDefine() {
        return PmsCommandJobPara.class;
    }

    public void notifyLog(String cmd) {
        //通知
        String title = "服务器执行了脚本命令";
        NotifyHandler notifyHandler = BeanFactoryUtil.getBean(NotifyHandler.class);
        notifyHandler.addMessageToNotifier(PmsCode.CMD_EXECUTED, title,
                "服务器执行了脚本命令：" + cmd, null, null);
    }
}
