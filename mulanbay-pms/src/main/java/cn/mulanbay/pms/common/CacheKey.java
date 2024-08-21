package cn.mulanbay.pms.common;

import java.text.MessageFormat;

/**
 * 缓存key的定义
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class CacheKey {

    /**
     * 用户今日日历任务数缓存（用户编号）
     */
    public static final String USER_TODAY_CALENDAR_COUNTS = "user:todayCalendar:counts:{0}";

    /**
     * 用户计划配置（用户编号：planId）
     */
    public static final String USER_PLAN_NOTIFY = "user:planNotify:{0}:{1}";

    /**
     * 用户统计提醒配置（用户编号：statId）
     */
    public static final String USER_STAT_NOTIFY = "user:statNotify:{0}:{1}";

    /**
     * 用户积分的锁定（用户编号）
     */
    public static final String REWARD_LOCK = "user:rewardUpdate:lock:{0}";

    /**
     * 命令的锁定（命令编号）
     */
    public static final String CMD_EXE_LOCK = "sys:cmdExe:lock:{0}";

    /**
     * 用户当前积分（用户编号）
     */
    public static final String USER_CURRENT_POINTS = "user:currentPoints:{0}";

    /**
     * 用户当前积分（用户编号,功能点号）
     */
    public static final String USER_CONTINUE_OP = "user:continue:op:{0}:{1}";

    /**
     * 用户提醒统计（用户编号：USER_STAT_ID）
     */
    public static final String USER_STAT = "user:stat:{0}:{1}";

    /**
     * 用户登录失败次数（用户名称/手机号）
     */
    public static final String USER_LOGIN_FAIL = "user:loginFail:{0}";

    /**
     * 用户代码发送限流
     * 错误代码，用户编号
     */
    public static final String USER_CODE_LIMIT = "user:codeLimit:{0}:{1}";

    /**
     * 系统监控时间线
     */
    public static final String SERVER_DETAIL_MONITOR_TIMELINE = "sys:serverDetailMonitor:timeline";

    /**
     * 微信WX_JSAPI_TICKET（appid）
     */
    public static final String WX_JSAPI_TICKET = "wx:jsApi:ticket:{0}";

    /**
     * 微信WX_ACCESS_TOKEN（appid）
     */
    public static final String WX_ACCESS_TOKEN = "wx:accessToken:{0}";

    /**
     * 用户最新的一条消息（用户编号）
     */
    public static final String USER_LATEST_MESSAGE = "user:latestMessage:{0}";

    /**
     * 用户最新评分（用户编号）
     */
    public static final String USER_LATEST_SCORE = "user:latestScore:{0}";

    /**
     * 验证码
     */
    public static final String CAPTCHA_CODE = "security:captcha:code:{0}";

    /**
     * 消费记录缓存队列:用户ID
     */
    public static final String CONSUME_CACHE_QUEUE = "user:consume:cacheQueue:{0}";

    /**
     * 商品类型的匹配列表:用户ID
     */
    public static final String GOODS_TYPE_MATCH_LIST = "user:goodsType:matchList:{0}";

    /**
     * 饮食分类
     */
    public static final String FOOD_CATEGORY_LIST = "buss:diet:foodCategory";

    /**
     * 商品的寿命
     */
    public static final String GOODS_LIFETIME_LIST = "buss:goods:lifetimeList";

    /**
     * 消费记录匹配追踪
     */
    public static final String CONSUME_MATCH_TRACE = "buss:consume:matchTrace:{0}";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 登录用户
     * redis key
     */
    public static final String LOGIN_TOKEN_KEY = "security:login_tokens:{0}";

    /**
     * cookie里面使用
     */
    public static final String TOKEN_KEY = "Admin-Token";

    /**
     * 功能点
     */
    public static String SYS_FUNC ="sys_func";

    /**
     * 角色功能点
     */
    public static String ROLE_FUNC ="role_function";

    /**
     * 系统代码更新
     */
    public static String SYS_CODE_COUNTS ="buss:sysCode:counts:{0}";

    /**
     * 系统代码更新锁
     */
    public static String SYS_CODE_COUNTS_LOCK ="buss:sysCode:counts:lock:{0}";

    /**
     * 系统代码限流
     * 代码,时间
     */
    public static String SYS_CODE_COUNTS_LIMIT ="buss:sysCode:counts:limit:{0}:{1}";

    /**
     * 请求限制
     * 参数：URL,用户ID
     */
    public static String REQUEST_USER_LIMIT ="buss:request:userLimit:{0}:{1}";

    /**
     * 参数：URL,时间
     */
    public static String REQUEST_SYS_LIMIT ="buss:request:sysLimit:{0}:{1}";

    /**
     * 参数：消息ID
     */
    public static String MESSAGE_SEND_LOCK ="buss:message:sendLock:{0}";

    /**
     * @param pattern
     * @param args
     * @return
     */
    public static String getKey(String pattern, String... args) {
        return MessageFormat.format(pattern, args);
    }

}
