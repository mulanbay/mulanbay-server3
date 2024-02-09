package cn.mulanbay.pms.web.bean.req.health.drug;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class TreatDrugForm implements BindUser {

    private Long drugId;

    @NotNull(message = "看病编号不能为空")
    private Long treatId;

    private Long userId;

    @NotEmpty(message = "看病名称不能为空")
    private String drugName;

    @NotEmpty(message = "单位不能为空")
    private String unit;

    @NotNull(message = "数量不能为空")
    private Integer amount;

    @NotEmpty(message = "疾病不能为空")
    private String disease;

    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    private BigDecimal totalPrice;
    //每多少天一次
    @NotNull(message = "用法不能为空")
    private Integer perDay;

    @NotNull(message = "次数不能为空")
    private Integer perTimes;

    //每次食用的单位
    //@NotEmpty(message = "{validate.treatDrug.eu.NotEmpty}")
    private String eu;

    //@NotNull(message = "{validate.treatDrug.ec.NotNull}")
    private Integer ec;

    @NotEmpty(message = "使用方式不能为空")
    private String useWay;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "使用方式不能为空")
    private Date treatDate;

    //开始于结束用药时间
    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "开始日期不能为空")
    private Date beginDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "结束日期不能为空")
    private Date endDate;

    //药是否有效
    @NotNull(message = "是否有效不能为空")
    private Boolean available;

    @NotNull(message = "是否提醒不能为空")
    private Boolean remind;

    private String remark;

    public Long getDrugId() {
        return drugId;
    }

    public void setDrugId(Long drugId) {
        this.drugId = drugId;
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
}
