package cn.mulanbay.pms.web.bean.req.behavior;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.SqlType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class BehaviorTemplateForm implements BindUser {

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

    /**
     * 额外条件
     */
    private String extraSql;

    /**
     * 日期区段，比如旅行虽然只有开始日期，其实是跨好几天时间，根据统计出的天数加时间
     */
    @NotNull(message = "日期区段不能为空")
    private Boolean dateRegion;

    @NotNull(message = "状态不能为空")
    private CommonStatus status;

    //加入月分析
    @NotNull(message = "月分析不能为空")
    private Boolean monthStat;

    //加入天分析
    @NotNull(message = "天分析不能为空")
    private Boolean dayStat;
    //加入小时分析
    @NotNull(message = "小时分析不能为空")
    private Boolean hourStat;

    @NotNull(message = "全天日历不能为空")
    private Boolean allDay;

    /**
     * 计划值的单位，这里的值只是在配置用户计划时默认加载使用，实际还是根据UserPlan取里面的unit来
     * 因为比如运动锻炼，跑步的数值单位是公里，而平板支撑的数值单位是分钟
     */
    @NotEmpty(message = "单位不能为空")
    private String unit;

    @NotNull(message = "排序号不能为空")
    private Short orderIndex;

    @NotNull(message = "参数数量不能为空")
    private Integer paras;

    //业务类型
    @NotNull(message = "业务类型不能为空")
    private BussType bussType;

    @NotNull(message = "级别不能为空")
    private Integer level;

    @NotNull(message = "来源不能为空")
    private BussSource source;

    //链接地址
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

    public String getExtraSql() {
        return extraSql;
    }

    public void setExtraSql(String extraSql) {
        this.extraSql = extraSql;
    }

    public Boolean getDateRegion() {
        return dateRegion;
    }

    public void setDateRegion(Boolean dateRegion) {
        this.dateRegion = dateRegion;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public Boolean getMonthStat() {
        return monthStat;
    }

    public void setMonthStat(Boolean monthStat) {
        this.monthStat = monthStat;
    }

    public Boolean getDayStat() {
        return dayStat;
    }

    public void setDayStat(Boolean dayStat) {
        this.dayStat = dayStat;
    }

    public Boolean getHourStat() {
        return hourStat;
    }

    public void setHourStat(Boolean hourStat) {
        this.hourStat = hourStat;
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
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

    public BussSource getSource() {
        return source;
    }

    public void setSource(BussSource source) {
        this.source = source;
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
