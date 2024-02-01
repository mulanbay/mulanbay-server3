package cn.mulanbay.pms.web.bean.req.consume.consume;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.CrossType;
import cn.mulanbay.persistent.query.Parameter.Operator;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.ChartType;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import cn.mulanbay.pms.web.bean.req.GroupType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ConsumeAnalyseStatSH extends QueryBuilder implements DateStatSH, BindUser, FullEndDateTime {

    private String groupField;

    private GroupType type;

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

    @Query(fieldName = "goods_type_id", op = Operator.EQ)
    private Long goodsTypeId;

    @Query(fieldName = "source_id", op = Operator.EQ)
    private Long sourceId;

    @Query(fieldName = "tags", op = Operator.LIKE)
    private String tags;

    //sql需要原始的数字类型
    @Query(fieldName = "consume_type", op = Operator.EQ)
    private Short consumeType;

    private boolean stat;

    private ChartType chartType;

    private DateGroupType dateGroupType;

    /**
     * 以商品类型分组时，是否以顶层分组
     */
    private Boolean groupTop;

    public String getGroupField() {
        return groupField;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return dateGroupType;
    }

    public void setDateGroupType(DateGroupType dateGroupType) {
        this.dateGroupType = dateGroupType;
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

    public boolean isStat() {
        return stat;
    }

    public void setStat(boolean stat) {
        this.stat = stat;
    }

    public Short getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(Short consumeType) {
        this.consumeType = consumeType;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public Boolean getGroupTop() {
        return groupTop;
    }

    public void setGroupTop(Boolean groupTop) {
        this.groupTop = groupTop;
    }
}
