package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 书籍分类
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "book_category")
public class BookCategory implements Serializable {

    private static final long serialVersionUID = 4350834742584135205L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "cate_id", unique = true, nullable = false)
    private Long cateId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "cate_name")
    private String cateName;

    @Column(name = "order_index", nullable = false)
    private Short orderIndex;
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

    public Long getCateId() {
        return cateId;
    }

    public void setCateId(Long cateId) {
        this.cateId = cateId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public Short getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Short orderIndex) {
        this.orderIndex = orderIndex;
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
        if (other instanceof BookCategory bean) {
            return bean.getCateId().equals(this.getCateId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cateId);
    }
}
