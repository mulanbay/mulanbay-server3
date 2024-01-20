package cn.mulanbay.pms.web.bean.req.consume.consume;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.NullType;
import cn.mulanbay.persistent.query.Parameter.Operator;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import cn.mulanbay.web.bean.request.PageSearch;

import java.util.Date;

public class ConsumeUseTimeStatSH extends PageSearch implements DateStatSH, BindUser, FullEndDateTime {

    private String groupField;

    @Query(fieldName = "buy_time", op = Operator.GTE)
    private Date startDate;

    @Query(fieldName = "buy_time", op = Operator.LTE)
    private Date endDate;

    @Query(fieldName = "user_id", op = Operator.EQ)
    private Long userId;

    @Query(fieldName = "goods_type_id", op = Operator.EQ)
    private Long goodsTypeId;

    @Query(fieldName = "source_id", op = Operator.EQ)
    private Long sourceId;

    @Query(fieldName = "secondhand", op = Operator.EQ)
    private Boolean secondhand;

    /**
     * 非空表示设置过作废日期
     */
    @Query(fieldName = "invalid_time", op = Operator.NULL_NOTNULL)
    private NullType invalidTmeType;

    public String getGroupField() {
        return groupField;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }

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
    public DateGroupType getDateGroupType() {
        return null;
    }

    @Override
    public Boolean isCompleteDate() {
        return null;
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

    public Long getGoodsTypeId() {
        return goodsTypeId;
    }

    public void setGoodsTypeId(Long goodsTypeId) {
        this.goodsTypeId = goodsTypeId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Boolean getSecondhand() {
        return secondhand;
    }

    public void setSecondhand(Boolean secondhand) {
        this.secondhand = secondhand;
    }

    public NullType getInvalidTmeType() {
        return invalidTmeType;
    }

    public void setInvalidTmeType(NullType invalidTmeType) {
        this.invalidTmeType = invalidTmeType;
    }
}
