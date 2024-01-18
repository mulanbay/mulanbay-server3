package cn.mulanbay.business.handler;

import cn.mulanbay.common.exception.ValidateError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

/**
 * 资源文件配置的处理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class MessageHandler extends BaseHandler {

	private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

	@Autowired
	ReloadableResourceBundleMessageSource messageSource;

	public MessageHandler() {
		super("消息处理");
	}

	/**

	 * 表单字段验证异常及错误代码的异常信息
	 *
	 * @param key
	 * @return
	 */
	public ValidateError getErrorInfo(String key) {
		ValidateError ve = new ValidateError();
		ve.setField(key);
		ve.setErrorInfo(this.getConfigMessage(key));
		return ve;
	}

	/**
	 * 后期可以根据Accept-Language来选择资源文件类型，实现国际化
	 *
	 * @param key
	 * @return
	 */
	public String getConfigMessage(String key) {
		try {
			return messageSource.getMessage(key, null,
					Locale.ROOT);
		} catch (NoSuchMessageException e) {
			logger.warn("未找到" + key + "的配置项");
		} catch (Exception e) {
			logger.error("GetConfigMessage error", e);
		}
		return null;
	}

	public ValidateError getCodeInfo(int code) {
		return getErrorInfo("code." + code);
	}

}
