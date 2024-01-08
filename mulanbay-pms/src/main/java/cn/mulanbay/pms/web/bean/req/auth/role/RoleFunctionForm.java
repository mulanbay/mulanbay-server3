package cn.mulanbay.pms.web.bean.req.auth.role;

/**
 * ${DESCRIPTION}
 *
 * @author fenghong
 * @create 2018-02-14 15:55
 */
public class RoleFunctionForm {

    private Long roleId;

    private String functionIds;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getFunctionIds() {
        return functionIds;
    }

    public void setFunctionIds(String functionIds) {
        this.functionIds = functionIds;
    }
}
