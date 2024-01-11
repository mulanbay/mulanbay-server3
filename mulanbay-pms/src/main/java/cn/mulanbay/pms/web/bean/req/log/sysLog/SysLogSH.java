package cn.mulanbay.pms.web.bean.req.log.sysLog;

import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.CrossType;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.FunctionDataType;
import cn.mulanbay.pms.persistent.enums.FunctionType;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import cn.mulanbay.pms.persistent.enums.UrlType;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class SysLogSH extends PageSearch implements FullEndDateTime {

    @Query(fieldName = "username,urlAddress,title,content,ipAddress", op = Parameter.Operator.LIKE, crossType = CrossType.OR)
    private String name;

    @Query(fieldName = "paras", op = Parameter.Operator.LIKE)
    private String paras;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    public Long userId;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occurTime", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occurTime", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "sysFunc.funcId", op = Parameter.Operator.EQ)
    private Long funcId;

    @Query(fieldName = "method", op = Parameter.Operator.EQ)
    private String method;

    @Query(fieldName = "sysFunc.urlType", op = Parameter.Operator.EQ)
    private UrlType urlType;

    @Query(fieldName = "sysFunc.funcType", op = Parameter.Operator.EQ)
    private FunctionType funcType;

    @Query(fieldName = "sysFunc.funcDataType", op = Parameter.Operator.EQ)
    private FunctionDataType funcDataType;

    @Query(fieldName = "idValue", op = Parameter.Operator.EQ)
    private String idValue;

    @Query(fieldName = "logLevel", op = Parameter.Operator.EQ)
    private LogLevel logLevel;

    @Query(fieldName = "errorCode", op = Parameter.Operator.EQ)
    private Integer errorCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParas() {
        return paras;
    }

    public void setParas(String paras) {
        this.paras = paras;
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

    public Long getFuncId() {
        return funcId;
    }

    public void setFuncId(Long funcId) {
        this.funcId = funcId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public UrlType getUrlType() {
        return urlType;
    }

    public void setUrlType(UrlType urlType) {
        this.urlType = urlType;
    }

    public FunctionType getFuncType() {
        return funcType;
    }

    public void setFuncType(FunctionType funcType) {
        this.funcType = funcType;
    }

    public FunctionDataType getFuncDataType() {
        return funcDataType;
    }

    public void setFuncDataType(FunctionDataType funcDataType) {
        this.funcDataType = funcDataType;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
