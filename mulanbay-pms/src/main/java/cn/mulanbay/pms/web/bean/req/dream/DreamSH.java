package cn.mulanbay.pms.web.bean.req.dream;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DreamSH extends PageSearch implements BindUser, FullEndDateTime {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "startDate", op = Parameter.Operator.GTE, referFieldName = "dateQueryType")
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "startDate", op = Parameter.Operator.LTE, referFieldName = "dateQueryType")
    private Date endDate;

    @Query(fieldName = "rate", op = Parameter.Operator.GTE)
    private Integer minRate;

    @Query(fieldName = "rate", op = Parameter.Operator.LTE)
    private Integer maxRate;

    @Query(fieldName = "important", op = Parameter.Operator.GTE)
    private Integer minImportant;

    @Query(fieldName = "important", op = Parameter.Operator.LTE)
    private Integer maxImportant;

    @Query(fieldName = "dreamName", op = Parameter.Operator.LIKE)
    private String name;

    @Query(fieldName = "status", op = Parameter.Operator.IN)
    private String statuses;

    private String dateQueryType;

    private String sortField;

    private String sortType;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    public Long userId;

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

    public Integer getMinRate() {
        return minRate;
    }

    public void setMinRate(Integer minRate) {
        this.minRate = minRate;
    }

    public Integer getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(Integer maxRate) {
        this.maxRate = maxRate;
    }

    public Integer getMinImportant() {
        return minImportant;
    }

    public void setMinImportant(Integer minImportant) {
        this.minImportant = minImportant;
    }

    public Integer getMaxImportant() {
        return maxImportant;
    }

    public void setMaxImportant(Integer maxImportant) {
        this.maxImportant = maxImportant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatuses() {
        return statuses;
    }

    public void setStatuses(String statuses) {
        this.statuses = statuses;
    }

    public String getDateQueryType() {
        return dateQueryType;
    }

    public void setDateQueryType(String dateQueryType) {
        this.dateQueryType = dateQueryType;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
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
