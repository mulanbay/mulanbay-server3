package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.ChartType;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 用户图表配置模板
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "chart_template")
public class ChartTemplate implements java.io.Serializable {

    private static final long serialVersionUID = 4564004216708394161L;
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "template_id", unique = true, nullable = false)
    private Long templateId;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "title")
    private String title;

    /**
     * 请求参数,json格式
     */
    @Column(name = "para")
    private String para;

    @Column(name = "status")
    private CommonStatus status;
    @Column(name = "order_index")
    private Short orderIndex;

    //业务类型
    @Column(name = "buss_type")
    private BussType bussType;

    @Column(name = "level")
    private Integer level;

    /**
     * 接口地址
     */
    @Column(name = "api")
    private String api;

    /**
     * 业务跳转地址
     */
    @Column(name = "url")
    private String url;

    /**
     * 时间字段
     * 1. 如果时间条件只有一个字段，则写一个该字段，比如bussDate
     * 2. 如果是区间类型，则两个字段中间用逗号分隔，比如startDate,endDate
     */
    @Column(name = "date_field")
    private String dateField;

    /**
     * 时间格式化，比如:YYYY-MM-DD(与前端时间格式一致)
     */
    @Column(name = "date_format")
    private String dateFormat;

    @Column(name = "chart_type")
    private ChartType chartType;

    @Column(name = "remark")
    private String remark;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time",updatable = false)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time",insertable = false)
    private Date modifyTime;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
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

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDateField() {
        return dateField;
    }

    public void setDateField(String dateField) {
        this.dateField = dateField;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Transient
    public String getChartTypeName() {
        if (chartType != null) {
            return chartType.getName();
        } else {
            return null;
        }
    }

    @Transient
    public String getBussTypeName() {
        return bussType==null ? null:bussType.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ChartTemplate bean) {
            return bean.getTemplateId().equals(this.getTemplateId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(templateId);
    }
}
