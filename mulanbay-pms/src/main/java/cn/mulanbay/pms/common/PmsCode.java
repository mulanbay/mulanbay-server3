package cn.mulanbay.pms.common;

/**
 * 错误代码
 * 规则：1位系统代码（pms为1）+2两位模块代码+2两位子模块代码+两位编码
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class PmsCode {

    /**
     * 限流停止
     */
    public final static int SERVER_LIMIT_STOP = 9997;

    /**
     * 自动停止
     */
    public final static int SERVER_AUTO_STOP = 9998;

    /**
     * 手动停止
     */
    public final static int SERVER_MANUAL_STOP = 9999;

    /**
     * 用户未找到
     */
    public final static int USER_NOT_FOUND = 10001;

    /**
     * 用户被禁用
     */
    public final static int USER_DISABLE = 10002;

    /**
     * 用户过期
     */
    public final static int USER_EXPIRED = 10003;

    /**
     * 用户未登录
     */
    public final static int USER_NOT_LOGIN = 10004;

    /**
     * 用户的登录token异常
     */
    public final static int USER_TOKEN_ERROR = 10005;

    /**
     * 用户密码错误
     */
    public final static int USER_PASSWORD_ERROR = 10006;

    /**
     * 未授权操作
     */
    public final static int USER_NOT_AUTH = 10007;

    public final static int EXECUTE_CMD_ERROR = 10008;

    public final static int IP_NOT_ALLOWED = 10009;

    public final static int THREAD_CAN_ONLY_DO_ONCE =10012;

    public final static int CREATE_CUSTOM_SSL_ERROR=10013;

    public final static int FTP_ERROR=10014;

    public final static int PAGE_SIZE_OVER_MAX=10015;

    public final static int USER_REQUEST_TOO_FREQ=10016;

    public final static int USER_FUNCTION_TOO_FREQ=10017;

    public final static int USER_LOGIN_FAIL_MAX=10018;

    public final static int USER_SEC_AUTH_FAIL=10019;

    /**
     * 验证码错误
     */
    public final static int USER_VERIFY_CODE_ERROR =10022;

    public final static int USER_LOGIN_FAMILY_FAIL =10023;

    public final static int USER_FAMILY_NO_ADMIN =10024;

    public final static int USER_NOT_IN_FAMILY =10025;

    public final static int FUNCTION_UN_DEFINE =10026;

    public final static int BEAN_GET_CACHE_ERROR =10027;

    /**
     * 系统锁定
     */
    public final static int SYSTEM_LOCK =10028;

    /**
     * 系统解锁
     */
    public final static int SYSTEM_UNLOCK =10029;

    /**
     * 通用类 start
     **/

    public static final int USER_ENTITY_NOT_FOUND = 1010001;

    public static final int USER_ENTITY_NOT_ALLOWED = 1010002;

    public static final int UN_SUPPORT_DATE_GROUP_TYPE = 1010003;

    /**
     * 用户重要操作
     */
    public static final int USER_IMP_OP = 1010004;

    /**
     * 用户敏感操作
     */
    public static final int USER_SENS_OP = 1010005;

    /**
     * 用户登录
     */
    public static final int USER_LOGIN = 1010006;

    /**
     * 清除缓存失败
     */
    public static final int DELETE_CACHE_ERROR = 1010007;

    public static final int URL_DECODE_ERROR = 1010008;

    /**
     * 表单验证失败
     */
    public static final int FROM_CHECK_FAIL = 1010009;

    /**
     * 表单验证失败
     */
    public static final int CREATE_TREE_ERROR = 1010010;

    public static final int CLASS_NOT_FOUND = 1010011;

    public static final int AOP_ERROR = 1010012;

    /** 通用类 end **/

    /**
     * 用户类 start
     **/
    public static final int USER_SEC_AUTH_PHONE_NULL = 1010101;
    public static final int USER_SEC_AUTH_EMAIL_NULL = 1010102;
    public static final int USER_SEC_AUTH_WECHAT_NULL = 1010103;
    public static final int USER_SEC_AUTH_CODE_NULL = 1010105;
    public static final int USER_SEC_AUTH_CODE_ERROR = 1010106;

    /** 用户 end **/

    /**
     * 用户行为类 start
     **/
    public static final int USER_BEHAVIOR_CONFIG_WITH_LEVEL_NOT_FOUND = 1010201;
    public static final int USER_BEHAVIOR_CONFIG_CHECK_ERROR = 1010202;
    public static final int USER_BEHAVIOR_CONFIG_NULL = 1010203;

    /** 用户行为类 end **/

    /**
     * 报表类 start
     **/
    public static final int REPORT_CONFIG_SQL_ERROR = 1020101;

    /** 报表类 end **/

    /**
     * 消费类 start
     **/
    public static final int UNSUPPORT_CONSUME_GROUP_TYPE = 1030101;

    public static final int CONSUME_GOODS_TYPE_NULL = 1030102;

    /** 消费类 end **/

    /**
     * 运动类 start
     **/
    public static final int SPORT_MILESTONE_KM_MN_NULL = 1040101;

    public static final int SPORT_MILESTONE_KM_NULL = 1040102;

    public static final int SPORT_MILESTONE_ORDER_INDEX_ERROR = 1040103;

    /** 运动类 end **/

    /**
     * 计划类 start
     **/
    public static final int PLAN_CONFIG_SQL_RETURN_COLUMN_ERROR = 1050101;

    public static final int PLAN_CONFIG_PLAN_TYPE_NOT_SUPPORT = 1050102;

    public static final int PLAN_CONFIG_SQL_ERROR = 1050103;

    public static final int PLAN_REPORT_RE_STAT_YEAR_NULL = 1050104;

    public static final int USER_PLAN_REMIND_NULL = 1050105;


    /** 计划类 end **/

    /**
     * 阅读类 start
     **/
    public static final int BOOK_STATUS_ERROR = 1060101;

    public static final int BOOK_ISBN_EXIT = 1060102;

    public static final int BOOK_SCORE_NULL = 1060103;

    /** 阅读类 end **/

    /**
     * 日志类 start
     **/
    public static final int OPERATION_LOG_BEAN_ID_NULL = 1070101;

    public static final int OPERATION_LOG_COMPARE_ID_VALUE_NULL = 1070102;

    public static final int SYS_CODE_NOT_DEFINED = 1070103;

    /** 日志类 end **/

    /**
     * 系统类 start
     **/
    public static final int START_YEAR_NOT_EQUALS_END_YEAR = 1080101;

    public static final int SYSTEM_FUNCTION_NOT_DEFINE = 1080102;

    public static final int SYSTEM_FUNCTION_DISABLED = 1080103;

    public static final int OPERATION_LOG_PARA_IS_NULL = 1080104;

    public static final int OPERATION_LOG_CANNOT_GET = 1080105;

    public static final int START_OR_END_DATE_NULL = 1080106;

    public static final int NETWORK_RE_OK = 1080107;

    public static final int DISK_ALERT = 1080108;

    public static final int MEMORY_ALERT = 1080109;

    public static final int CPU_ALERT = 1080110;

    public static final int CMD_NOT_FOUND = 1080111;

    public static final int CMD_DISABLED = 1080112;

    public static final int SYSTEM_ALERT_AUTO_JOB = 1080113;

    public static final int HANDLER_START = 1080114;

    public static final int HANDLER_STOP = 1080114;

    /** 系统类 end **/

    /**
     * 健康类 start
     **/
    public static final int TREAT_DRUG_DETAIL_OCCUR_TIME_INCORRECT = 1090101;

    public static final int TREAT_SYNC_UN_CONFIG = 1090102;

    public static final int TREAT_TEST_NAME_DUPLICATE = 1090103;

    /** 健康类 end **/

    /**
     * 梦想类 start
     **/
    public static final int DREAM_PLAN_VALUE_NULL = 1100101;

    public static final int DREAM_FINISH_DATE_NULL = 1100102;

    /** 梦想类 end **/

    /**
     * 饮食类 start
     **/
    public static final int DIET_TYPE_NULL = 1110101;


    /** 饮食类 end **/


    /** 统计类 start **/

    /** 统计类 end **/

    /**
     * 资金类 start
     **/
    public static final int BUDGET_LOG_EXIT = 1130104;

    /** 资金类 end **/

    /**
     * 消息提醒类 start
     **/

    /**
     * 通用提醒代码
     */
    public static final int MESSAGE_NOTIFY_COMMON_CODE = 1140100;

    /**
     * 积分奖励日统计
     */
    public static final int REWARD_POINTS_DAILY_STAT = 1140101;

    /**
     * 用户统计
     */
    public static final int USER_STAT = 1140102;

    /**
     * 用户计划统计(未完成)
     */
    public static final int USER_PLAN_UN_COMPLETED_STAT = 1140103;

    /**
     * 用户计划统计(已完成)
     */
    public static final int USER_PLAN_COMPLETED_STAT = 1140104;

    /**
     * 用户梦想统计(未完成)
     */
    public static final int USER_DREAM_UN_COMPLETED_STAT = 1140105;

    /**
     * 用户梦想统计(已完成)
     */
    public static final int USER_DREAM_COMPLETED_STAT = 1140106;

    public static final int USER_DAILY_TASK_STAT = 1140107;

    public static final int USER_DRUG_REMIND_STAT = 1140110;

    public static final int CMD_EXECUTED = 1140111;

    public static final int MESSAGE_DUPLICATE_SEND = 1140112;

    public static final int BUDGET_CHECK_OVER = 1140113;

    public static final int BUDGET_CHECK_LESS = 1140114;

    public static final int BUDGET_UN_COMPLETED = 1140115;

    public static final int BUDGET_CHECK = 1140116;

    public static final int USER_OPERATION_REMIND_STAT = 1140117;

    public static final int CALENDAR_MESSAGE_NOTIFY = 1140118;

    public static final int DIET_VARIETY_STAT = 1140119;

    /** 消息提醒类 end **/

    /**
     * 用户积分类 start
     **/

    /**
     * 通用提醒代码
     */
    public static final int USER_REWARD_UPDATE_LOCK_FAIL = 1150100;

    public static final int USER_REWARD_UPDATE_ERROR = 1150101;


    /** 用户积分类 end **/

    /**
     * 微信 start
     **/

    public static final int WX_JSAPITOCKEN_ERROR = 1160100;

    public static final int WX_TOKEN_SHA_ERROR = 1160101;

    public static final int WX_TOKEN_RESULT = 1160102;

    /** 微信 end **/

    /**
     * 用户评分类 start
     **/

    public static final int USER_SCORE_NULL = 1170100;

    public static final int USER_PRE_SCORE_NULL = 1170101;


    /** 用户评分类 end **/

    /**
     * 人生经历类 start
     **/

    public static final int EXP_ID_NOT_EQUALS = 1180100;

    /** 人生经历类 end **/
}
