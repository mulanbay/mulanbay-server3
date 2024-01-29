package cn.mulanbay.pms.web.bean.req.fund.budget;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.PeriodType;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BudgetAnalyseSH implements BindUser {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "时间不能为空")
    private Date date;

    public Long userId;

    private Long userBehaviorId;

    private PeriodType period;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PeriodType getPeriod() {
        return period;
    }

    public void setPeriod(PeriodType period) {
        this.period = period;
    }

    public Long getUserBehaviorId() {
        return userBehaviorId;
    }

    public void setUserBehaviorId(Long userBehaviorId) {
        this.userBehaviorId = userBehaviorId;
    }

}
