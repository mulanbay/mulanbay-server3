package cn.mulanbay.pms.web.bean.req.report.stat;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.web.bean.request.PageSearch;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class UserStatTimelineStatSH extends PageSearch implements BindUser {

    @NotNull(message = "统计编号不能为空")
    @Query(fieldName = "stat.statId", op = Parameter.Operator.EQ)
    private Long statId;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "createdTime", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "createdTime", op = Parameter.Operator.LTE)
    private Date endDate;

    public Long getStatId() {
        return statId;
    }

    public void setStatId(Long statId) {
        this.statId = statId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

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
}
