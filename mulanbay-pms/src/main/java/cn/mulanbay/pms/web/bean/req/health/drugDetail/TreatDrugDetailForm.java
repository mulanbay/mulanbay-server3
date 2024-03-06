package cn.mulanbay.pms.web.bean.req.health.drugDetail;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class TreatDrugDetailForm implements BindUser {

    private Long detailId;

    @NotNull(message = "药品编号不能为空")
    private Long drugId;
    private Long userId;
    //用药时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @NotNull(message = "用药日期不能为空")
    private Date occurTime;

    //实际食用的单位
    private String eu;
    //实际食用的量，可能半颗
    private Double ec;

    private String remark;

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getDrugId() {
        return drugId;
    }

    public void setDrugId(Long drugId) {
        this.drugId = drugId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }

    public String getEu() {
        return eu;
    }

    public void setEu(String eu) {
        this.eu = eu;
    }

    public Double getEc() {
        return ec;
    }

    public void setEc(Double ec) {
        this.ec = ec;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
