package cn.mulanbay.pms.common;

import java.math.RoundingMode;

/**
 * Created by fenghong on 2017/2/27.
 * 常量
 */
public class Constant {

    /**
     * 正常的http响应码
     */
    public static final int SC_OK = 200;

    public static final String DATE_FORMAT = "yyyy-MM-dd";// 24

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";// 24

    /**
     * 默认评分组
     */
    public static Long DEFAULT_SCORE_GROUP_ID=1L;

    /**
     * 管理员
     */
    public static long ADMIN_USER_ID=0L;

    /**
     * 根节点号
     */
    public static long ROOT_ID=0L;

    /**
     * 预算周期前缀
     */
    public static final String BUDGET_PERIOD_PX = "p";// 24

    /**
     * 小数的精度
     */
    public static final int SCALE = 2;// 24

    /**
     * 小数进位规则
     */
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * 默认的用户等级
     */
    public static final int USER_LEVEL=3;

    /**
     * 最大季度
     */
    public static final int MAX_QUARTERLY = 4;

    /**
     * 最大月份数
     */
    public static final int MAX_MONTH = 12;

    /**
     * 最大天数
     */
    public static final int MAX_DAY = 366;

    /**
     * 一个月最大周数
     */
    public static final int MAX_MONTH_WEEK = 4;

    /**
     * 一个月最大天数
     */
    public static final int MAX_MONTH_DAY = 31;

    /**
     * 最大周数
     */
    public static final int MAX_WEEK = 53;

    /**
     * 每月的工作天数
     */
    public static final double MONTH_WORK_DAY = 20.83;

    /**
     * 每周的工作天数
     */
    public static final double WEEK_WORK_DAY = 5;

    /**
     * 每年的工作天数
     */
    public static final double YEAR_WORK_DAY = 250;

    /**
     * 价格符号
     */
    public static final String MONEY_SYMBOL = "￥";

    /**
     * 每周天数
     */
    public static final int DAYS_WEEK = 7;

    /**
     * 小时
     */
    public static final int MAX_HOUR = 24;

    /**
     * 附加查询条件替换字符
     */
    public static final String EXTRA_SQL_RPC = "{extra_sql}";

}
