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
 * 手术
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "treat_operation")
public class TreatOperation implements java.io.Serializable {
    private static final long serialVersionUID = 8012764840429490409L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "operation_id", unique = true, nullable = false)
    private Long operationId;

    @ManyToOne
    @JoinColumn(name = "treat_id")
    private Treat treat;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "operation_name")
    private String operationName;
    //分类名：比如B超下面有很多的检查
    @Column(name = "category")
    private String category;
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "treat_date")
    private Date treatDate;
    //复查
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "review_date")
    private Date reviewDate;
    @Column(name = "fee",precision = 9,scale = 2)
    private BigDecimal fee;
    //药是否有效
    @Column(name = "available")
    private Boolean available;
    // 是否有病
    @Column(name = "sick")
    private Boolean sick;
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

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
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

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getTreatDate() {
        return treatDate;
    }

    public void setTreatDate(Date treatDate) {
        this.treatDate = treatDate;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getSick() {
        return sick;
    }

    public void setSick(Boolean sick) {
        this.sick = sick;
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
        if (other instanceof TreatOperation bean) {
            return bean.getOperationId().equals(this.getOperationId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(operationId);
    }

}
