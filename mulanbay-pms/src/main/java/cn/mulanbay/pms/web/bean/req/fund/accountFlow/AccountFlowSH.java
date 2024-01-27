package cn.mulanbay.pms.web.bean.req.fund.accountFlow;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.NullType;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.persistent.enums.AccountAdjustType;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class AccountFlowSH extends PageSearch implements BindUser, FullEndDateTime, DateStatSH {

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Query(fieldName = "createdTime", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Query(fieldName = "createdTime", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "adjustType", op = Parameter.Operator.EQ)
    private AccountAdjustType adjustType;

    @Query(fieldName = "account.accountId", op = Parameter.Operator.EQ)
    private Long accountId;

    @Query(fieldName = "status", op = Parameter.Operator.EQ)
    private CommonStatus status;

    @Query(fieldName = "snapshot.snapshotId", op = Parameter.Operator.NULL_NOTNULL)
    private NullType snapshotId;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
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

    public AccountAdjustType getAdjustType() {
        return adjustType;
    }

    public void setAdjustType(AccountAdjustType adjustType) {
        this.adjustType = adjustType;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public NullType getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(NullType snapshotId) {
        this.snapshotId = snapshotId;
    }
}
