package cn.mulanbay.pms.persistent.dto.auth;

public class UserRoleDTO {

    private Long roleId;

    private String roleName;

    private Long userRoleId;

    public UserRoleDTO(Long roleId, String roleName, Long userRoleId) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.userRoleId = userRoleId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(Long userRoleId) {
        this.userRoleId = userRoleId;
    }
}
