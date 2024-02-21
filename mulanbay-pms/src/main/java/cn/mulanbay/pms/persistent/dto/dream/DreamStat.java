package cn.mulanbay.pms.persistent.dto.dream;

public class DreamStat {

    private String name;

    private Number id;
    //次数
    private Long totalCount;

    public DreamStat(Number id, Long totalCount) {
        this.id = id;
        this.totalCount = totalCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Number getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
