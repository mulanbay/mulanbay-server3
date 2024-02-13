package cn.mulanbay.pms.web.bean.req.health.body;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BodyInfoSH extends PageSearch implements BindUser, FullEndDateTime {

    @Query(fieldName = "weight", op = Parameter.Operator.GTE)
    private Integer startWeight;

    @Query(fieldName = "weight", op = Parameter.Operator.LTE)
    private Integer endWeight;

    @Query(fieldName = "height", op = Parameter.Operator.GTE)
    private Integer startHeight;

    @Query(fieldName = "height", op = Parameter.Operator.LTE)
    private Integer endHeight;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "recordTime", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "recordTime", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    public Integer getStartWeight() {
        return startWeight;
    }

    public void setStartWeight(Integer startWeight) {
        this.startWeight = startWeight;
    }

    public Integer getEndWeight() {
        return endWeight;
    }

    public void setEndWeight(Integer endWeight) {
        this.endWeight = endWeight;
    }

    public Integer getEndHeight() {
        return endHeight;
    }

    public void setEndHeight(Integer endHeight) {
        this.endHeight = endHeight;
    }

    public Integer getStartHeight() {
        return startHeight;
    }

    public void setStartHeight(Integer startHeight) {
        this.startHeight = startHeight;
    }

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
}
