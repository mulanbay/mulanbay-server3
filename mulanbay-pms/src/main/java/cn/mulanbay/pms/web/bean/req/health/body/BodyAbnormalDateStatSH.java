package cn.mulanbay.pms.web.bean.req.health.body;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.CrossType;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BodyAbnormalDateStatSH extends QueryBuilder implements DateStatSH, BindUser, FullEndDateTime {

    // 支持多个字段查询
    @Query(fieldName = "organ,disease", op = Parameter.Operator.LIKE, crossType = CrossType.OR)
    private String name;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occur_date", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occur_date", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "user_id", op = Parameter.Operator.EQ)
    public Long userId;

    @NotNull(message = "分组类型不能为空")
    private DateGroupType dateGroupType;

    // 是否补全日期
    @NotNull(message = "是否补全日期不能为空")
    private Boolean completeDate;

    //做统计绘图跟踪使用
    private String disease;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCompleteDate() {
        return completeDate;
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
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public DateGroupType getDateGroupType() {
        return dateGroupType;
    }

    public void setDateGroupType(DateGroupType dateGroupType) {
        this.dateGroupType = dateGroupType;
    }

    @Override
    public Boolean isCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Boolean compliteDate) {
        this.completeDate = compliteDate;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }
}
