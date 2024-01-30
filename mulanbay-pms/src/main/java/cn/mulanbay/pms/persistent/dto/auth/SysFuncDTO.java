package cn.mulanbay.pms.persistent.dto.auth;

/**
 * 系统功能点封装
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class SysFuncDTO {

    private Long funcId;

    private Long pid;

    private String funcName;

    public SysFuncDTO(Long funcId, String funcName) {
        this.funcId = funcId;
        this.funcName = funcName;
    }

    /**
     * 构造器中的顺序要和查询的列顺序要一致
     * 必须需要一个包括所有查询列的构造器
     * @param funcId
     * @param pid
     * @param funcName
     */
    public SysFuncDTO(Long funcId, Long pid, String funcName) {
        this.funcId = funcId;
        this.pid = pid;
        this.funcName = funcName;
    }

    public Long getFuncId() {
        return funcId;
    }

    public void setFuncId(Long funcId) {
        this.funcId = funcId;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }
}
