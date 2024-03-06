package cn.mulanbay.pms.web.bean.req.behavior;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.web.bean.request.PageSearch;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class UserBehaviorCalendarSH extends PageSearch implements BindUser, FullEndDateTime {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "开始日期不能为空")
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "结束日期不能为空")
    private Date endDate;

    private String name;

    public Long userId;

    private BussType bussType;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BussType getBussType() {
        return bussType;
    }

    public void setBussType(BussType bussType) {
        this.bussType = bussType;
    }
}
