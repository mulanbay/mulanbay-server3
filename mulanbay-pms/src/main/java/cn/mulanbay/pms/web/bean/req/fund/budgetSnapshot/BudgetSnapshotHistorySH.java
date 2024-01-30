package cn.mulanbay.pms.web.bean.req.fund.budgetSnapshot;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BudgetSnapshotHistorySH extends PageSearch implements BindUser {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    private Date endDate;

    @Query(fieldName = "bussKey", op = Parameter.Operator.GTE)
    private String startBussKey;

    @Query(fieldName = "bussKey", op = Parameter.Operator.GTE)
    private String endBussKey;

    @Query(fieldName = "fromId", op = Parameter.Operator.EQ)
    private Long budgetId;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    private boolean needChart;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStartBussKey() {
        return startBussKey;
    }

    public void setStartBussKey(String startBussKey) {
        this.startBussKey = startBussKey;
    }

    public String getEndBussKey() {
        return endBussKey;
    }

    public void setEndBussKey(String endBussKey) {
        this.endBussKey = endBussKey;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isNeedChart() {
        return needChart;
    }

    public void setNeedChart(boolean needChart) {
        this.needChart = needChart;
    }
}
