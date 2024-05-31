package cn.mulanbay.pms.web.bean.req.report.plan;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.SqlType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class PlanTemplateForm implements BindUser {

    private Long templateId;
    private Long userId;

    @NotEmpty(message = "名称不能为空")
    private String templateName;

    @NotEmpty(message = "标题不能为空")
    private String title;

    @NotNull(message = "SQL类型不能为空")
    private SqlType sqlType;

    @NotEmpty(message = "SQL不能为空")
    private String sqlContent;

    @NotEmpty(message = "单位不能为空")
    private String unit;

    @NotNull(message = "状态不能为空")
    private CommonStatus status;

    @NotNull(message = "排序号不能为空")
    private Short orderIndex;

    @NotNull(message = "参数数量不能为空")
    private Integer paras;

    @NotNull(message = "业务类型不能为空")
    private BussType bussType;

    //等级
    @NotNull(message = "级别不能为空")
    private Integer level;

    //奖励积分(正为加，负为减)
    @NotNull(message = "奖励积分不能为空")
    private Integer rewards;

    private BussSource source;
    private String calendarTitle;

    private String url;

    private String remark;

    /**
     * 复制时的模版ID
     */
    private Long fromTemplateId;

    /**
     * 新增时是否拷贝配置项
     */
    private Boolean copyItems;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    public void setSqlType(SqlType sqlType) {
        this.sqlType = sqlType;
    }

    public String getSqlContent() {
        return sqlContent;
    }

    public void setSqlContent(String sqlContent) {
        this.sqlContent = sqlContent;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    public Integer getParas() {
        return paras;
    }

    public void setParas(Integer paras) {
        this.paras = paras;
    }

    public BussType getBussType() {
        return bussType;
    }

    public void setBussType(BussType bussType) {
        this.bussType = bussType;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getRewards() {
        return rewards;
    }

    public void setRewards(Integer rewards) {
        this.rewards = rewards;
    }

    public BussSource getSource() {
        return source;
    }

    public void setSource(BussSource source) {
        this.source = source;
    }

    public String getCalendarTitle() {
        return calendarTitle;
    }

    public void setCalendarTitle(String calendarTitle) {
        this.calendarTitle = calendarTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getFromTemplateId() {
        return fromTemplateId;
    }

    public void setFromTemplateId(Long fromTemplateId) {
        this.fromTemplateId = fromTemplateId;
    }

    public Boolean getCopyItems() {
        return copyItems;
    }

    public void setCopyItems(Boolean copyItems) {
        this.copyItems = copyItems;
    }
}
