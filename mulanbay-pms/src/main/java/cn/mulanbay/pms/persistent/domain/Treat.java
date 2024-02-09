package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.TreatStage;
import cn.mulanbay.pms.persistent.enums.TreatType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 看病记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "treat")
public class Treat implements java.io.Serializable {
    private static final long serialVersionUID = 4680164191879433281L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "treat_id", unique = true, nullable = false)
    private Long treatId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "treat_type")
    private TreatType treatType;
    //医院
    @Column(name = "hospital")
    private String hospital;

    // 科室
    @Column(name = "department")
    private String department;
    //器官
    @Column(name = "organ")
    private String organ;
    // 疾病症状
    @Column(name = "disease")
    private String disease;
    // 疼痛级别(1-10)
    @Column(name = "pain_level")
    private Integer painLevel;
    // 重要等级(0-5)
    @Column(name = "important")
    private Double important;
    // 确诊疾病
    @Column(name = "confirm_disease")
    private String confirmDisease;
    // 是否有病
    @Column(name = "sick")
    private Boolean sick;
    // 看病日期
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "treat_time")
    private Date treatTime;
    // 门诊类型(普通、专家、急诊)
    @Column(name = "os")
    private String os;
    //医生名字
    @Column(name = "doctor")
    private String doctor;
    // 挂号费（原价:个人+医保）
    @Column(name = "reg_fee",precision = 9,scale = 2)
    private BigDecimal regFee;
    // 药费（原价:个人+医保）
    @Column(name = "drug_fee",precision = 9,scale = 2)
    private BigDecimal drugFee;
    // 手术费用（原价:个人+医保）
    @Column(name = "operation_fee",precision = 9,scale = 2)
    private BigDecimal operationFee;
    // 总共花费
    @Column(name = "total_fee",precision = 9,scale = 2)
    private BigDecimal totalFee;
    // 医保花费
    @Column(name = "mi_fee",precision = 9,scale = 2)
    private BigDecimal miFee;
    // 个人支付费用
    @Column(name = "pd_fee",precision = 9,scale = 2)
    private BigDecimal pdFee;

    @Column(name = "tags")
    private String tags;
    //门诊阶段
    @Column(name = "stage")
    private TreatStage stage;
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

    public Long getTreatId() {
        return treatId;
    }

    public void setTreatId(Long treatId) {
        this.treatId = treatId;
    }

    public Long getUserId() {
        return userId;
    }

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
    public String getTreatTypeName() {
        return treatType == null ? null : treatType.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Treat bean) {
            return bean.getTreatId().equals(this.getTreatId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(treatId);
    }

}
