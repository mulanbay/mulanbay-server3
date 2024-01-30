package cn.mulanbay.pms.web.bean.req.consume.goodsType;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class GoodsTypeForm implements BindUser {

    private Long typeId;

    @NotNull(message = "父级代码不能为空")
    private Long pid;

    @NotEmpty(message = "名称不能为空")
    private String typeName;

    //@NotEmpty(message = "{validate.goodsType.behaviorName.notEmpty}")
    private String behaviorName;

    private Long userId;

    @NotNull(message = "状态不能为空")
    private CommonStatus status;

    @NotNull(message = "排序号不能为空")
    @Min(value = 0, message = "排序号需要大于等于0")
    private Short orderIndex;

    // 是否加入统计
    @NotNull(message = "请选择是否需要统计")
    private Boolean stat;

    private String tags;

    private String remark;

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getBehaviorName() {
        return behaviorName;
    }

    public void setBehaviorName(String behaviorName) {
        this.behaviorName = behaviorName;
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

    public Short getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Short orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Boolean getStat() {
        return stat;
    }

    public void setStat(Boolean stat) {
        this.stat = stat;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
