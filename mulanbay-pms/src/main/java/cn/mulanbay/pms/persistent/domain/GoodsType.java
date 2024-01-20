package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 商品类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "goods_type")
public class GoodsType implements java.io.Serializable {

    // Fields

    /**
     *
     */
    private static final long serialVersionUID = 8575214853538973501L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "type_id", unique = true, nullable = false)
    private Long typeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "pid")
    private Long pid;

    @Column(name = "type_name", nullable = false)
    private String typeName;

    @Column(name = "behavior_name")
    private String behaviorName;

    @Column(name = "status", nullable = false)
    private CommonStatus status;

    @Column(name = "order_index", nullable = false)
    private Short orderIndex;

    // 是否加入统计
    @Column(name = "stat", nullable = false)
    private Boolean stat;

    //标签，目前用于AI自动匹配
    @Column(name = "tags")
    private String tags;

    @Column(name = "remark")
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time")
    private Date modifyTime;

    // Constructors

    // Property accessors


    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getBehaviorName() {
        return behaviorName;
    }

    public void setBehaviorName(String behaviorName) {
        this.behaviorName = behaviorName;
    }

    public CommonStatus getStatus() {
        return this.status;
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

    public Boolean getStat() {
        return stat;
    }

    public void setStat(Boolean stat) {
        this.stat = stat;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    /**
     * 默认为有,前端tree可以展开
     * @return
     */
    @Transient
    public Boolean getHasChildren() {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof GoodsType bean) {
            return bean.getTypeId().equals(this.getTypeId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(typeId);
    }
}