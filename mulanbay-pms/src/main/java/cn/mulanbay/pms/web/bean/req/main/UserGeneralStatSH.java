package cn.mulanbay.pms.web.bean.req.main;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class UserGeneralStatSH implements BindUser {

    private Long userId;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    private Date date;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
