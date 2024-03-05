package cn.mulanbay.pms.persistent.dto.schedule;

public class TaskTriggerStat {
    private Object id;
    private String name;
    private Long totalCount;

    public TaskTriggerStat(Object id, Long totalCount) {
        this.id = id;
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
