package cn.mulanbay.pms.web.bean.req.commonData;

import cn.mulanbay.common.aop.BindUser;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class CommonDataForm implements BindUser {

    private Long dataId;
    private Long userId;

    @NotNull(message = "类型不能为空")
    private Long typeId;

    @NotEmpty(message = "名称不能为空")
    private String dataName;

    @NotNull(message = "数值不能为空")
    private Integer value;

    @NotNull(message = "发生时间不能为空")
    private Date occurTime;

    private String location;
    private String remark;

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
