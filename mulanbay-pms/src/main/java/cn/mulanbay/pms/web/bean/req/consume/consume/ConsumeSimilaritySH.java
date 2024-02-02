package cn.mulanbay.pms.web.bean.req.consume.consume;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.CrossType;
import cn.mulanbay.persistent.query.Parameter.Operator;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ConsumeSimilaritySH extends PageSearch implements BindUser, FullEndDateTime {

    @Query(fieldName = "goods_name,tags,shop_name,remark", op = Operator.LIKE, crossType = CrossType.OR)
    private String name;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "buy_time", op = Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "buy_time", op = Operator.LTE)
    private Date endDate;

    @Query(fieldName = "user_id", op = Operator.EQ)
    private Long userId;

    @Query(fieldName = "total_price", op = Operator.GTE)
    private Double startTotalPrice;

    @Query(fieldName = "total_price", op = Operator.LTE)
    private Double endTotalPrice;

    @Query(fieldName = "goodsType.typeId", op = Operator.EQ)
    private Long goodsTypeId;

    @Query(fieldName = "source.sourceId", op = Operator.EQ)
    private Long sourceId;


    @Query(fieldName = "tags", op = Operator.LIKE)
    private String tags;

    //sql需要原始的数字类型
    @Query(fieldName = "consume_type", op = Operator.EQ)
    private Short consumeType;

    private String orderByField;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Short getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(Short consumeType) {
        this.consumeType = consumeType;
    }

    public String getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(String orderByField) {
        this.orderByField = orderByField;
    }
}
