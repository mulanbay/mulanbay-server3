package cn.mulanbay.pms.web.bean.req.health.sleep;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.ChartType;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.SleepStatType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class SleepAnalyseStatSH extends QueryBuilder implements DateStatSH, BindUser {


    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "sleep_date", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "sleep_date", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "user_id", op = Parameter.Operator.EQ)
    private Long userId;

    private DateGroupType groupType;

    private SleepStatType valueType;

    private ChartType chartType;

    public DateGroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(DateGroupType groupType) {
        this.groupType = groupType;
    }

    public SleepStatType getValueType() {
        return valueType;
    }

    public void setValueType(SleepStatType valueType) {
        this.valueType = valueType;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public DateGroupType getDateGroupType() {
        return groupType;
    }

    @Override
    public Boolean isCompleteDate() {
        return null;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }
}
