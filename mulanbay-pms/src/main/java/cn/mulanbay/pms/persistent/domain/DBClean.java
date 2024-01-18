package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.CleanType;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 数据库清理配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "db_clean")
public class DBClean implements java.io.Serializable {

    // Fields

    /**
     *
     */
    private static final long serialVersionUID = 961922489014144054L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * 清理的名称
     */
    @Column(name = "name", nullable = false, length = 32)
    private String name;

    /**
     * 表明
     */
    @Column(name = "table_name")
    private String tableName;

    /**
     * 以哪个字段作为时间判断条件进行清理
     */
    @Column(name = "date_field")
    private String dateField;

    /**
     * 保留天数
     */
    @Column(name = "days")
    private Integer days;

    @Column(name = "clean_type")
    private CleanType cleanType;

    //额外条件
    @Column(name = "extra_condition")
    private String extraCondition;

    @Column(name = "last_clean_time")
    private Date lastCleanTime;

    @Column(name = "last_clean_counts")
    private Integer lastCleanCounts;

    @Column(name = "status")
    private CommonStatus status;

    @Column(name = "order_index")
    private Short orderIndex;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time")
    private Date modifyTime;

    // Constructors

    /**
     * default constructor
     */
    public DBClean() {
    }

    public DBClean(String name, CommonStatus status, Short orderIndex) {
        super();
        this.name = name;
        this.status = status;
        this.orderIndex = orderIndex;
    }

    // Property accessors
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDateField() {
        return dateField;
    }

    public void setDateField(String dateField) {
        this.dateField = dateField;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public CleanType getCleanType() {
        return cleanType;
    }

    public void setCleanType(CleanType cleanType) {
        this.cleanType = cleanType;
    }

    public String getExtraCondition() {
        return extraCondition;
    }

    public void setExtraCondition(String extraCondition) {
        this.extraCondition = extraCondition;
    }

    public Date getLastCleanTime() {
        return lastCleanTime;
    }

    public void setLastCleanTime(Date lastCleanTime) {
        this.lastCleanTime = lastCleanTime;
    }

    public Integer getLastCleanCounts() {
        return lastCleanCounts;
    }

    public void setLastCleanCounts(Integer lastCleanCounts) {
        this.lastCleanCounts = lastCleanCounts;
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

    @Column(name = "created_time", length = 19)
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
    public String getCleanTypeName() {
        if (cleanType != null) {
            return cleanType.getName();
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DBClean bean) {
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