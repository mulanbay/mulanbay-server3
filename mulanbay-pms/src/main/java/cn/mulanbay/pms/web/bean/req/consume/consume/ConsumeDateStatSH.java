package cn.mulanbay.pms.web.bean.req.consume.consume;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import cn.mulanbay.pms.web.bean.req.GroupType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ConsumeDateStatSH extends QueryBuilder implements DateStatSH, BindUser, FullEndDateTime {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Query(fieldName = "buy_time", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Query(fieldName = "buy_time", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "user_id", op = Parameter.Operator.EQ)
    public Long userId;

    @Query(fieldName = "secondhand", op = Parameter.Operator.EQ)
    private Boolean secondhand;

    @Query(fieldName = "total_price", op = Parameter.Operator.GTE)
    private Double startTotalPrice;

    @Query(fieldName = "total_price", op = Parameter.Operator.LTE)
    private Double endTotalPrice;

    @Query(fieldName = "goods_type_id", op = Parameter.Operator.EQ)
    private Long goodsType;

    @Query(fieldName = "source_id", op = Parameter.Operator.EQ)
    private Long sourceId;

    //sql需要原始的数字类型
    @Query(fieldName = "consume_type", op = Parameter.Operator.EQ)
    private Short consumeType;

    private DateGroupType dateGroupType;

    private GroupType groupType;

    // 是否补全日期
    private Boolean completeDate;

    @Override
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

    public Boolean getSecondhand() {
        return secondhand;
    }

    public void setSecondhand(Boolean secondhand) {
        this.secondhand = secondhand;
    }

    public Double getStartTotalPrice() {
        return startTotalPrice;
    }

    public void setStartTotalPrice(Double startTotalPrice) {
        this.startTotalPrice = startTotalPrice;
    }

    public Double getEndTotalPrice() {
        return endTotalPrice;
    }

    public void setEndTotalPrice(Double endTotalPrice) {
        this.endTotalPrice = endTotalPrice;
    }

    public Long getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Long goodsType) {
        this.goodsType = goodsType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Short getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(Short consumeType) {
        this.consumeType = consumeType;
    }

    @Override
    public DateGroupType getDateGroupType() {
        return dateGroupType;
    }

    @Override
    public Boolean isCompleteDate() {
        return completeDate;
    }

    public void setDateGroupType(DateGroupType dateGroupType) {
        this.dateGroupType = dateGroupType;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }

    public Boolean getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Boolean completeDate) {
        this.completeDate = completeDate;
    }
}
