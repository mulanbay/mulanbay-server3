package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.lock.DistributedLock;
import cn.mulanbay.common.config.OSType;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.CommonResult;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.MessageNotify;
import cn.mulanbay.common.util.CommandUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.domain.Command;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.service.CommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * 命令处理
 */
@Component
public class CommandHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    /**
     * 分布式锁重试次数
     */
    @Value("${mulanbay.lock.retryTimes:3}")
    int retryTimes;

    /**
     * 分布式锁超时时间(毫秒)
     */
    @Value("${mulanbay.lock.expire:5000}")
    long expire;

    @Autowired
    BaseService baseService;

    @Autowired
    CommandService commandService;

    @Autowired
    MessageNotify messageNotify;

    @Autowired
    ThreadPoolHandler threadPoolHandler;

    @Autowired
    DistributedLock distributedLock;

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
     * @param sync 是否同步
     * @return
     */
    public CommonResult handleCmd(String cmd,OSType osType, boolean sync) {
        if(sync){
            return this.exeCmd(cmd,osType);
        }else{
            try {
                Future<CommonResult> future = threadPoolHandler.submit(() -> this.exeCmd(cmd,osType));
                return future.get();
            } catch (Exception e) {
                logger.error("threadPool exe Callable error",e);
                throw new ApplicationException(PmsCode.EXECUTE_CMD_ERROR,e);
            }
        }
    }

    /**
     * 执行命令
     * @param cmd
     * @param osType
     * @return
     */
    private CommonResult exeCmd(String cmd,OSType osType){
        String lockKey = CacheKey.getKey(CacheKey.CMD_EXE_LOCK, cmd);
        boolean lock = false;
        try {
            lock = distributedLock.lock(lockKey, expire, retryTimes);
            if (!lock) {
                return new CommonResult(ErrorCode.CMD_EXEC_ERROR,"执行命令锁定失败");
            }
            String res = CommandUtil.executeCmd(osType, cmd);
            messageNotify.notifyMsg(ErrorCode.CMD_EXEC_NOTIFY,"命令执行结果通知","命令["+cmd+"]执行结果："+res);
            return new CommonResult(res);
        } catch (Exception e) {
            logger.error("执行操作系统类型为[" + osType + "]的命令:" + cmd + "异常", e);
            messageNotify.notifyMsg(ErrorCode.CMD_EXEC_ERROR,"命令执行异常","命令["+cmd+"]执行异常："+e.getMessage());
            return new CommonResult(ErrorCode.CMD_EXEC_ERROR,"命令执行异常,"+e.getMessage());
        } finally {
            if(lock){
                distributedLock.releaseLock(lockKey);
            }
        }
    }
}
