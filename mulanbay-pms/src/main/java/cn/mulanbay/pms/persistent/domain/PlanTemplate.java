package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.SqlType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 用户计划配置模板
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "plan_template")
public class PlanTemplate implements java.io.Serializable {
    private static final long serialVersionUID = 3222686239313264692L;
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

    /**
     * 计划值的单位，这里的值只是在配置用户计划时默认加载使用，实际还是根据UserPlan取里面的unit来
     * 因为比如运动锻炼，跑步的数值单位是公里，而平板支撑的数值单位是分钟
     */
    @Column(name = "unit")
    private String unit;

    @Column(name = "status")
    private CommonStatus status;

    @Column(name = "order_index")
    private Short orderIndex;

    @Column(name = "paras")
    private Integer paras;

    //业务类型
    @Column(name = "buss_type")
    private BussType bussType;

    @Column(name = "level")
    private Integer level;
    //奖励积分(正为加，负为减)
    @Column(name = "rewards")
    private Integer rewards;

    /**
     * 业务来源，该key目前作为日历自动更新的匹配类型
     * 业务逻辑：UserPlan、UserStat、UserCalendar都可以使用这个bussKey来匹配某个日历是否为同一种类型
     * 比如：UserPlan定义了用户消费限制计划，UserStat定义消费限制统计，都生成到UserCalendar中
     *      在定时任务中检查时，任意的一个业务查询到已经完成，就可以更新这个日历为已经完成
     */
    @Column(name = "source")
    private BussSource source;

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
        return status==null? null:status.getName();
    }

    @Transient
    public String getSqlTypeName() {
        return sqlType==null? null:sqlType.getName();
    }

    @Transient
    public String getBussTypeName() {
        return bussType==null ? null:bussType.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PlanTemplate bean) {
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
