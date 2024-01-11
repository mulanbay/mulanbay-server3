package cn.mulanbay.pms.web.bean.req.log.operLog;

import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.web.bean.request.PageSearch;

import java.util.Date;

public class OperLogStatSH extends PageSearch implements FullEndDateTime {

    private String username;

    @Query(fieldName = "ol.user_id", op = Parameter.Operator.EQ)
    public Long userId;

    @Query(fieldName = "ol.occur_end_time", op = Parameter.Operator.GTE)
    private Date startDate;

    @Query(fieldName = "ol.occur_end_time", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "sf.func_type", op = Parameter.Operator.EQ)
    private Short funcType;

    @Query(fieldName = "sf.func_data_type", op = Parameter.Operator.EQ)
    private Short funcDataType;

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
}
