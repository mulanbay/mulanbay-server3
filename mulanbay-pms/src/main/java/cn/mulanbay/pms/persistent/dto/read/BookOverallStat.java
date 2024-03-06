package cn.mulanbay.pms.persistent.dto.read;

public class BookOverallStat {

    private Number indexValue;
    private Long totalCount;
    private Long cateId;

    public BookOverallStat(Number indexValue, Long totalCount, Long cateId) {
        this.indexValue = indexValue;
        this.totalCount = totalCount;
        this.cateId = cateId;
    }

    public Number getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Number indexValue) {
        this.indexValue = indexValue;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getCateId() {
        return cateId;
    }

    public void setCateId(Long cateId) {
        this.cateId = cateId;
    }
}
