package cn.mulanbay.pms.web.bean.req.health.drug;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class TreatDrugCalendarSH implements BindUser {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    private Date bussDay;

    public Long userId;

    public Date getBussDay() {
        return bussDay;
    }

    public void setBussDay(Date bussDay) {
        this.bussDay = bussDay;
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
