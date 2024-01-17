package cn.mulanbay.business.handler;

import cn.mulanbay.business.BusinessCode;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler管理类
 * 系统初始化时由它来调用各个Handler的初始化工作
 * 系统关闭时由它来调用各个Handler的资源回收工作
 * //@see cn.mulanbay.web.servlet.SpringServlet
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class HandlerManager {

	private static final Logger logger = LoggerFactory.getLogger(HandlerManager.class);

	@Autowired
	private List<BaseHandler> handlerList;

	public List<BaseHandler> getHandlerList() {
		return handlerList;
	}

	public void setHandlerList(List<BaseHandler> handlerList) {
		this.handlerList = handlerList;
	}

	/**
	 * 获取handler
	 * @param className
	 * @return
	 */
	public BaseHandler getHandler(String className){
		try {
			Class clz = Class.forName(className);
			return this.getHandler(clz);
		} catch (Exception e) {
			throw new ApplicationException(BusinessCode.CLASS_NAME_NOT_FOUND,"未找到相关类");
		}
	}

	/**
	 * 获取handler
	 * @param clz
	 * @return
	 */
	public BaseHandler getHandler(Class clz){
		if(StringUtil.isEmpty(handlerList)){
			return null;
		}else{
			for(BaseHandler bh : handlerList){
				if(bh.getClass().equals(clz)){
					return bh;
				}
			}
		}
		return null;
	}

	/**
	 * 获取Handler信息列表
	 * @return
	 */
	public List<HandlerInfo> getHandlerInfoList(){
		if(StringUtil.isEmpty(handlerList)){
			return new ArrayList<>();
		}else{
			List<HandlerInfo> res = new ArrayList<>();
			for(BaseHandler bh : handlerList){
				res.add(bh.getHandlerInfo());
			}
			return res;
		}
	}

	/**
	 * 获取支持的方法列表
	 * @return
	 */
	public List<MethodBean> getMethodList(String className){
		try {
			Class clz = Class.forName(className);
			List<MethodBean> list = new ArrayList<>();
			Method[] ms = clz.getDeclaredMethods();
			for(Method m : ms){
				HandlerMethod hm = m.getAnnotation(HandlerMethod.class);
				if(hm!=null){
					MethodBean mb = new MethodBean();
					mb.setName(hm.desc());
					mb.setMethod(m.getName());
					list.add(mb);
				}
			}
			return list;
		} catch (ClassNotFoundException e) {
			throw new ApplicationException(BusinessCode.CLASS_NAME_NOT_FOUND,"未找到相关类");
		}
	}

	/**
	 * 执行方法
	 * @param method
	 * @param className
	 * @return
	 */
	public boolean invokeMethod(String method,String className){
		try {
			Class clz = Class.forName(className);
			BaseHandler handler = this.getHandler(clz);
			Method m = clz.getMethod(method);
			m.invoke(handler);
			return true;
		} catch (ClassNotFoundException e) {
			throw new ApplicationException(BusinessCode.CLASS_NAME_NOT_FOUND,"未找到相关类");
		} catch (Exception e) {
			logger.error("invokeMethod error",e);
			return false;
		}
	}

}
