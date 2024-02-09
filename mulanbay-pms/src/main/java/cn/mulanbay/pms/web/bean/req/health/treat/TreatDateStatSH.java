package cn.mulanbay.pms.web.bean.req.health.treat;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.CrossType;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class TreatDateStatSH extends QueryBuilder implements DateStatSH, BindUser {

    @Query(fieldName = "user_id", op = Parameter.Operator.EQ)
    private Long userId;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "treat_time", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "treat_time", op = Parameter.Operator.LTE)
    private Date endDate;

    // 支持多个字段查询
    @Query(fieldName = "hospital,department,organ,disease,confirm_disease", op = Parameter.Operator.LIKE, crossType = CrossType.OR)
    private String name;

    @Query(fieldName = "treat_type", op = Parameter.Operator.EQ)
    private Short treatType;

    //需要统计的费用字段
    private String feeField;

    // 是否补全日期
    private Boolean completeDate;

    private DateGroupType dateGroupType;

    //做统计绘图跟踪使用
    private String disease;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
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
    public DateGroupType getDateGroupType() {
        return dateGroupType;
    }

    public void setDateGroupType(DateGroupType dateGroupType) {
        this.dateGroupType = dateGroupType;
    }

    public Boolean getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Boolean completeDate) {
        this.completeDate = completeDate;
    }

    @Override
    public Boolean isCompleteDate() {
        return this.completeDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeeField() {
        return feeField;
    }

    public void setFeeField(String feeField) {
        this.feeField = feeField;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public Short getTreatType() {
        return treatType;
    }

    public void setTreatType(Short treatType) {
        this.treatType = treatType;
    }
}
