package cn.mulanbay.pms.web.bean.req.music.instrument;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class InstrumentForm implements BindUser {

    private Long instrumentId;

    @NotEmpty(message = "名称不能为空")
    private String instrumentName;

    private Long userId;

    @NotNull(message = "状态不能为空")
    private CommonStatus status;

    @NotNull(message = "排序号不能为空")
    private Short orderIndex;

    private String remark;

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Long instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
