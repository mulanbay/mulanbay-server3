package cn.mulanbay.pms.web.bean.req.sport.sport;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SportForm implements BindUser {

    private Long sportId;

    @NotEmpty(message = "名称不能为空")
    private String sportName;
    private Long userId;

    @NotNull(message = "状态不能为空")
    private CommonStatus status;

    @NotEmpty(message = "单位不能为空")
    private String unit;

    @NotNull(message = "排序号不能为空")
    private Short orderIndex;

    private String remark;

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Short getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Short orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
