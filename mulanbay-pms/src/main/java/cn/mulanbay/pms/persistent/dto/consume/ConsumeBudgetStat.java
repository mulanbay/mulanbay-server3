package cn.mulanbay.pms.persistent.dto.consume;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: TODO(一句话描述该类的功能)
 * @Author: fenghong
 * @Create : 2020/12/13 16:38
 */
public class ConsumeBudgetStat {

    private BigDecimal totalPrice;

    private Date maxConsumeDate;

    public ConsumeBudgetStat(BigDecimal totalPrice, Date maxConsumeDate) {
        this.totalPrice = totalPrice;
        this.maxConsumeDate = maxConsumeDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getMaxConsumeDate() {
        return maxConsumeDate;
    }

    public void setMaxConsumeDate(Date maxConsumeDate) {
        this.maxConsumeDate = maxConsumeDate;
    }
}
