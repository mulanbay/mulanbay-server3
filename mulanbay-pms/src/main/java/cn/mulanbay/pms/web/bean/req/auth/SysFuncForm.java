package cn.mulanbay.pms.web.bean.req.auth;

import cn.mulanbay.pms.persistent.enums.*;
import jakarta.validation.constraints.NotEmpty;

public class SysFuncForm {
    private Long funcId;

    @NotEmpty(message = "名称不能为空")
    private String funcName;
    private String supportMethods;

    @NotEmpty(message = "URL地址不能为空")
    private String urlAddress;
    private UrlType urlType;
    private FunctionType funcType;
    private FunctionDataType funcDataType;
    private String imageName;
    private Long parentId;
    private CommonStatus status;
    //针对什么类,查询使用
    private String beanName;
    //主键的域,查询使用
    private String idField;
    private IdFieldType idFieldType;
    private Integer orderIndex;
    private Boolean doLog;
    //是否计入触发统计（因为有些是筛选条件，这些并不需要统计）
    private Boolean triggerStat;
    //区分用户,有些公共的功能点不需要区分用户
    private Boolean diffUser;
    //是否登录验证
    private Boolean loginAuth;
    //是否授权认证
    private Boolean permissionAuth;
    //是否IP认证
    private Boolean ipAuth;
    //是否自动登陆
    private Boolean alwaysShow;
    //是否请求限制
    private Boolean requestLimit;
    //限制周期
    private Integer requestLimitPeriod;
    //是否每天限制操作数(大于0说明要限制)
    private Integer dayLimit;
    //记录返回数据
    private Boolean logRes;
    //奖励积分(正为加，负为减)
    private Integer rewardPoint;
    //错误代码定义，方便日志监控
    private Integer errorCode;
    //是否树形统计
    private Boolean treeStat;
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
}
