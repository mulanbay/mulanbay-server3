package cn.mulanbay.pms.web.bean.req.log.operLog;

import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.FunctionType;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

public class OperLogFlowSH extends PageSearch implements FullEndDateTime {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occurEndTime", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occurEndTime", op = Parameter.Operator.LTE)
    private Date endDate;

    @NotEmpty(message = "主键值不能为空")
    @Query(fieldName = "idValue", op = Parameter.Operator.EQ)
    private String idValue;

    /**
     * 直接用sql模式
     */
    @NotEmpty(message = "类名不能为空")
    @Query(fieldName = "sysFunc.beanName", op = Parameter.Operator.EQ)
    private String beanName;

    @NotNull(message = "操作类型不能为空")
    @Query(fieldName = "sysFunc.funcType", op = Parameter.Operator.IN)
    private List<FunctionType> funcTypes;

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

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public List<FunctionType> getFuncTypes() {
        return funcTypes;
    }

    public void setFuncTypes(List<FunctionType> funcTypes) {
        this.funcTypes = funcTypes;
    }
}
