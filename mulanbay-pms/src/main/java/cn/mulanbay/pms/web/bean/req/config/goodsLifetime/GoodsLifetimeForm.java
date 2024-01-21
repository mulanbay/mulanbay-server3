package cn.mulanbay.pms.web.bean.req.config.goodsLifetime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class GoodsLifetimeForm  {

    private Long lifetimeId;

    @NotEmpty(message = "名称不能为空")
    private String lifetimeName;

    @NotEmpty(message = "标签不能为空")
    private String tags;

    @NotNull(message = "时长不能为空")
    private Integer days;

    private String remark;

    public Long getLifetimeId() {
        return lifetimeId;
    }

    public void setLifetimeId(Long lifetimeId) {
        this.lifetimeId = lifetimeId;
    }

    public String getLifetimeName() {
        return lifetimeName;
    }

    public void setLifetimeName(String lifetimeName) {
        this.lifetimeName = lifetimeName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
