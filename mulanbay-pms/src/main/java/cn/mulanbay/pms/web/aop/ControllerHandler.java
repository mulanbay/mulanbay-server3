package cn.mulanbay.pms.web.aop;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.BindUserLevel;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import cn.mulanbay.pms.handler.TokenHandler;
import cn.mulanbay.pms.persistent.domain.SysFunc;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.web.bean.LoginUser;
import cn.mulanbay.web.bean.request.PageSearch;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

import static cn.mulanbay.pms.common.Constant.DAY_SECONDS;

/**
 * 全局Aop
 * 登录、权限验证及用户信息的自动注入
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
@Aspect
public class ControllerHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerHandler.class);

    @Value("${mulanbay.persistent.page.maxPageSize}")
    int maxPageSize;

    @Autowired
    SystemConfigHandler systemConfigHandler;

    @Autowired
    protected TokenHandler tokenHandler;

    @Autowired
    CacheHandler cacheHandler;

    private HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        return request;
    }

    /**
     * controller包的子包里面任何方法
     */
    @Pointcut("execution(public * cn.mulanbay.pms.web.controller..*.*(..))")
    public void setUserInfo() {
    }

    /**
     * controller包的子包里面任何方法
     */
    @Pointcut("execution(public * cn.mulanbay.pms.web.controller..*.*(..))")
    public void doLog() {
    }

    @Before("setUserInfo()")
    public void beforeBuss(JoinPoint joinPoint) {
        try {
            //检查功能点是否启用
            HttpServletRequest request = this.getRequest();
            String url = request.getServletPath();
            String method = request.getMethod();
            SysFunc sf = systemConfigHandler.getFunction(url, method);
            //检查系统功能
            checkSysFunc(sf);
            boolean autoLoad = !sf.getSecAuth();
            LoginUser loginUser = tokenHandler.getLoginUser(request, autoLoad);
            if (sf.getLoginAuth()) {
                if (loginUser == null) {
                    throw new ApplicationException(PmsCode.USER_NOT_LOGIN);
                }
                //刷新用户
                tokenHandler.verifyToken(loginUser);
                Long userId = loginUser.getUserId();
                //请求限制检查
                checkUserLimit(userId,sf);
                //每日限制检查
                checkSysLimit(userId,sf);
                //权限认证
                Long roleId = loginUser.getRoleId();
                checkPermission(roleId,sf);
            }
            //设置用户等信息
            handleRequestInfoSet(joinPoint, loginUser);
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("do beforeBuss error", e);
            throw new ApplicationException(PmsCode.AOP_ERROR,e.getMessage());
        }
    }

    /**
     * 请求限制验证
     * @param userId
     * @param sf
     */
    private void checkUserLimit(Long userId, SysFunc sf){
        if (sf.getUserPeriod()>0) {
            String key = CacheKey.getKey(CacheKey.REQUEST_USER_LIMIT,sf.getUrlAddress(),userId.toString());
            //请求限制
            Date date = cacheHandler.get(key,Date.class);
            if (date != null) {
                logger.warn("用户ID={},请求{}过于频繁",userId,sf.getUrlAddress());
                throw new ApplicationException(PmsCode.USER_REQUEST_TOO_FREQ);
            } else {
                cacheHandler.setMS(key, new Date(), sf.getUserPeriod().longValue());
            }
        }
    }

    /**
     * 请求限制验证
     * @param userId
     * @param sf
     */
    private void checkSysLimit(Long userId, SysFunc sf){
        if (sf.getSysLimit() > 0) {
            String key = CacheKey.getKey(CacheKey.REQUEST_SYS_LIMIT,sf.getUrlAddress(),DateUtil.getToday(DateUtil.FormatDay1));
            //请求限制
            Integer s = cacheHandler.get(key, Integer.class);
            if (s != null) {
                if (s.intValue() < sf.getSysLimit()) {
                    s = s + 1;
                    cacheHandler.set(key, s,  DAY_SECONDS);
                } else {
                    logger.warn("用户ID={},在当天请求{}过于频繁",userId,sf.getUrlAddress());
                    throw new ApplicationException(PmsCode.USER_FUNCTION_TOO_FREQ);
                }
            } else {
                cacheHandler.set(key, 1, DAY_SECONDS);
            }
        }
    }

    /**
     * 检查系统功能
     * @param sf
     */
    private void checkSysFunc(SysFunc sf){
        if (sf == null) {
            throw new ApplicationException(PmsCode.FUNCTION_UN_DEFINE);
        } else if (sf.getStatus() == CommonStatus.DISABLE) {
            throw new ApplicationException(PmsCode.SYSTEM_FUNCTION_DISABLED);
        }
    }

    /**
     * 检查权限
     * @param roleId 用户当前登录的角色
     * @param sf 当前访问的功能点
     */
    private void checkPermission(Long roleId,SysFunc sf){
        if (sf.getPermissionAuth()) {
            if (roleId == null) {
                throw new ApplicationException(PmsCode.USER_NOT_AUTH);
            }
            boolean b = systemConfigHandler.isRoleAuth(roleId, sf.getFuncId());
            if (!b) {
                throw new ApplicationException(PmsCode.USER_NOT_AUTH);
            }
        }
    }

    @AfterReturning(value = "doLog()", returning = "resultMap")
    public void afterBuss(JoinPoint joinpoint, Object resultMap) {
    }

    /**
     * 抛出异常时才调用
     */
    @AfterThrowing("doLog()")
    public void afterThrowing() {
        //System.out.println("校验token出现异常了......");
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    public void handleRequestInfoSet(JoinPoint joinPoint, LoginUser loginUser) {
        Object[] arguments = joinPoint.getArgs();
        for (Object arg : arguments) {
            if (arg != null) {
                if (loginUser != null) {
                    if (arg instanceof BindUserLevel) {
                        BindUserLevel bu = (BindUserLevel) arg;
                        bu.setLevel(loginUser.getLevel());
                    }
                    // 个人模式下，直接设置为当前用户
                    if (arg instanceof BindUser) {
                        BindUser bu = (BindUser) arg;
                        bu.setUserId(loginUser.getUserId());
                    }
                }
                // 判断分页数据中的最大数
                if (arg instanceof PageSearch) {
                    PageSearch bu = (PageSearch) arg;
                    if (bu.getPageSize() > maxPageSize) {
                        throw new ApplicationException(PmsCode.PAGE_SIZE_OVER_MAX);
                    }
                }
                //时间查询类添加23:59:59
                if (arg instanceof FullEndDateTime) {
                    FullEndDateTime qwu = (FullEndDateTime) arg;
                    Date endDate = qwu.getEndDate();
                    if (endDate != null) {
                        qwu.setEndDate(DateUtil.tillMiddleNight(endDate));
                    }
                }
            }

        }
    }
}
