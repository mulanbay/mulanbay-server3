package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.enums.UserCalendarFinishType;
import cn.mulanbay.pms.persistent.enums.UserCalendarSource;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 用户日历
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "user_calendar")
public class UserCalendar implements java.io.Serializable {

    private static final long serialVersionUID = 8213897467891411864L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "calendar_id", unique = true, nullable = false)
    private Long calendarId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    //延迟次数
    @Column(name = "delays")
    private Integer delays;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "buss_day")
    private Date bussDay;

    //失效时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "expire_time")
    private Date expireTime;

    //唯一key
    @Column(name = "buss_identity_key")
    private String bussIdentityKey;

    @Column(name = "source_type")
    private UserCalendarSource sourceType;

    @Column(name = "source_id")
    private Long sourceId;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "finish_time")
    private Date finishTime;

    @Column(name = "finish_type")
    private UserCalendarFinishType finishType;

    /**
     * 自动生成时的来源消息ID
     */
    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "finish_source")
    private UserCalendarSource finishSource;

    //完成的源ID
    @Column(name = "finish_source_id")
    private Long finishSourceId;

    @Column(name = "finish_message_id")
    private Long finishMessageId;


    //是否为全天任务
    @Column(name = "all_day")
    private Boolean allDay;

    //手动设置有用
    @Column(name = "location")
    private String location;

    @Column(name = "read_only")
    private Boolean readOnly;

    @Column(name = "period")
    private PeriodType period;

    /**
     * 针对重复循环的有效
     */
    @Column(name = "period_values")
    private String periodValues;

    /**
     * 以模板新增的，可以查询是否完成的判断
     * 这里不需要多对一设置
     * 判断是否完成以bussIdentityKey来判断
     */
    @Column(name = "template_id")
    private Long templateId;

    //用户自己选择的值，以模板新增的，可以查询是否完成的判断
    @Column(name = "bind_values")
    private String bindValues;

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

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getDelays() {
        return delays;
    }

    public void setDelays(Integer delays) {
        this.delays = delays;
    }

    public Date getBussDay() {
        return bussDay;
    }

    public void setBussDay(Date bussDay) {
        this.bussDay = bussDay;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getBussIdentityKey() {
        return bussIdentityKey;
    }

    public void setBussIdentityKey(String bussIdentityKey) {
        this.bussIdentityKey = bussIdentityKey;
    }

    public UserCalendarSource getSourceType() {
        return sourceType;
    }

    public void setSourceType(UserCalendarSource sourceType) {
        this.sourceType = sourceType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public UserCalendarFinishType getFinishType() {
        return finishType;
    }

    public void setFinishType(UserCalendarFinishType finishType) {
        this.finishType = finishType;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public UserCalendarSource getFinishSource() {
        return finishSource;
    }

    public void setFinishSource(UserCalendarSource finishSource) {
        this.finishSource = finishSource;
    }

    public Long getFinishSourceId() {
        return finishSourceId;
    }

    public void setFinishSourceId(Long finishSourceId) {
        this.finishSourceId = finishSourceId;
    }

    public Long getFinishMessageId() {
        return finishMessageId;
    }

    public void setFinishMessageId(Long finishMessageId) {
        this.finishMessageId = finishMessageId;
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public PeriodType getPeriod() {
        return period;
    }

    public void setPeriod(PeriodType period) {
        this.period = period;
    }

    public String getPeriodValues() {
        return periodValues;
    }

    public void setPeriodValues(String periodValues) {
        this.periodValues = periodValues;
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
    public String getSourceTypeName() {
        return sourceType == null ? null : sourceType.getName();
    }

    @Transient
    public int getSourceTypeIndex() {
        return sourceType == null ? 0 : sourceType.getValue();
    }

    @Transient
    public String getFinishTypeName() {
        return finishType == null ? null : finishType.getName();
    }

    @Transient
    public String getPeriodName() {
        return period == null ? null : period.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof UserCalendar bean) {
            return bean.getCalendarId().equals(this.getCalendarId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(calendarId);
    }
}
