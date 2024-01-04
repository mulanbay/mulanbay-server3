package cn.mulanbay.pms.web.interceptor;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.common.util.MapUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.handler.LogHandler;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import cn.mulanbay.pms.handler.TokenHandler;
import cn.mulanbay.pms.persistent.domain.OperLog;
import cn.mulanbay.pms.persistent.domain.SysFunc;
import cn.mulanbay.pms.util.IPUtil;
import cn.mulanbay.pms.web.bean.LoginUser;
import cn.mulanbay.web.filter.MultipleRequestWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.RequestFacade;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 参数请求处理，目前用来记录日志
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class RequestInterceptor extends BaseInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestInterceptor.class);

    @Value("${mulanbay.log.operLog}")
    boolean enableOperLog;

    @Autowired
    SystemConfigHandler systemConfigHandler;

    @Autowired
    TokenHandler tokenHandler;

    @Autowired
    LogHandler logHandler;

    private ThreadLocal<String> para = new ThreadLocal<>();

    private ThreadLocal<Date> startTime = new ThreadLocal<>();

    public RequestInterceptor() {
    }

    public RequestInterceptor(boolean enableOperationLog) {
        this.enableOperLog = enableOperationLog;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("收到请求地址：" + request.getServletPath() + ",method:" + request.getMethod());
        logger.debug("ContentType:" + request.getContentType());
        startTime.set(new Date());
        if (enableOperLog) {
            setPara(request);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exp) throws Exception {
        //logger.debug("afterCompletion请求参数："+ para.get());
        if (enableOperLog) {
            addOperationLog(request);
        }
    }

    /**
     * 设置请求参数
     *
     * @param request
     */
    private void setPara(HttpServletRequest request) {
        try {
            String method = request.getMethod();
            String contentType = request.getContentType();
            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                logger.debug("文件处理请求");
            } else if ("GET".equals(method) || contentType == null) {
                HttpServletRequest hsr = request;
                para.set(JsonUtil.beanToJson(MapUtil.changeRequestMapToNormalMap(hsr.getParameterMap())));
            } else if ("POST".equals(method)) {
                MultipleRequestWrapper requestWrapper = new MultipleRequestWrapper(request);
                para.set(requestWrapper.getBody());
            }
            logger.debug("preHandle请求参数：" + para.get());
        } catch (Exception e) {
            logger.error("set para error", e);
        }
    }

    /**
     * 增加操作日志
     *
     * @param request
     */
    private void addOperationLog(HttpServletRequest request) {
        try {
            String url = request.getServletPath();
            String method = request.getMethod();
            SysFunc sf = systemConfigHandler.getFunction(url, method);
            if (sf != null && !sf.getDoLog()) {
                logger.warn("请求地址[" + url + "],method[" + method + "]功能点配置不记录日志");
                return;
            }
            // 记录操作日志
            OperLog log = new OperLog();
            log.setSysFunc(sf);
            log.setOccurStartTime(startTime.get());
            log.setParas(para.get());
            log.setUrlAddress(url);
            log.setMethod(method);
            log.setIpAddress(IPUtil.getIpAddress(request));
            LoginUser loginUser = tokenHandler.getLoginUser(request);
            if (loginUser != null) {
                log.setUserId(loginUser.getUserId());
                log.setUserName(loginUser.getUsername());
            }
            log.setOccurEndTime(new Date());
            String cacheKey = CacheKey.getKey(CacheKey.USER_OPERATE_OP, request.getRequestedSessionId(), url);
            //todo CacheHandler 如果采用AutoWired模式注入，导致拦截器无法正常功能，且JsonWebMvcConfig无法正常加载，一时未找到原因
            CacheHandler cacheHandler = BeanFactoryUtil.getBean(CacheHandler.class);
            String idValue = cacheHandler.getForString(cacheKey);
            if (StringUtil.isNotEmpty(idValue)) {
                log.setIdValue(idValue);
            }
            logHandler.addOperationLog(log);
            logger.debug("记录了操作日志");
        } catch (ApplicationException e) {
            logger.error("do before addOperationLog error", e);
        } catch (Exception e) {
            logger.error("do before addOperationLog error", e);
        }finally {
            para.remove();
            startTime.remove();
        }
    }
}
