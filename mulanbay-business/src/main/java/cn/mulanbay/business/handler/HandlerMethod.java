package cn.mulanbay.business.handler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 方法定义
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerMethod {

	String desc();

}
