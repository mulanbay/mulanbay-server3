package cn.mulanbay.pms.web.bean.req.health.test;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.TreatTestResult;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class TreatTestForm implements BindUser {

    private Long testId;

    @NotNull(message = "手术编号不能为空")
    private Long operationId;

    private Long userId;

    @NotEmpty(message = "检验名称不能为空")
    private String name;

    private String unit;

    @NotEmpty(message = "测试值不能为空")
    private String value;

    private Double minValue;

    private Double maxValue;

    private String referScope;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @NotNull(message = "测试日期不能为空")
    private Date testTime;

    //空为自动计算
    private TreatTestResult result;

    private String typeName;

    private String remark;

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public String getReferScope() {
        return referScope;
    }

    public void setReferScope(String referScope) {
        this.referScope = referScope;
    }

    public Date getTestTime() {
        return testTime;
    }

    public void setTestTime(Date testTime) {
        this.testTime = testTime;
    }

    public TreatTestResult getResult() {
        return result;
    }

    public void setResult(TreatTestResult result) {
        this.result = result;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
