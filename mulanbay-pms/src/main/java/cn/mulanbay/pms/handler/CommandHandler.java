package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.common.config.OSType;
import cn.mulanbay.common.exception.CommonResult;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.domain.Command;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.service.CommandService;
import cn.mulanbay.pms.thread.CommandExecuteThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 命令处理
 */
@Component
public class CommandHandler extends BaseHandler {

    @Autowired
    BaseService baseService;

    @Autowired
    CommandService commandService;

    public CommandHandler() {
        super("命令处理");
    }


    public CommonResult handleCmd(Long id, boolean sync) {
        Command cc = baseService.getObject(Command.class, id);
        return handleCmd(cc, sync);
    }

    public CommonResult handleCmdScode(String scode, boolean sync) {
        Command cc = commandService.getCommandByScode(scode);
        return handleCmd(cc, sync);
    }

    public CommonResult handleCmdCode(String code, boolean sync) {
        Command cc = commandService.getCommand(code);
        return handleCmd(cc, sync);
    }

    public CommonResult handleCmd(Command cc, boolean sync) {
        CommonResult cr = new CommonResult();
        if (cc.getStatus() == CommonStatus.DISABLE) {
            cr.setCode(PmsCode.CMD_DISABLED);
            return cr;
        }
        return this.handleCmd(cc.getUrl(),OSType.UNKNOWN,sync);
    }

    /**
     * 处理命令
     * @param cmd 命令地址(在操作系统下的全路径)
     * @param sync
     * @return
     */
    public CommonResult handleCmd(String cmd,OSType osType, boolean sync) {
        CommonResult cr = new CommonResult();
        CommandExecuteThread thread = new CommandExecuteThread(cmd,osType);
        if(sync){
            String res = thread.exeCmd();
            cr.setInfo(res);
        }else{
            thread.start();
            cr.setInfo("请稍后检查执行结果!");
        }
        return cr;

    }
}
