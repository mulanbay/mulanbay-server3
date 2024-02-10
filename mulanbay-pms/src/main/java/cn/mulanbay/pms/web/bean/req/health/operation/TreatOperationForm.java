package cn.mulanbay.pms.web.bean.req.health.operation;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public class TreatOperationForm implements BindUser {

    private Long operationId;

    @NotNull(message = "看病记录编号不能为空")
    private Long treatId;

    private Long userId;

    @NotEmpty(message = "手术名称不能为空")
    private String operationName;

    private String category;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "看病日期不能为空")
    private Date treatDate;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    private Date reviewDate;

    @NotNull(message = "费用不能为空")
    private BigDecimal fee;
    //药是否有效

    @NotNull(message = "是否有效不能为空")
    private Boolean available;
    // 是否有病
    @NotNull(message = "是否有病不能为空")
    private Boolean sick;

    private String remark;

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

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
}
