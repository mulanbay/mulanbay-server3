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
     * 用户今日日历任务数时间线缓存（用户编号）
     */
    public static final String USER_TODAY_CALENDAR_TIMELINE_COUNTS = "user:todayCalendar:timeline:counts:{0}";


    /**
     * 日终积分统计
     */
    public static final String DAILY_REWARD_POINTS_STAT = "daily_reward_points_stat:{0}";

    /**
     * 用户计划配置（用户编号：planId）
     */
    public static final String USER_PLAN_NOTIFY = "userPlanNotify:{0}:{1}";

    /**
     * 用户提醒配置（用户编号：statId）
     */
    public static final String USER_STAT_NOTIFY = "userStatNotify:{0}:{1}";

    /**
     * 用户积分的锁定（用户编号）
     */
    public static final String REWARD_POINT_LOCK = "distributeLock:updateUserPoint:{0}";

    /**
     * 命令的锁定（命令编号）
     */
    public static final String CMD_SEND_LOCK = "distributeLock:cmdSend:{0}";

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
    public static final String USER_STAT = "userStat:{0}:{1}";

    /**
     * 用户登录失败次数（用户名称/手机号）
     */
    public static final String USER_LOGIN_FAIL = "userLoginFail:{0}";

    /**
     * 用户新增操作（sessionId:URL）
     */
    public static final String USER_OPERATE_OP = "userOperateOp:{0}:{1}";

    /**
     * 用户错误代码发送限流（用户编号：错误代码）
     */
    public static final String USER_ERROR_CODE_LIMIT = "userErrorCodeLimit:{0}:{1}";

    /**
     * 系统监控时间线
     */
    public static final String SERVER_DETAIL_MONITOR_TIMELINE = "serverDetailMonitorTimeline";

    /**
     * 用户登录token（token）
     */
    public static final String USER_LOGIN_TOKEN = "userLogin:{0}";

    /**
     * 用户二次认证（用户编号）
     */
    public static final String USER_SEC_AUTH_CODE = "user:secAuthCode:{0}";

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
     * QA列表
     */
    public static final String QA_LIST = "qa:list";

    /**
     * QA的匹配缓存
     */
    public static final String QA_MATCH_CACHE = "qa:match:{0}";

    /**
     * 验证码
     */
    public static final String CAPTCHA_CODE = "captcha:code:{0}";

    /**
     * 消费记录缓存队列:用户ID
     */
    public static final String CONSUME_CACHE_QUEUE = "consume:cacheQueue:x:{0}";

    /**
     * 商品类型的匹配列表:用户ID
     */
    public static final String GOODS_TYPE_MATCH_LIST = "goodsType:matchList:x:{0}";

    /**
     * 饮食分类
     */
    public static final String FOOD_CATEGORY_LIST = "diet:foodCategory";

    /**
     * 商品的寿命
     */
    public static final String GOODS_LIFETIME_LIST = "goods:lifetimeList";

    /**
     * 消费记录匹配追踪
     */
    public static final String CONSUME_MATCH_TRACE = "consume:matchTrace:{0}";

    /**
     * 二次认证
     */
    public static final String USER_SEC_AUTH = "user_sec_auth";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:{0}";

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
    public static String SYS_CODE_COUNTS ="sys_code_counts:{0}";

    /**
     * 系统代码更新锁
     */
    public static String SYS_CODE_COUNTS_LOCK ="sys_code_counts:lock:{0}";

    /**
     * 系统代码限流
     * 代码,时间
     */
    public static String SYS_CODE_COUNTS_LIMIT ="sys_code_counts:limit:{0}:{1}";

    /**
     * 请求限制
     * 参数：URL,用户ID
     */
    public static String REQUEST_USER_LIMIT ="request_user_limit:{0}:{1}";

    /**
     * 参数：URL,时间
     */
    public static String REQUEST_SYS_LIMIT ="request_sys_limit:{0}:{1}";


    /**
     * @param pattern
     * @param args
     * @return
     */
    public static String getKey(String pattern, String... args) {
        return MessageFormat.format(pattern, args);
    }

}
