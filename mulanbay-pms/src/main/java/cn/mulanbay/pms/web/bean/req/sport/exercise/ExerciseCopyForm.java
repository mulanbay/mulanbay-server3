package cn.mulanbay.pms.web.bean.req.sport.exercise;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ExerciseCopyForm implements BindUser {

    private Long userId;

    @NotNull(message = "模版时间不能为空")
    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    private Date templateDate;

    @NotNull(message = "开始时间不能为空")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date beginTime;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getTemplateDate() {
        return templateDate;
    }

    public void setTemplateDate(Date templateDate) {
        this.templateDate = templateDate;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }
}
