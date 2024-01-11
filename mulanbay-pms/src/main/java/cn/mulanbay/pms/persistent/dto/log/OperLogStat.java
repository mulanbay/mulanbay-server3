package cn.mulanbay.pms.persistent.dto.log;

import java.math.BigInteger;

public class OperLogStat {

    private String funcName;

    private Long totalCount;

    public OperLogStat(String funcName, Long totalCount) {
        this.funcName = funcName;
        this.totalCount = totalCount;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
