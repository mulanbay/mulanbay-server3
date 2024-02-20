package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 阅读详情
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "read_detail")
public class ReadDetail implements java.io.Serializable {
    private static final long serialVersionUID = -178426399345959964L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "detail_id", unique = true, nullable = false)
    private Long detailId;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    //阅读日期
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "read_time")
    private Date readTime;

    @Column(name = "duration")
    private Integer duration;
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

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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
        if (other instanceof ReadDetail bean) {
            return bean.getDetailId().equals(this.getDetailId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(detailId);
    }
}
