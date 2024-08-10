package cn.mulanbay.pms.web.interceptor;

import cn.mulanbay.business.handler.MessageHandler;
import cn.mulanbay.common.exception.ValidateError;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 拦截器基类
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class BaseInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(BaseInterceptor.class);

    private String interceptorName;

    @Autowired
    MessageHandler messageHandler;

    public String getInterceptorName() {
        return interceptorName;
    }

    public void setInterceptorName(String interceptorName) {
        this.interceptorName = interceptorName;
    }

    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception exp)
            throws Exception {
        // TODO Auto-generated method stub

    }

    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler, ModelAndView mav)
            throws Exception {
        // TODO Auto-generated method stub

    }

    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    void handleFail(HttpServletResponse response,int code, String failInfo) {
        try {
            String message = failInfo;
            if(StringUtil.isEmpty(message)){
                ValidateError ve = messageHandler.getCodeInfo(code);
                if (ve != null) {
                    message = ve.getErrorInfo();
                } else {
                    message = "未找到相关错误信息";
                }
            }
            ResultBean rb = new ResultBean();
            rb.setCode(code);
            rb.setMessage(message);
            // Ajax调用
            response.setContentType("text/html;charset = UTF-8");
            response.getWriter().print(JsonUtil.beanToJson(rb));
        } catch (Exception e) {
            logger.error("处理拦截器拦截验证失败异常", e);
        }
    }

}
