package cn.mulanbay.pms.web.bean.req.health.test;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.TreatTestResult;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class TreatTestSH extends PageSearch implements FullEndDateTime, BindUser {

    @Query(fieldName = "operation.treat.tags", op = Parameter.Operator.EQ)
    private String tags;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "testTime", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "testTime", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    @Query(fieldName = "operation.operationId", op = Parameter.Operator.EQ)
    private Long operationId;

    @Query(fieldName = "name", op = Parameter.Operator.LIKE)
    private String name;

    @Query(fieldName = "result", op = Parameter.Operator.EQ)
    private TreatTestResult result;

    private String sort = "desc";

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TreatTestResult getResult() {
        return result;
    }

    public void setResult(TreatTestResult result) {
        this.result = result;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
