package cn.mulanbay.pms.web.bean.req.auth.sysFunc;

import cn.mulanbay.pms.persistent.enums.*;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SysFuncForm {
    private Long funcId;

    @NotEmpty(message = "名称不能为空")
    private String funcName;

    @NotEmpty(message = "请求方法不能为空")
    private String supportMethods;

    @NotEmpty(message = "URL地址不能为空")
    private String urlAddress;

    @NotNull(message = "功能类型不能为空")
    private FunctionType funcType;

    @NotNull(message = "菜单类型不能为空")
    private FunctionDataType funcDataType;

    private String imageName;

    @NotNull(message = "父级节点不能为空")
    private Long parentId;

    @NotNull(message = "状态不能为空")
    private CommonStatus status;

    @NotEmpty(message = "绑定的类不能为空")
    private String beanName;

    //主键的域,查询使用
    private String idField;
    private IdFieldType idFieldType;

    @NotNull(message = "排序号不能为空")
    private Integer orderIndex;

    @NotNull(message = "是否记录日志不能为空")
    private Boolean doLog;
    //是否计入触发统计（因为有些是筛选条件，这些并不需要统计）
    @NotNull(message = "是否计入触发统计不能为空")
    private Boolean triggerStat;
    //区分用户,有些公共的功能点不需要区分用户
    @NotNull(message = "是否区分用户不能为空")
    private Boolean diffUser;
    //是否登录验证
    @NotNull(message = "是否登录验证不能为空")
    private Boolean loginAuth;
    //是否授权认证
    @NotNull(message = "是否授权认证不能为空")
    private Boolean permissionAuth;
    //是否IP认证
    @NotNull(message = "是否IP认证不能为空")
    private Boolean ipAuth;

    @NotNull(message = "是否始终显示不能为空")
    private Boolean alwaysShow;

    @NotNull(message = "用户限流不能为空")
    @Min(value = 0)
    private Integer userPeriod;

    /**
     * 系统限制次数（每天次数）
     */
    @NotNull(message = "系统限流不能为空")
    @Min(value = 0)
    private Integer sysLimit;
    //记录返回数据
    @NotNull(message = "是否记录返回数据不能为空")
    private Boolean logRes;
    //奖励积分(正为加，负为减)
    @NotNull(message = "奖励积分不能为空")
    private Integer rewardPoint;
    //系统代码定义，方便日志监控
    @NotNull(message = "系统代码定义不能为空")
    private Integer code;
    //是否树形统计
    @NotNull(message = "是否树形统计不能为空")
    private Boolean treeStat;

    @NotNull(message = "是否二次认证不能为空")
    private Boolean secAuth;
    //权限标识
    private String perms;
    //组件路径
    private String component;
    //路由地址
    private String path;
    //菜单可见
    private Boolean visible;
    //是不是路由（true时说明有在页面有对应的地址）
    private Boolean router;
    //外链
    private Boolean frame;
    //是否缓存，keep-alive使用
    private Boolean cache;
    private String remark;

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public Integer getUserPeriod() {
        return userPeriod;
    }

    public void setUserPeriod(Integer userPeriod) {
        this.userPeriod = userPeriod;
    }

    public Integer getSysLimit() {
        return sysLimit;
    }

    public void setSysLimit(Integer sysLimit) {
        this.sysLimit = sysLimit;
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

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
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

}
