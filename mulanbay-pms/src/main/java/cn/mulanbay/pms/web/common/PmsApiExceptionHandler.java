package cn.mulanbay.pms.web.common;

import cn.mulanbay.common.util.MapUtil;
import cn.mulanbay.pms.handler.LogHandler;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import cn.mulanbay.pms.handler.TokenHandler;
import cn.mulanbay.pms.persistent.domain.SysFunc;
import cn.mulanbay.pms.persistent.domain.SysLog;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import cn.mulanbay.pms.util.IPUtil;
import cn.mulanbay.pms.web.bean.LoginUser;
import cn.mulanbay.web.common.ApiExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

/**
 * 异常处理类
 *
 * @author fenghong
 */
@ControllerAdvice
public class PmsApiExceptionHandler extends ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(PmsApiExceptionHandler.class);

    @Value("${mulanbay.log.sysLog}")
    boolean needSystemLog;

    @Autowired
    LogHandler logHandler;

    @Autowired
    TokenHandler tokenHandler;

    @Autowired
    SystemConfigHandler systemConfigHandler;

    @Override
    protected boolean doSystemLog() {
        return needSystemLog;
    }

    @Override
    protected void addSystemLog(HttpServletRequest request, Class exceptionClass, String title, String msg, int errorCode) {
        try {
            SysLog log = new SysLog();
            log.setLogLevel(LogLevel.ERROR);
            log.setTitle(title);
            log.setContent(msg);
            log.setErrorCode(errorCode);
            long userId = 0;
            String userName = "系统操作";
            if (request != null) {
                LoginUser loginUser = tokenHandler.getLoginUser(request);
                if (loginUser != null) {
                    userId = loginUser.getUserId();
                    userName = loginUser.getUsername();
                }
                String url = request.getServletPath();
                String method = request.getMethod();
                SysFunc sf = systemConfigHandler.getFunction(url, method);
                log.setSysFunc(sf);
                log.setUrlAddress(url);
                log.setMethod(method);
                log.setIpAddress(IPUtil.getIpAddress(request));
                log.setExceptionClassName(exceptionClass.getName());
                //需要转换为通用的单项模式
                Map<String, String[]> pm = request.getParameterMap();
                log.setParaMap(MapUtil.changeRequestMapToNormalMap(pm));
            }
            log.setUserId(userId);
            log.setUsername(userName);
            logHandler.addSystemLog(log);
        } catch (Exception e) {
            logger.error("添加系统日志异常", e);
        }
    }

}
