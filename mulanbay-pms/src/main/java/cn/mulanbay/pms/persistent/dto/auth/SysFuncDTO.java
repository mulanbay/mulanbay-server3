package cn.mulanbay.pms.persistent.dto.auth;

import java.math.BigInteger;

/**
 * 系统功能点封装
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class SysFuncDTO {

    private BigInteger funcId;

    private BigInteger pid;

    private String funcName;

    public BigInteger getFuncId() {
        return funcId;
    }

    public void setFuncId(BigInteger funcId) {
        this.funcId = funcId;
    }

    public BigInteger getPid() {
        return pid;
    }

    public void setPid(BigInteger pid) {
        this.pid = pid;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }
}
