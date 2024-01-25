package cn.mulanbay.pms.web.bean.req.consume.consume;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.CrossType;
import cn.mulanbay.persistent.query.Parameter.Operator;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.web.bean.request.PageSearch;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ConsumeWordCloudSH extends PageSearch implements BindUser, FullEndDateTime {

    @Query(fieldName = "goodsType.TypeId", op = Operator.EQ)
    private Long goodsTypeId;

    @Query(fieldName = "source.sourceId", op = Operator.EQ)
    private Long sourceId;

    @Query(fieldName = "goodsName,tags,shopName,remark", op = Operator.LIKE, crossType = CrossType.OR)
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Query(fieldName = "buyTime", op = Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Query(fieldName = "buyTime", op = Operator.LTE)
    private Date endDate;

    @Query(fieldName = "secondhand", op = Operator.EQ)
    private Boolean secondhand;

    @Query(fieldName = "userId", op = Operator.EQ)
    private Long userId;

    @Query(fieldName = "tags", op = Operator.LIKE)
    private String tags;

    private String field;

    /**
     * 是否忽略短语
     */
    private Boolean ignoreShort;

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

    public Boolean getSecondhand() {
        return secondhand;
    }

    public void setSecondhand(Boolean secondhand) {
        this.secondhand = secondhand;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Boolean getIgnoreShort() {
        return ignoreShort;
    }

    public void setIgnoreShort(Boolean ignoreShort) {
        this.ignoreShort = ignoreShort;
    }
}
