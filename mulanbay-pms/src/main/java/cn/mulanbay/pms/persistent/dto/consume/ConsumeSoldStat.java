package cn.mulanbay.pms.persistent.dto.consume;

public class ConsumeSoldStat {

    // 0非二手 1二手
    private Integer secondhand;

    // 0未售 1已售
    private Integer sold;

    private Long totalCount;

    public ConsumeSoldStat(Integer secondhand, Integer sold, Long totalCount) {
        this.secondhand = secondhand;
        this.sold = sold;
        this.totalCount = totalCount;
    }

    public Integer getSecondhand() {
        return secondhand;
    }

    public void setSecondhand(Integer secondhand) {
        this.secondhand = secondhand;
    }

    public Integer getSold() {
        return sold;
    }

    public void setSold(Integer sold) {
        this.sold = sold;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
