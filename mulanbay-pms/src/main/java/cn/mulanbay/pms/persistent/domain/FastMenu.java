package cn.mulanbay.pms.persistent.domain;

import jakarta.persistence.*;

import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 快捷菜单
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "fast_menu")
public class FastMenu implements java.io.Serializable {

    // Fields

    /**
     *
     */
    private static final long serialVersionUID = 961922489014144054L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "order_index", nullable = false)
    private Short orderIndex;

    // Constructors

    /**
     * default constructor
     */
    public FastMenu() {
    }


    // Property accessors
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Short getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Short orderIndex) {
        this.orderIndex = orderIndex;
    }
    @Override
    public boolean equals(Object other) {
        if (other instanceof FastMenu bean) {
            return bean.getId().equals(this.getId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}