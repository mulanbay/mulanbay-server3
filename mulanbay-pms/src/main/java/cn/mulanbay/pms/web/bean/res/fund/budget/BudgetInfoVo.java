package cn.mulanbay.pms.web.bean.res.fund.budget;

import cn.mulanbay.pms.persistent.domain.Budget;

import java.math.BigDecimal;

public class BudgetInfoVo extends Budget {

    /**
     * 系数
     */
    private int factor;

    /**
     * 乘以系数后的值
     */
    private BigDecimal sumAmount;

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    public BigDecimal getSumAmount() {
        return sumAmount;
    }

    public void setSumAmount(BigDecimal sumAmount) {
        this.sumAmount = sumAmount;
    }
}
