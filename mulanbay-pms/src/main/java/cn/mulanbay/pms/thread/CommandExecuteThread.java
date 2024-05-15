package cn.mulanbay.pms.thread;

import cn.mulanbay.business.handler.lock.DistributedLock;
import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.config.OSType;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.MessageNotify;
import cn.mulanbay.common.thread.EnhanceThread;
import cn.mulanbay.common.util.CommandUtil;
import cn.mulanbay.pms.common.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 操作系统命令执行线程
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class CommandExecuteThread extends EnhanceThread {

	private static final Logger logger = LoggerFactory.getLogger(CommandExecuteThread.class);

	// 异步时间（秒）
	private long asyncTime = 0;

	private String cmd;

	private OSType osType;

	public CommandExecuteThread(String cmd, OSType osType) {
		super("启动执行操作系统命令");
		this.cmd = cmd;
		this.osType = osType;
	}

    public long getAsyncTime() {
        return asyncTime;
    }

    public void setAsyncTime(long asyncTime) {
        this.asyncTime = asyncTime;
    }

	@Override
	public void doTask(){
		this.exeCmd();
	}

	public String exeCmd() {
		MessageNotify messageNotify = BeanFactoryUtil.getBean(MessageNotify.class);
		DistributedLock distributedLock = BeanFactoryUtil.getBean(DistributedLock.class);
		String lockKey = CacheKey.getKey(CacheKey.CMD_SEND_LOCK, cmd);
		try {
			boolean b = distributedLock.lock(lockKey, 5000L, 3, 20);
			if (!b) {
				return "执行命令锁定失败";
			}
			if(asyncTime>0){
				sleep(asyncTime * 1000);
			}
			String res = CommandUtil.executeCmd(osType, cmd);
			messageNotify.notifyMsg(ErrorCode.CMD_EXEC_NOTIFY,"命令执行结果通知","命令["+cmd+"]执行结果："+res);
			return res;
		} catch (Exception e) {
			logger.error("执行操作系统类型为[" + osType + "]的命令:" + cmd + "异常", e);
			messageNotify.notifyMsg(ErrorCode.CMD_EXEC_ERROR,"命令执行异常","命令["+cmd+"]执行异常："+e.getMessage());
			return "命令执行异常,"+e.getMessage();
		} finally {
			distributedLock.releaseLock(lockKey);
		}
	}

}
