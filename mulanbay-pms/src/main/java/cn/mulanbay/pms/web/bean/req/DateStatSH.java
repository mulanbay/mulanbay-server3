package cn.mulanbay.pms.web.bean.req;

import cn.mulanbay.pms.persistent.enums.DateGroupType;

import java.util.Date;

/**
 * 时间统计类的基类
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public interface DateStatSH {

    public Date getStartDate();

    public Date getEndDate();

    public DateGroupType getDateGroupType();

    /**
     * 是否补全日期
     * 针对统计时横坐标数据没有安装正常日期连续时
     * @return
     */
    public Boolean isCompleteDate();

}
