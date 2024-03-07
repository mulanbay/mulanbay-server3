package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.SqlType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 用户行为配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "behavior_template")
public class BehaviorTemplate implements java.io.Serializable {
    private static final long serialVersionUID = -5625649846678266289L;

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
     * 额外条件
     */
    @Column(name = "extra_sql")
    private String extraSql;

    /**
     * 日期区段，比如旅行虽然只有开始日期，其实是跨好几天时间，根据统计出的天数加时间
     */
    @Column(name = "date_region")
    private Boolean dateRegion;

    @Column(name = "status")
    private CommonStatus status;

    //加入月分析
    @Column(name = "month_stat")
    private Boolean monthStat;

    //加入天分析
    @Column(name = "day_stat")
    private Boolean dayStat;
    //加入小时分析
    @Column(name = "hour_stat")
    private Boolean hourStat;

    //全天日历数据
    @Column(name = "all_day")
    private Boolean allDay;
    /**
     * 计划值的单位，这里的值只是在配置用户计划时默认加载使用，实际还是根据UserPlan取里面的unit来
     * 因为比如运动锻炼，跑步的数值单位是公里，而平板支撑的数值单位是分钟
     */
    @Column(name = "unit")
    private String unit;

    @Column(name = "order_index")
    private Short orderIndex;

    //业务类型
    @Column(name = "buss_type")
    private BussType bussType;

    @Column(name = "level")
    private Integer level;

    @Column(name = "source")
    private BussSource source;

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
    public String getBussTypeName() {
        return bussType==null ? null:bussType.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof BehaviorTemplate bean) {
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
