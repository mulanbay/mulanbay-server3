package cn.mulanbay.pms.persistent.dto.food;

public class DietAnalyseStat {

    private String name;

    private Long totalCount;

    public DietAnalyseStat() {
    }

    public DietAnalyseStat(String name, Long totalCount) {
        this.name = name;
        this.totalCount = totalCount;
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
