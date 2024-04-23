package cn.mulanbay.business.handler;

/**
 * 基础Handler，定义通用的方法及流程
 * 项目中涉及到第三方或者在service-controller之间的调用的可以集成及实现
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class BaseHandler {

	// 自检失败关闭系统
	boolean scfShutdown = false;

	// 自检结果
	Boolean checkResult = null;

	//处理器名称
	protected String handlerName;

	public BaseHandler() {
	}

	public BaseHandler(String handlerName) {
		this.handlerName = handlerName;
	}

	/**
	 * 初始化，一般为系统启动时调用
	 */
	public void init() {

	}

	/**
	 * 重新加载，在系统运行时操作，需要线程同步
	 */
	public void reload() {

	}

	/**
	 * 容器destroy时调用
	 */
	public void destroy() {

	}

	/**
	 * 自检，一般为系统启动时调用
	 *
	 * @return
	 */
	public Boolean selfCheck() {
		return null;
	}

	public boolean isScfShutdown() {
		return scfShutdown;
	}

	public void setScfShutdown(boolean scfShutdown) {
		this.scfShutdown = scfShutdown;
	}

	public Boolean getCheckResult() {
		return checkResult;
	}

	/**
	 * Handler名称
	 *
	 * @return
	 */
	public String getHandlerName() {
		return handlerName;
	}

	/**
	 * 处理信息
	 * @return
	 */
	public HandlerInfo getHandlerInfo(){
		return new HandlerInfo(this.handlerName);
	}

	/**
	 * 是否记录日志
	 *
	 * @return
	 */
	public boolean isDoLog(){
		return false;
	}
}
