package cn.mulanbay.pms.persistent.dto.auth;

public class RoleFunctionDTO {

    private Long funcId;

    private String funcName;

    private Long pid;

    private Long roleFunctionId;

    public RoleFunctionDTO(Long funcId, String funcName, Long pid, Long roleFunctionId) {
        this.funcId = funcId;
        this.funcName = funcName;
        this.pid = pid;
        this.roleFunctionId = roleFunctionId;
    }

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

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getRoleFunctionId() {
        return roleFunctionId;
    }

    public void setRoleFunctionId(Long roleFunctionId) {
        this.roleFunctionId = roleFunctionId;
    }
}
