package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 用户提醒配置模板
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "stat_template")
public class StatTemplate implements java.io.Serializable {

    private static final long serialVersionUID = 5577140811957103873L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "template_id", unique = true, nullable = false)
    private Long templateId;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "title")
    private String title;

    @Column(name = "sql_type")
    private SqlType sqlType;

    @Column(name = "sql_content")
    private String sqlContent;

    @Column(name = "result_type")
    private ResultType resultType;

    @Column(name = "value_type")
    private ValueType valueType;

    @Column(name = "status")
    private CommonStatus status;

    @Column(name = "order_index")
    private Short orderIndex;

    //业务类型
    @Column(name = "buss_type")
    private BussType bussType;

    @Column(name = "level")
    private Integer level;
    //奖励积分(正为加，负为减)
    @Column(name = "rewards")
    private Integer rewards;

    @Column(name = "buss_key")
    private String bussKey;

    @Column(name = "calendar_title")
    private String calendarTitle;
    //链接地址
    @Column(name = "url")
    private String url;

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

    public ResultType getResultType() {
        return resultType;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
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

    public Integer getRewards() {
        return rewards;
    }

    public void setRewards(Integer rewards) {
        this.rewards = rewards;
    }

    public String getBussKey() {
        return bussKey;
    }

    public void setBussKey(String bussKey) {
        this.bussKey = bussKey;
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
    public String getStatusName() {
        return status==null ? null:status.getName();
    }

    @Transient
    public String getSqlTypeName() {
        return sqlType==null ? null:sqlType.getName();
    }

    @Transient
    public String getValueTypeName() {
        return valueType==null ? null:valueType.getName();
    }

    @Transient
    public String getResultTypeName() {
        return resultType==null ? null:resultType.getName();
    }

    @Transient
    public String getBussTypeName() {
        return bussType==null ? null:bussType.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StatTemplate bean) {
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
