package cn.mulanbay.pms.web.bean.res.fund.budget;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.domain.Budget;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

public class BudgetInfoVo extends Budget {

    //发生的时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date occurDate;

    //实际普通消费金额
    private BigDecimal ncAmount;
    //实际突发消费金额
    private BigDecimal bcAmount;
    //实际看病消费金额
    private BigDecimal trAmount;
    //有些花费可以直接与消费记录挂钩
    private Long consumeId;

    //跟实际需要实现的还剩余几天
    private Integer leftDays;

    //时间的比例值，比如每天的在月预算里是30，在年预算里是365
    private int drate = 1;

    public Date getOccurDate() {
        return occurDate;
    }

    public void setOccurDate(Date occurDate) {
        this.occurDate = occurDate;
    }

    public BigDecimal getNcAmount() {
        return ncAmount;
    }

    public void setNcAmount(BigDecimal ncAmount) {
        this.ncAmount = ncAmount;
    }

    public BigDecimal getBcAmount() {
        return bcAmount;
    }

    public void setBcAmount(BigDecimal bcAmount) {
        this.bcAmount = bcAmount;
    }

    public BigDecimal getTrAmount() {
        return trAmount;
    }

    public void setTrAmount(BigDecimal trAmount) {
        this.trAmount = trAmount;
    }

    public Long getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(Long consumeId) {
        this.consumeId = consumeId;
    }

    public Integer getLeftDays() {
        return leftDays;
    }

    public void setLeftDays(Integer leftDays) {
        this.leftDays = leftDays;
    }

    public int getDrate() {
        return drate;
    }

    public void setDrate(int drate) {
        this.drate = drate;
    }
}
