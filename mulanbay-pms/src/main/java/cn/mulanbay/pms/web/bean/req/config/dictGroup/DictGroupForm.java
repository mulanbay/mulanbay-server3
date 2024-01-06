package cn.mulanbay.pms.web.bean.req.config.dictGroup;

import cn.mulanbay.pms.persistent.enums.CommonStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class DictGroupForm {
    private Long groupId;
    @NotEmpty(message = "名称不能为空")
    private String groupName;

    @NotEmpty(message = "代码不能为空")
    private String code;

    @NotNull(message = "状态不能为空")
    private CommonStatus status;

    @NotNull(message = "排序号不能为空")
    private Short orderIndex;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
}
