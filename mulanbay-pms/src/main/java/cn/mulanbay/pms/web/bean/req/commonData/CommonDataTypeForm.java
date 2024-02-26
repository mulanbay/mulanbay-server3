package cn.mulanbay.pms.web.bean.req.commonData;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.CommonStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CommonDataTypeForm implements BindUser {

    private Long typeId;

    @NotEmpty(message = "名称不能为空")
    private String typeName;

    private Long userId;

    @NotEmpty(message = "组名不能为空")
    private String groupName;

    @NotEmpty(message = "单位不能为空")
    private String unit;

    //加入月分析
    @NotNull(message = "加入月分析不能为空")
    private Boolean monthStat;

    @NotNull(message = "加入年分析不能为空")
    private Boolean yearStat;

    // 加入八小时之外统计
    @NotNull(message = "加入八小时之外统计不能为空")
    private Boolean overWorkStat;

    @NotNull(message = "状态不能为空")
    private CommonStatus status;

    @NotNull(message = "排序号不能为空")
    private Short orderIndex;

    @NotNull(message = "积分奖励不能为空")
    private Integer rewardPoint;

    private String remark;

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getMonthStat() {
        return monthStat;
    }

    public void setMonthStat(Boolean monthStat) {
        this.monthStat = monthStat;
    }

    public Boolean getYearStat() {
        return yearStat;
    }

    public void setYearStat(Boolean yearStat) {
        this.yearStat = yearStat;
    }

    public Boolean getOverWorkStat() {
        return overWorkStat;
    }

    public void setOverWorkStat(Boolean overWorkStat) {
        this.overWorkStat = overWorkStat;
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

    public Integer getRewardPoint() {
        return rewardPoint;
    }

    public void setRewardPoint(Integer rewardPoint) {
        this.rewardPoint = rewardPoint;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
