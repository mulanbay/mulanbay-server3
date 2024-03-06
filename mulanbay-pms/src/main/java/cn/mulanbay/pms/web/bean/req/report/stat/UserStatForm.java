package cn.mulanbay.pms.web.bean.req.report.stat;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.BindUserLevel;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.CompareType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class UserStatForm implements BindUser, BindUserLevel {

    private Long statId;

    @NotEmpty(message = "标题不能为空")
    private String title;
    private Long userId;

    @NotNull(message = "模版不能为空")
    private Long templateId;

    private String bindValues;

    @NotNull(message = "是否提醒不能为空")
    private Boolean remind;

    @NotNull(message = "期望值不能为空")
    private Long expectValue;

    @NotNull(message = "比较类型不能为空")
    private CompareType compareType;

    @NotNull(message = "状态不能为空")
    private CommonStatus status;

    @NotNull(message = "排序号不能为空")
    private Short orderIndex;

    private String calendarTitle;

    private String calendarTime;

    private String unit;

    private String remark;

    //绑定的用户等级
    private Integer level;

    public Long getStatId() {
        return statId;
    }

    public void setStatId(Long statId) {
        this.statId = statId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getBindValues() {
        return bindValues;
    }

    public void setBindValues(String bindValues) {
        this.bindValues = bindValues;
    }

    public Boolean getRemind() {
        return remind;
    }

    public void setRemind(Boolean remind) {
        this.remind = remind;
    }

    public Long getExpectValue() {
        return expectValue;
    }

    public void setExpectValue(Long expectValue) {
        this.expectValue = expectValue;
    }

    public CompareType getCompareType() {
        return compareType;
    }

    public void setCompareType(CompareType compareType) {
        this.compareType = compareType;
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

    public String getCalendarTitle() {
        return calendarTitle;
    }

    public void setCalendarTitle(String calendarTitle) {
        this.calendarTitle = calendarTitle;
    }

    public String getCalendarTime() {
        return calendarTime;
    }

    public void setCalendarTime(String calendarTime) {
        this.calendarTime = calendarTime;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getLevel() {
        return level;
    }

    @Override
    public void setLevel(Integer level) {
        this.level = level;
    }
}
