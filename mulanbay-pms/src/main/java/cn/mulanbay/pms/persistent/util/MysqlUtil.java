package cn.mulanbay.pms.persistent.util;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.enums.DateGroupType;

/**
 * mysql字段拼装处理
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
public class MysqlUtil {

    /**
     * 时间函数
     *
     * @param field
     * @param dateGroupType
     * @return
     */
    public static String dateTypeMethod(String field, DateGroupType dateGroupType) {
        String method = null;
        switch (dateGroupType){
            case HOUR:
                method = "hour";
                break;
            case DAYOFMONTH:
                //在这个月中的那一天
                method = "day";
                break;
            case DAYOFWEEK:
                //星期索引，周一=1，周二=2，周日=7
                return " WEEKDAY(" + field + ")+1 ";
            case WEEK:
                method = "weekofyear";
                break;
            case MONTH:
                method = "month";
                break;
            case YEAR:
                return " CAST(DATE_FORMAT(" + field + ",'%Y') AS signed) ";
            case DAY,DAYCALENDAR:
                return " CAST(DATE_FORMAT(" + field + ",'%Y%m%d') AS signed) ";
            case YEARMONTH:
                return " CAST(DATE_FORMAT(" + field + ",'%Y%m') AS signed) ";
            case HOURMINUTE:
                //这里是除以100，不是60，比如：7.5代表的是7点50分，而不是7点半
                return "(CAST(DATE_FORMAT(" + field + ",'%H') AS signed)+CAST(DATE_FORMAT(" + field + ",'%i') AS signed)/100)";
            default:
                throw new ApplicationException(PmsCode.UN_SUPPORT_DATE_GROUP_TYPE);
        }
        return " " + method + "(" + field + ") ";
    }
}
