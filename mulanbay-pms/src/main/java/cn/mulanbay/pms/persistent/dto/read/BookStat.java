package cn.mulanbay.pms.persistent.dto.read;

import cn.mulanbay.common.util.NumberUtil;

import java.math.BigDecimal;

public class BookStat {

    private Long totalCount;

    private BigDecimal totalCostDays;

    public BookStat() {
    }

    public BookStat(Long totalCount, BigDecimal totalCostDays) {
        this.totalCount = totalCount;
        this.totalCostDays = totalCostDays;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalCostDays() {
        return totalCostDays;
    }

    public void setTotalCostDays(BigDecimal totalCostDays) {
        this.totalCostDays = totalCostDays;
    }

    /**
     * 获取平均花费天数
     *
     * @return
     */
    public double getAvgCostDays() {
        if (totalCount == null || totalCount.intValue() == 0) {
            return 0;
        } else if (totalCostDays == null || totalCostDays.doubleValue() <= 0) {
            return 0;
        } else {
            return NumberUtil.getAvg(totalCostDays.doubleValue(), totalCount.intValue(), 1);
        }
    }

}
