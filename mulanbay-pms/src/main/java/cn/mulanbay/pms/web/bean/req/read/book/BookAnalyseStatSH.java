package cn.mulanbay.pms.web.bean.req.read.book;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter.Operator;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BookAnalyseStatSH extends QueryBuilder implements DateStatSH, BindUser, FullEndDateTime {

    private GroupType groupType;

    @Query(fieldName = "name", op = Operator.LIKE)
    private String name;

    private String dateQueryType;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "finish_date", op = Operator.GTE, referFieldName = "dateQueryType")
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "finish_date", op = Operator.LTE, referFieldName = "dateQueryType")
    private Date endDate;

    @Query(fieldName = "user_id", op = Operator.EQ)
    private Long userId;

    //因为是sql查询
    @Query(fieldName = "status", op = Operator.EQ)
    private Integer status;

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }

    public String getDateQueryType() {
        return dateQueryType;
    }

    public void setDateQueryType(String dateQueryType) {
        this.dateQueryType = dateQueryType;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public enum GroupType {
        BOOK_CATEGORY("cate_id"),
        BOOK_TYPE("book_type"),
        LANGUAGE("language"),
        SCORE("score"),
        STATUS("status"),
        PUBLISH_YEAR("publish_year"),
        PRESS("press"),
        COUNTRY("country_id"),
        //完成天数，开始时间--结束时间
        PERIOD("period"),
        //花费的时间
        TIME("time"),
        //来源
        SOURCE("source");

        private String fieldName;

        GroupType(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}
