package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.persistent.enums.CommonStatus;
import jakarta.persistence.*;

import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 数据字典组
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "dict_group")
public class DictGroup implements java.io.Serializable {

    // Fields

    /**
     *
     */
    private static final long serialVersionUID = 961922489014144054L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "group_id", unique = true, nullable = false)
    private Long groupId;

    @Column(name = "group_name", nullable = false, length = 32)
    private String groupName;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "status", nullable = false)
    private CommonStatus status;

    @Column(name = "order_index", nullable = false)
    private Short orderIndex;

    // Constructors

    /**
     * default constructor
     */
    public DictGroup() {
    }


    // Property accessors


    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    @Transient
    public String getStatusName() {
        if (status != null) {
            return status.getName();
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DictGroup bean) {
            return bean.getGroupId().equals(this.getGroupId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(groupId);
    }
}