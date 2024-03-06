package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.TreatTestResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 检测报告
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "treat_test")
public class TreatTest implements java.io.Serializable {
    private static final long serialVersionUID = 1598040961232804958L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "test_id", unique = true, nullable = false)
    private Long testId;

    @ManyToOne
    @JoinColumn(name = "operation_id")
    private TreatOperation operation;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "unit")
    private String unit;
    //检查结果
    @Column(name = "value")
    private String value;
    //如果检查结果是数字类的minValue，maxValue为参考范围的最小最大值
    @Column(name = "min_value")
    private Double minValue;
    @Column(name = "max_value")
    private Double maxValue;
    //如果检查结果是普通的汉字类，则referScope为参考范围
    @Column(name = "refer_scope")
    private String referScope;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "test_time")
    private Date testTime;
    //结果:-1 过低，0正常 1 过高
    @Column(name = "result")
    private TreatTestResult result;
    //分类/试验方法
    @Column(name = "type_name")
    private String typeName;
    @Column(name = "remark")
    private String remark;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time",updatable = false)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time",insertable = false)
    private Date modifyTime;

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public TreatOperation getOperation() {
        return operation;
    }

    public void setOperation(TreatOperation operation) {
        this.operation = operation;
    }

    public Long getUserId() {
        return userId;
    }

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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Transient
    public String getResultName() {
        return result == null ? null : result.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TreatTest bean) {
            return bean.getTestId().equals(this.getTestId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(testId);
    }
}
