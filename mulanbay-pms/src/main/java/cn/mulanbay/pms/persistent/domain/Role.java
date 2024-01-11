package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.persistent.enums.CommonStatus;
import jakarta.persistence.*;

import java.util.Date;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 角色
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "role")
public class Role implements java.io.Serializable {

    // Fields

    /**
     *
     */
    private static final long serialVersionUID = 961922489014144054L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "role_id", unique = true, nullable = false)
    private Long roleId;

    @Column(name = "role_name", nullable = false, length = 32)
    private String roleName;

    @Column(name = "status", nullable = false)
    private CommonStatus status;

    @Column(name = "order_index", nullable = false)
    private Short orderIndex;

    @Column(name = "remark")
    private String remark;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "modify_time")
    private Date modifyTime;

    // Constructors

    /**
     * default constructor
     */
    public Role() {
    }

    // Property accessors

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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
        if (status != null) {
            return status.getName();
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Role bean) {
            return bean.getRoleId().equals(this.getRoleId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}