package cn.mulanbay.pms.persistent.dto.reward;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserRewardSourceStat implements Serializable {

    private Number id;

    //总次数
    private Long totalCount;

    // 总得分
    private BigDecimal totalRewards;

    public UserRewardSourceStat(Number id, Long totalCount, BigDecimal totalRewards) {
        this.id = id;
        this.totalCount = totalCount;
        this.totalRewards = totalRewards;
    }

    public Number getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalRewards() {
        return totalRewards;
    }

    public void setTotalRewards(BigDecimal totalRewards) {
        this.totalRewards = totalRewards;
    }
}
