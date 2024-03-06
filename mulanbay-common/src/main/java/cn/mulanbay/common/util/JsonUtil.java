package cn.mulanbay.common.util;

import cn.hutool.json.JSONUtil;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * JSON操作工具类
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class JsonUtil {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

	/**
	 * 将java对象转换成json字符串
	 *
	 * @param o
	 *            准备转换的对象
	 * @return json字符串
	 */
	public static String beanToJson(Object o) {
		try {
			if (o == null) {
				return null;
			}
			String json = JSONUtil.toJsonStr(o);
			return json;
		} catch (Exception e) {
			logger.error("对象[" + o + "]转换为Json异常：", e);
		}
		return null;
	}

	/**
	 * 将json字符串转换成java对象
	 *
	 * @param json
	 *            准备转换的json字符串
	 * @param cls
	 *            准备转换的类
	 * @return
	 * @throws Exception
	 */
	public static Object jsonToBean(String json, Class<?> cls) {
		try {
			if (json == null || "".equals(json)) {
				return null;
			}
			Object vo = JSONUtil.toBean(json,cls);
			return vo;
		} catch (Exception e) {
			logger.error("json[" + json + "]转换为Bean[" + cls + "]异常：", e);
			throw new ApplicationException(ErrorCode.JSON_PARSE_ERROR,"json[" + json + "]转换为Bean[" + cls + "]异常",e);
		}
	}

	/**
	 * 将json字符串转换成java对象
	 *
	 * @param json
	 *            准备转换的json字符串
	 * @param cls
	 *            准备转换的类
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static List jsonToBeanList(String json, Class<?> cls) {
		try {
			if (json == null || "".equals(json)) {
				return null;
			}
			List list = JSONUtil.toList(json,cls);
			return list;
		} catch (Exception e) {
			logger.error("json[" + json + "]转换为Bean[" + cls + "]异常：", e);
		}
		return null;
	}

}
