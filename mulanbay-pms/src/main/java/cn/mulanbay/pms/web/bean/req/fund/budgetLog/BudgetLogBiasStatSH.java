package cn.mulanbay.pms.web.bean.req.fund.budgetLog;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.NullType;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BudgetLogBiasStatSH extends QueryBuilder implements DateStatSH, BindUser, FullEndDateTime {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "bussDay", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "bussDay", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    @Query(fieldName = "statPeriod", op = Parameter.Operator.EQ)
    private PeriodType statPeriod;

    @Query(fieldName = "accountChangeAmount", op = Parameter.Operator.NULL_NOTNULL)
    private NullType accountChangeAmount;

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

    public PeriodType getStatPeriod() {
        return statPeriod;
    }

    public void setStatPeriod(PeriodType statPeriod) {
        this.statPeriod = statPeriod;
    }

    public NullType getAccountChangeAmount() {
        return accountChangeAmount;
    }

    public void setAccountChangeAmount(NullType accountChangeAmount) {
        this.accountChangeAmount = accountChangeAmount;
    }
}
