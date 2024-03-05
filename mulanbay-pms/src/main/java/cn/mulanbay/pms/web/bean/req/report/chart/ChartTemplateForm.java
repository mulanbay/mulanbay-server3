package cn.mulanbay.pms.web.bean.req.report.chart;

import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.ChartType;
import cn.mulanbay.pms.persistent.enums.CommonStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ChartTemplateForm {


    private Long templateId;
    private Long userId;

    @NotEmpty(message = "名称不能为空")
    private String templateName;

    @NotEmpty(message = "标题不能为空")
    private String title;

    @NotEmpty(message = "查询参数不能为空")
    private String para;

    @NotNull(message = "状态不能为空")
    private CommonStatus status;

    @NotNull(message = "排序号不能为空")
    private Short orderIndex;

    @NotNull(message = "业务类型不能为空")
    private BussType bussType;

    //等级
    @NotNull(message = "级别不能为空")
    private Integer level;

    //奖励积分(正为加，负为减)
    @NotNull(message = "图表类型不能为空")
    private ChartType chartType;

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

    public Long getUserId() {
        return userId;
    }

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

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
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

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
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
