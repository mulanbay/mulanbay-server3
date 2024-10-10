package cn.mulanbay.pms.web.bean.req.life.experience;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.ExperienceCostStatType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ExperienceCostStatSH extends QueryBuilder implements FullEndDateTime, BindUser {

    @Query(fieldName = "exp_id", op = Parameter.Operator.EQ)
    private Long expId;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "start_date", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "start_date", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "user_id", op = Parameter.Operator.EQ)
    public Long userId;

    private ExperienceCostStatType statType;

    /**
     * 以商品类型分组时，是否以顶层分组
     */
    private Boolean groupTop;

    public Long getExpId() {
        return expId;
    }

    public void setExpId(Long expId) {
        this.expId = expId;
    }

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

    public ExperienceCostStatType getStatType() {
        return statType;
    }

    public void setStatType(ExperienceCostStatType statType) {
        this.statType = statType;
    }

    public Boolean getGroupTop() {
        return groupTop;
    }

    public void setGroupTop(Boolean groupTop) {
        this.groupTop = groupTop;
    }
}
