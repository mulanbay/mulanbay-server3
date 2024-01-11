package cn.mulanbay.pms.persistent.dto.log;

import java.math.BigInteger;

public class OperLogTreeStat {

    private Long funcId;

    private Long totalCount;

    private String name;

    private Long pid;

    private String pname;

    public OperLogTreeStat(Long funcId, Long totalCount, String name, Long pid, String pname) {
        this.funcId = funcId;
        this.totalCount = totalCount;
        this.name = name;
        this.pid = pid;
        this.pname = pname;
    }

    public Long getFuncId() {
        return funcId;
    }

    public void setFuncId(Long funcId) {
        this.funcId = funcId;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }
}
