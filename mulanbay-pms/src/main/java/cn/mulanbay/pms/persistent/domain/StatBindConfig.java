package cn.mulanbay.pms.persistent.domain;


import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 所有报表、计划、提醒类型的sql中值的选择在这里配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "stat_bind_config")
public class StatBindConfig implements java.io.Serializable {

    private static final long serialVersionUID = 882330393814155329L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "config_id", unique = true, nullable = false)
    private Long configId;

    @Column(name = "config_name")
    private String configName;

    @Column(name = "type")
    private StatBussType type;

    @Column(name = "source")
    private StatValueSource source;

    @Column(name = "value_class")
    private StatValueClass valueClass;

    /**
     * 绑定的外键ID
     */
    @Column(name = "fid")
    private Long fid;

    /**
     * 具体的值，比如说起来，枚举类，字典组代码
     */
    @Column(name = "config_value")
    private String configValue;

    //当source为枚举类时有效
    @Column(name = "enum_id_type")
    private EnumIdType enumIdType;

    //是否记录由上层来决定
    @Column(name = "cascade_type")
    private CasCadeType casCadeType;

    /**
     * SQL类型有效
     */
    @Column(name = "bind_user")
    private Boolean bindUser;

    /**
     * 是否树形结构，SQL和JSON类型有效
     */
    @Column(name = "tree")
    private Boolean tree;

    /**
     * 表单的字段名
     */
    @Column(name = "form_field")
    private String formField;

    /**
     * 默认值
     */
    @Column(name = "default_value")
    private String defaultValue;

    /**
     * 是否可为空
     */
    @Column(name = "nullable")
    private Boolean nullable;

    @Column(name = "order_index")
    private Integer orderIndex;

    //没有配置时，提示信息
    @Column(name = "msg")
    private String msg;

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

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public StatBussType getType() {
        return type;
    }

    public void setType(StatBussType type) {
        this.type = type;
    }

    public StatValueSource getSource() {
        return source;
    }

    public void setSource(StatValueSource source) {
        this.source = source;
    }

    public StatValueClass getValueClass() {
        return valueClass;
    }

    public void setValueClass(StatValueClass valueClass) {
        this.valueClass = valueClass;
    }

    public Long getFid() {
        return fid;
    }

    public void setFid(Long fid) {
        this.fid = fid;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public EnumIdType getEnumIdType() {
        return enumIdType;
    }

    public void setEnumIdType(EnumIdType enumIdType) {
        this.enumIdType = enumIdType;
    }

    public CasCadeType getCasCadeType() {
        return casCadeType;
    }

    public void setCasCadeType(CasCadeType casCadeType) {
        this.casCadeType = casCadeType;
    }

    public Boolean getBindUser() {
        return bindUser;
    }

    public void setBindUser(Boolean bindUser) {
        this.bindUser = bindUser;
    }

    public Boolean getTree() {
        return tree;
    }

    public void setTree(Boolean tree) {
        this.tree = tree;
    }

    public String getFormField() {
        return formField;
    }

    public void setFormField(String formField) {
        this.formField = formField;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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
    public String getTypeName() {
        return type==null ? null:type.getName();
    }

    @Transient
    public String getCasCadeTypeName() {
        return casCadeType==null ? null:casCadeType.getName();
    }

    @Transient
    public String getSourceName() {
        return source==null ? null:source.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StatBindConfig bean) {
            return bean.getConfigId().equals(this.getConfigId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(configId);
    }
}
