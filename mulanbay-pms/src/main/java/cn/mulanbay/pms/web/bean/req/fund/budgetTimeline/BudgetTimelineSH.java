package cn.mulanbay.pms.web.bean.req.fund.budgetTimeline;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BudgetTimelineSH implements BindUser {

    @NotNull(message = "周期不能为空")
    private PeriodType statPeriod;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    private Date bussDay;

    public Long userId;

    /**
     * 是否包含突然的临时性消费
     * 如果选择预测，那么包含突发消费的数据不会准确
     */
    private Boolean needOutBurst = false;

    /**
     * 按值还是按进度
     */
    private StatType statType;

    /**
     * 是否要预测
     */
    private Boolean predict = false;

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

    public Boolean getNeedOutBurst() {
        return needOutBurst;
    }

    public void setNeedOutBurst(Boolean needOutBurst) {
        this.needOutBurst = needOutBurst;
    }

    public StatType getStatType() {
        return statType;
    }

    public void setStatType(StatType statType) {
        this.statType = statType;
    }

    public Boolean getPredict() {
        return predict;
    }

    public void setPredict(Boolean predict) {
        this.predict = predict;
    }

    public enum StatType {
        RATE, VALUE
    }
}
