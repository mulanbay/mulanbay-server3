package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BussType;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;


/**
 * 人生档案
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "archive")
public class Archive implements java.io.Serializable {

    private static final long serialVersionUID = 7254329209805899896L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "archive_id", unique = true, nullable = false)
    private Long archiveId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "date")
    private Date date;

    @Column(name = "buss_type")
    private BussType bussType;

    @Column(name = "source_id")
    private Long sourceId;
    @Column(name = "remark")
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time")
    private Date modifyTime;

    public Long getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(Long archiveId) {
        this.archiveId = archiveId;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BussType getBussType() {
        return bussType;
    }

    public void setBussType(BussType bussType) {
        this.bussType = bussType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
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

    @Override
    public boolean equals(Object other) {
        if (other instanceof Archive bean) {
            return bean.getArchiveId().equals(this.getArchiveId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(archiveId);
    }
}
