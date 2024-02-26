package cn.mulanbay.pms.web.bean.req.commonData;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.QueryBuilder;

import cn.mulanbay.pms.common.Constant;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class CommonDataTimelineSH extends QueryBuilder implements BindUser, FullEndDateTime {

    @NotNull(message = "类型不能为空")
    private Long typeId;

    private String name;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    private Date endDate;

    public Long userId;

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}
