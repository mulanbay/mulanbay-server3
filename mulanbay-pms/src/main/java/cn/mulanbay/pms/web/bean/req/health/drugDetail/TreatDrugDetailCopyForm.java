package cn.mulanbay.pms.web.bean.req.health.drugDetail;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class TreatDrugDetailCopyForm implements BindUser {

    private Long userId;

    private Long drugId;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "模版不能为空")
    private Date templateDate;

    //开始于结束用药时间
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "开始日期不能为空")
    private Date beginDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "结束日期不能为空")
    private Date endDate;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDrugId() {
        return drugId;
    }

    public void setDrugId(Long drugId) {
        this.drugId = drugId;
    }

    public Date getTemplateDate() {
        return templateDate;
    }

    public void setTemplateDate(Date templateDate) {
        this.templateDate = templateDate;
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
}
