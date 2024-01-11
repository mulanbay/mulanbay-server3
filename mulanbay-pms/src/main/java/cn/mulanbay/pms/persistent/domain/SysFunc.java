package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 功能点配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "sys_func")
public class SysFunc implements java.io.Serializable {

    private static final long serialVersionUID = 529618585154626058L;

    /**
     * 功能点编号
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "func_id", unique = true, nullable = false)
    private Long funcId;

    /**
     * 功能点名称
     */
    @Column(name = "func_name", nullable = false, length = 32)
    private String funcName;

    @Column(name = "support_methods", nullable = false, length = 64)
    private String supportMethods;

    @Column(name = "url_address", nullable = false, length = 100)
    private String urlAddress;

    @Column(name = "url_type", nullable = false)
    private UrlType urlType;

    @Column(name = "func_type", nullable = false)
    private FunctionType funcType;

    @Column(name = "func_data_type")
    private FunctionDataType funcDataType;

    @Column(name = "image_name")
    private String imageName;

    /**
     * 父级功能点
     */
    @JsonManagedReference
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "pid", nullable = true)
    private SysFunc parent;

    @Column(name = "status")
    private CommonStatus status;

    /**
     * 针对什么类,查询使用
     */
    @Column(name = "bean_name")
    private String beanName;

    /**
     * 主键的字段名
     */
    @Column(name = "id_field")
    private String idField;

    @Column(name = "id_field_type")
    private IdFieldType idFieldType;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "do_log")
    private Boolean doLog;

    /**
     * 是否计入触发统计（因为有些是筛选条件，这些并不需要统计）
     */
    @Column(name = "trigger_stat")
    private Boolean triggerStat;

    /**
     * 区分用户,有些公共的功能点不需要区分用户
     */
    @Column(name = "diff_user")
    private Boolean diffUser;

    /**
     * 是否登录验证
     */
    @Column(name = "login_auth")
    private Boolean loginAuth;

    /**
     * 是否授权认证
     */
    @Column(name = "permission_auth")
    private Boolean permissionAuth;

    /**
     * 是否IP认证
     */
    @Column(name = "ip_auth")
    private Boolean ipAuth;

    /**
     * 是否始终显示(针对需要权限认证的目录类型功能点)
     */
    @Column(name = "always_show")
    private Boolean alwaysShow;

    /**
     * 是否请求限制
     */
    @Column(name = "request_limit")
    private Boolean requestLimit;

    /**
     * 限制周期
     */
    @Column(name = "request_limit_period")
    private Integer requestLimitPeriod;

    /**
     * 是否每天限制操作数(大于0说明要限制)
     */
    @Column(name = "day_limit")
    private Integer dayLimit;

    /**
     * 记录返回数据
     */
    @Column(name = "log_res")
    private Boolean logRes;

    /**
     * 奖励积分(正为加，负为减)
     */
    @Column(name = "reward_point")
    private Integer rewardPoint;

    /**
     * 错误代码定义，方便日志监控
     */
    @Column(name = "error_code")
    private Integer errorCode;

    /**
     * 是否树形统计
     */
    @Column(name = "tree_stat")
    private Boolean treeStat;

    /**
     * 需要二次验证
     * 针对一些敏感的功能点
     */
    @Column(name = "sec_auth")
    private Boolean secAuth;

    /**
     * 权限标识,与前端的按钮绑定
     */
    @Column(name = "perms")
    private String perms;

    /**
     * 组件路径
     */
    @Column(name = "component")
    private String component;

    /**
     * 路由地址
     */
    @Column(name = "path")
    private String path;

    /**
     * 菜单可见
     */
    @Column(name = "visible")
    private Boolean visible;

    /**
     * 是不是路由（true时说明有在页面有对应的地址）
     */
    @Column(name = "router")
    private Boolean router;

    /**
     * 外链
     */
    @Column(name = "frame")
    private Boolean frame;

    /**
     * 是否缓存，keep-alive使用
     */
    @Column(name = "cache")
    private Boolean cache;

    @Column(name = "remark", length = 200)
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time", length = 19)
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time", length = 19)
    private Date modifyTime;

    @Transient
    List<SysFunc> children = new ArrayList<>();

    public Long getFuncId() {
        return funcId;
    }

    public void setFuncId(Long funcId) {
        this.funcId = funcId;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getSupportMethods() {
        return supportMethods;
    }

    public void setSupportMethods(String supportMethods) {
        this.supportMethods = supportMethods;
    }

    public String getUrlAddress() {
        return urlAddress;
    }

    public void setUrlAddress(String urlAddress) {
        this.urlAddress = urlAddress;
    }

    public UrlType getUrlType() {
        return urlType;
    }

    public void setUrlType(UrlType urlType) {
        this.urlType = urlType;
    }

    public FunctionType getFuncType() {
        return funcType;
    }

    public void setFuncType(FunctionType funcType) {
        this.funcType = funcType;
    }

    public FunctionDataType getFuncDataType() {
        return funcDataType;
    }

    public void setFuncDataType(FunctionDataType funcDataType) {
        this.funcDataType = funcDataType;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public SysFunc getParent() {
        return parent;
    }

    public void setParent(SysFunc parent) {
        this.parent = parent;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public IdFieldType getIdFieldType() {
        return idFieldType;
    }

    public void setIdFieldType(IdFieldType idFieldType) {
        this.idFieldType = idFieldType;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Boolean getDoLog() {
        return doLog;
    }

    public void setDoLog(Boolean doLog) {
        this.doLog = doLog;
    }

    public Boolean getTriggerStat() {
        return triggerStat;
    }

    public void setTriggerStat(Boolean triggerStat) {
        this.triggerStat = triggerStat;
    }

    public Boolean getDiffUser() {
        return diffUser;
    }

    public void setDiffUser(Boolean diffUser) {
        this.diffUser = diffUser;
    }

    public Boolean getLoginAuth() {
        return loginAuth;
    }

    public void setLoginAuth(Boolean loginAuth) {
        this.loginAuth = loginAuth;
    }

    public Boolean getPermissionAuth() {
        return permissionAuth;
    }

    public void setPermissionAuth(Boolean permissionAuth) {
        this.permissionAuth = permissionAuth;
    }

    public Boolean getIpAuth() {
        return ipAuth;
    }

    public void setIpAuth(Boolean ipAuth) {
        this.ipAuth = ipAuth;
    }

    public Boolean getAlwaysShow() {
        return alwaysShow;
    }

    public void setAlwaysShow(Boolean alwaysShow) {
        this.alwaysShow = alwaysShow;
    }

    public Boolean getRequestLimit() {
        return requestLimit;
    }

    public void setRequestLimit(Boolean requestLimit) {
        this.requestLimit = requestLimit;
    }

    public Integer getRequestLimitPeriod() {
        return requestLimitPeriod;
    }

    public void setRequestLimitPeriod(Integer requestLimitPeriod) {
        this.requestLimitPeriod = requestLimitPeriod;
    }

    public Integer getDayLimit() {
        return dayLimit;
    }

    public void setDayLimit(Integer dayLimit) {
        this.dayLimit = dayLimit;
    }

    public Boolean getLogRes() {
        return logRes;
    }

    public void setLogRes(Boolean logRes) {
        this.logRes = logRes;
    }

    public Integer getRewardPoint() {
        return rewardPoint;
    }

    public void setRewardPoint(Integer rewardPoint) {
        this.rewardPoint = rewardPoint;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Boolean getTreeStat() {
        return treeStat;
    }

    public void setTreeStat(Boolean treeStat) {
        this.treeStat = treeStat;
    }

    public Boolean getSecAuth() {
        return secAuth;
    }

    public void setSecAuth(Boolean secAuth) {
        this.secAuth = secAuth;
    }

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getRouter() {
        return router;
    }

    public void setRouter(Boolean router) {
        this.router = router;
    }

    public Boolean getFrame() {
        return frame;
    }

    public void setFrame(Boolean frame) {
        this.frame = frame;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
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
    public List<SysFunc> getChildren() {
        return children;
    }

    public void setChildren(List<SysFunc> children) {
        this.children = children;
    }

    @Transient
    public String getUrlTypeName() {
        if (urlType != null) {
            return urlType.getName();
        } else {
            return null;
        }
    }

    @Transient
    public String getFuncTypeName() {
        if (funcType != null) {
            return funcType.getName();
        } else {
            return null;
        }
    }

    @Transient
    public String getFuncDataTypeName() {
        if (funcDataType != null) {
            return funcDataType.getName();
        } else {
            return null;
        }
    }

    @Transient
    public String getStatusName() {
        if (status != null) {
            return status.getName();
        } else {
            return null;
        }
    }

    @Transient
    public Long getParentId() {
        if (parent != null) {
            return parent.getFuncId();
        } else {
            return null;
        }
    }

    @Transient
    public Boolean getHasChildren() {
        if (funcDataType == FunctionDataType.M || funcDataType == FunctionDataType.C) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SysFunc bean) {
            return bean.getFuncId().equals(this.getFuncId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
