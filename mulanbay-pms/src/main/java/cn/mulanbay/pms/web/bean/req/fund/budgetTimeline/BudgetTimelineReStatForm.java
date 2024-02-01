package cn.mulanbay.pms.web.bean.req.fund.budgetTimeline;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BudgetTimelineReStatForm implements BindUser {

    @NotNull(message = "周期不能为空")
    private PeriodType statPeriod;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date bussDay;

    public Long userId;

    public PeriodType getStatPeriod() {
        return statPeriod;
    }

    public void setStatPeriod(PeriodType statPeriod) {
        this.statPeriod = statPeriod;
    }

    public Date getBussDay() {
        return bussDay;
    }

    public void setBussDay(Date bussDay) {
        this.bussDay = bussDay;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
