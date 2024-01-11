package cn.mulanbay.pms.web.bean.req.log.operLog;

import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class OperLogDateStatSH extends PageSearch implements DateStatSH,FullEndDateTime {

    private String username;

    @Query(fieldName = "ol.user_id", op = Parameter.Operator.EQ)
    public Long userId;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "ol.occur_end_time", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "ol.occur_end_time", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "sf.func_type", op = Parameter.Operator.EQ)
    private Short funcType;

    @Query(fieldName = "sf.func_data_type", op = Parameter.Operator.EQ)
    private Short funcDataType;

    private DateGroupType dateGroupType;

    // 是否补全日期
    private Boolean completeDate;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

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
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Short getFuncType() {
        return funcType;
    }

    public void setFuncType(Short funcType) {
        this.funcType = funcType;
    }

    public Short getFuncDataType() {
        return funcDataType;
    }

    public void setFuncDataType(Short funcDataType) {
        this.funcDataType = funcDataType;
    }

    @Override
    public DateGroupType getDateGroupType() {
        return dateGroupType;
    }

    @Override
    public Boolean isCompleteDate() {
        return completeDate;
    }

    public void setDateGroupType(DateGroupType dateGroupType) {
        this.dateGroupType = dateGroupType;
    }

    public Boolean getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Boolean completeDate) {
        this.completeDate = completeDate;
    }
}
