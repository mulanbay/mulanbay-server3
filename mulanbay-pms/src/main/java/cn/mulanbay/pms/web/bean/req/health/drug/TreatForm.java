package cn.mulanbay.pms.web.bean.req.health.drug;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.TreatStage;
import cn.mulanbay.pms.persistent.enums.TreatType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public class TreatForm implements BindUser {

    private Long treatId;
    private Long userId;
    @NotNull(message = "类型不能为空")
    private TreatType treatType;
    //医院
    @NotEmpty(message = "医院不能为空")
    private String hospital;
    // 科室
    @NotEmpty(message = "科室不能为空")
    private String department;
    //器官
    @NotEmpty(message = "器官不能为空")
    private String organ;
    // 疾病
    @NotEmpty(message = "疾病症状不能为空")
    private String disease;
    // 疼痛级别(1-10)
    @NotNull(message = "疼痛级别不能为空")
    private Integer painLevel;
    // 重要等级(0-5)
    @NotNull(message = "重要性不能为空")
    private Double important;
    // 确诊疾病
    //@NotEmpty(message = "{validate.treatRecord.diagnosedDisease.notEmpty}")
    private String confirmDisease;
    // 是否有病
    @NotNull(message = "是否有病不能为空")
    private Boolean sick;
    // 看病日期
    @NotNull(message = "看病日期不能为空")
    private Date treatTime;
    // 门诊类型
    @NotEmpty(message = "门诊类型不能为空")
    private String os;
    //医生名字
    private String doctor;
    // 挂号费（原价:个人+医保）
    @NotNull(message = "挂号费不能为空")
    private BigDecimal regFee;
    // 药费（原价:个人+医保）
    @NotNull(message = "药费不能为空")
    private BigDecimal drugFee;
    // 手术费用（原价:个人+医保）
    @NotNull(message = "手术费用不能为空")
    private BigDecimal operationFee;
    // 总共花费
    @NotNull(message = "总共花费不能为空")
    private BigDecimal totalFee;
    // 医保花费
    @NotNull(message = "医保花费不能为空")
    private BigDecimal miFee;
    // 个人支付费用
    @NotNull(message = "个人支付费用不能为空")
    private BigDecimal pdFee;
    private String tags;
    //门诊阶段
    @NotNull(message = "门诊阶段不能为空")
    private TreatStage stage;
    private String remark;

    //同步到消费记录
    private Boolean syncToConsume=true;

    public Long getTreatId() {
        return treatId;
    }

    public void setTreatId(Long treatId) {
        this.treatId = treatId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TreatType getTreatType() {
        return treatType;
    }

    public void setTreatType(TreatType treatType) {
        this.treatType = treatType;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public Integer getPainLevel() {
        return painLevel;
    }

    public void setPainLevel(Integer painLevel) {
        this.painLevel = painLevel;
    }

    public Double getImportant() {
        return important;
    }

    public void setImportant(Double important) {
        this.important = important;
    }

    public String getConfirmDisease() {
        return confirmDisease;
    }

    public void setConfirmDisease(String confirmDisease) {
        this.confirmDisease = confirmDisease;
    }

    public Boolean getSick() {
        return sick;
    }

    public void setSick(Boolean sick) {
        this.sick = sick;
    }

    public Date getTreatTime() {
        return treatTime;
    }

    public void setTreatTime(Date treatTime) {
        this.treatTime = treatTime;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public BigDecimal getRegFee() {
        return regFee;
    }

    public void setRegFee(BigDecimal regFee) {
        this.regFee = regFee;
    }

    public BigDecimal getDrugFee() {
        return drugFee;
    }

    public void setDrugFee(BigDecimal drugFee) {
        this.drugFee = drugFee;
    }

    public BigDecimal getOperationFee() {
        return operationFee;
    }

    public void setOperationFee(BigDecimal operationFee) {
        this.operationFee = operationFee;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public BigDecimal getMiFee() {
        return miFee;
    }

    public void setMiFee(BigDecimal miFee) {
        this.miFee = miFee;
    }

    public BigDecimal getPdFee() {
        return pdFee;
    }

    public void setPdFee(BigDecimal pdFee) {
        this.pdFee = pdFee;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public TreatStage getStage() {
        return stage;
    }

    public void setStage(TreatStage stage) {
        this.stage = stage;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getSyncToConsume() {
        return syncToConsume;
    }

    public void setSyncToConsume(Boolean syncToConsume) {
        this.syncToConsume = syncToConsume;
    }
}
