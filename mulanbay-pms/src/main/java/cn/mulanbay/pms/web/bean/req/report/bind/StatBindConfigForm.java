package cn.mulanbay.pms.web.bean.req.report.bind;


import cn.mulanbay.pms.persistent.enums.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class StatBindConfigForm implements java.io.Serializable {

    private static final long serialVersionUID = 882330393814155329L;

    private Long configId;

    @NotEmpty(message = "名称不能为空")
    private String configName;

    @NotNull(message = "业务类型不能为空")
    private StatBussType type;

    @NotNull(message = "数据来源不能为空")
    private StatValueSource source;

    @NotNull(message = "数据值类型不能为空")
    private StatValueClass valueClass;

    /**
     * 绑定的外键ID
     */
    @NotNull(message = "外键不能为空")
    private Long fid;

    //@NotNull(message = "配置值不能为空")
    private String configValue;

    //当source为枚举类时有效
    private EnumIdType enumIdType;

    //是否记录由上层来决定
    @NotNull(message = "级联类型不能为空")
    private CasCadeType casCadeType;

    //是否和用户绑定，空表示不绑定
    @NotNull(message = "是否绑定用户不能为空")
    private Boolean bindUser;

    @NotNull(message = "是否树形结构不能为空")
    private Boolean tree;

    private String formField;

    private String defaultValue;

    /**
     * 是否可为空，针对type=CHART
     */
    @NotNull(message = "是否可为空不能为空")
    private Boolean nullable;

    @NotNull(message = "排序号不能为空")
    private Integer orderIndex;

    //没有配置时，提示信息
    @NotNull(message = "提示信息不能为空")
    private String msg;

    private String remark;

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
}
