package cn.mulanbay.pms.web.bean.req.auth;

import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;

public class SysFuncTreeReq extends QueryBuilder {

    private Boolean needRoot;

    @Query(fieldName = "funcDataType", op = Parameter.Operator.IN)
    private String intFuncDataTypes;

    @Query(fieldName = "secAuth", op = Parameter.Operator.EQ)
    private Boolean secAuth;

    @Query(fieldName = "permissionAuth", op = Parameter.Operator.EQ)
    private Boolean permissionAuth;

    public Boolean getNeedRoot() {
        return needRoot;
    }

    public void setNeedRoot(Boolean needRoot) {
        this.needRoot = needRoot;
    }

    public String getIntFuncDataTypes() {
        return intFuncDataTypes;
    }

    public void setIntFuncDataTypes(String intFuncDataTypes) {
        this.intFuncDataTypes = intFuncDataTypes;
    }

    public Boolean getSecAuth() {
        return secAuth;
    }

    public void setSecAuth(Boolean secAuth) {
        this.secAuth = secAuth;
    }

    public Boolean getPermissionAuth() {
        return permissionAuth;
    }

    public void setPermissionAuth(Boolean permissionAuth) {
        this.permissionAuth = permissionAuth;
    }
}
