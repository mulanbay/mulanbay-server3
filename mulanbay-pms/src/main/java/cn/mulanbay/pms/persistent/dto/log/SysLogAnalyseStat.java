package cn.mulanbay.pms.persistent.dto.log;

public class SysLogAnalyseStat {

    private Object id;
    private String name;
    private Long totalCount;

    public SysLogAnalyseStat(Object id, Long totalCount) {
        this.id = id;
        this.totalCount = totalCount;
    }

    public SysLogAnalyseStat(Object id, String name, Long totalCount) {
        this.id = id;
        this.name = name;
        this.totalCount = totalCount;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
