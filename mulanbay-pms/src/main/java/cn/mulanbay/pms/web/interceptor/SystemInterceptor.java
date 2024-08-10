package cn.mulanbay.pms.web.interceptor;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.pms.handler.SystemStatusHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 系统状态处理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Order(1)
@Component
public class SystemInterceptor extends BaseInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SystemInterceptor.class);

    @Autowired
    SystemStatusHandler systemStatusHandler;

    @Autowired
    LockProperties lockProperties;

    public SystemInterceptor() {
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        int code = systemStatusHandler.getStatus();
        if(code!= ErrorCode.SUCCESS){
            String path = request.getServletPath();
            if(lockProperties.auth(path)){
                //白名单
                return true;
            }
            logger.warn("系统停止服务,code={}",code);
            this.handleFail(response,code, systemStatusHandler.getMessage());
            return false;
        }
        return true;
    }

}
