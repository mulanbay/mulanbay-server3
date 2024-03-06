package cn.mulanbay.pms.web.bean.req.health.body;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BodyAbnormalGroupType;
import cn.mulanbay.pms.persistent.enums.ChartType;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BodyAbnormalStatSH extends QueryBuilder implements DateStatSH, BindUser, FullEndDateTime {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occur_date", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occur_date", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "user_id", op = Parameter.Operator.EQ)
    private Long userId;

    private BodyAbnormalGroupType groupField;

    @NotNull(message = "图表类型不能为空")
    private ChartType chartType;

    //检索使用
    private String name;

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
        return null;
    }

    @Override
    public Boolean isCompleteDate() {
        return null;
    }

    @Override
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

    public BodyAbnormalGroupType getGroupField() {
        return groupField;
    }

    public void setGroupField(BodyAbnormalGroupType groupField) {
        this.groupField = groupField;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
