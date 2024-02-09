package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 药品记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "treat_drug")
public class TreatDrug implements java.io.Serializable {
    private static final long serialVersionUID = 1598040961232804958L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "drug_id", unique = true, nullable = false)
    private Long drugId;

    @ManyToOne
    @JoinColumn(name = "treat_id")
    private Treat treat;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "drug_name")
    private String drugName;

    @Column(name = "unit")
    private String unit;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "disease")
    private String disease;

    @Column(name = "unit_price",precision = 9,scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price",precision = 9,scale = 2)
    private BigDecimal totalPrice;
    //每多少天一次
    @Column(name = "per_day")
    private Integer perDay;

    @Column(name = "per_times")
    private Integer perTimes;
    //每次食用的单位
    @Column(name = "eu")
    private String eu;
    //每次食用的量
    @Column(name = "ec")
    private Integer ec;

    @Column(name = "use_way")
    private String useWay;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "treat_date")
    private Date treatDate;
    //开始于结束用药时间
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "begin_date")
    private Date beginDate;
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "end_date")
    private Date endDate;
    //药是否有效
    @Column(name = "available")
    private Boolean available;

    @Column(name = "remind")
    private Boolean remind;
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

    public Long getDrugId() {
        return drugId;
    }

    public void setDrugId(Long drugId) {
        this.drugId = drugId;
    }

    public Treat getTreat() {
        return treat;
    }

    public void setTreat(Treat treat) {
        this.treat = treat;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getPerDay() {
        return perDay;
    }

    public void setPerDay(Integer perDay) {
        this.perDay = perDay;
    }

    public Integer getPerTimes() {
        return perTimes;
    }

    public void setPerTimes(Integer perTimes) {
        this.perTimes = perTimes;
    }

    public String getEu() {
        return eu;
    }

    public void setEu(String eu) {
        this.eu = eu;
    }

    public Integer getEc() {
        return ec;
    }

    public void setEc(Integer ec) {
        this.ec = ec;
    }

    public String getUseWay() {
        return useWay;
    }

    public void setUseWay(String useWay) {
        this.useWay = useWay;
    }

    public Date getTreatDate() {
        return treatDate;
    }

    public void setTreatDate(Date treatDate) {
        this.treatDate = treatDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getRemind() {
        return remind;
    }

    public void setRemind(Boolean remind) {
        this.remind = remind;
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

    @Override
    public boolean equals(Object other) {
        if (other instanceof TreatDrug bean) {
            return bean.getDrugId().equals(this.getDrugId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(drugId);
    }

}
