package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.StatValueClass;
import jakarta.persistence.*;

import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;


/**
 * 数据字典项
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "dict_item")
public class DictItem implements java.io.Serializable {

    // Fields

    /**
     *
     */
    private static final long serialVersionUID = 961922489014144054L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "item_id", unique = true, nullable = false)
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private DictGroup group;

    @Column(name = "item_name", nullable = false, length = 45)
    private String itemName;
    //子分类使用，可为空
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "value_class")
    private StatValueClass valueClass;

    @Column(name = "status", nullable = false)
    private CommonStatus status;

    @Column(name = "order_index", nullable = false)
    private Short orderIndex;

    // Constructors

    /**
     * default constructor
     */
    public DictItem() {
    }

    // Property accessors

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public DictGroup getGroup() {
        return group;
    }

    public void setGroup(DictGroup group) {
        this.group = group;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public StatValueClass getValueClass() {
        return valueClass;
    }

    public void setValueClass(StatValueClass valueClass) {
        this.valueClass = valueClass;
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
        if (other instanceof DictItem bean) {
            return bean.getItemId().equals(this.getItemId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(itemId);
    }
}