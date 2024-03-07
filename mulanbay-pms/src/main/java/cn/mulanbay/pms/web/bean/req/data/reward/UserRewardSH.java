package cn.mulanbay.pms.web.bean.req.data.reward;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.ReferType;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class UserRewardSH extends PageSearch implements BindUser, FullEndDateTime {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "createdTime", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "createdTime", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    @Query(fieldName = "source", op = Parameter.Operator.EQ)
    private BussSource source;

    @Query(fieldName = "rewards", op = Parameter.Operator.REFER, referFieldName = "rewardsCompareType", referType = ReferType.OP_REFER)
    private Integer rewards;

    @Query(fieldName = "sourceId", op = Parameter.Operator.EQ)
    private Long sourceId;

    //奖励积分的比较类型（大于，还是小于）
    private Parameter.Operator rewardsCompareType;

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

    public BussSource getSource() {
        return source;
    }

    public void setSource(BussSource source) {
        this.source = source;
    }

    public Integer getRewards() {
        return rewards;
    }

    public void setRewards(Integer rewards) {
        this.rewards = rewards;
    }

    public Parameter.Operator getRewardsCompareType() {
        return rewardsCompareType;
    }

    public void setRewardsCompareType(Parameter.Operator rewardsCompareType) {
        this.rewardsCompareType = rewardsCompareType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }
}
