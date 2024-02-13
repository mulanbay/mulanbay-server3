package cn.mulanbay.pms.web.bean.res.body;


import cn.mulanbay.pms.persistent.dto.health.TreatAnalyseDetailStat;

import java.math.BigDecimal;
import java.util.Date;

public class BodyAbnormalAnalyseVo {

    private long id;
    //器官名称
    private String name;

    //持续次数
    private Long totalCount;

    //持续天数
    private BigDecimal totalDays;

    private Date maxOccurDate;

    private Date minOccurDate;

    private TreatAnalyseDetailStat treatStat;

    //平均体重
    private BigDecimal avgWeight;

    //平均身高
    private BigDecimal avgHeight;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public BigDecimal getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(BigDecimal totalDays) {
        this.totalDays = totalDays;
    }

    public Date getMaxOccurDate() {
        return maxOccurDate;
    }

    public void setMaxOccurDate(Date maxOccurDate) {
        this.maxOccurDate = maxOccurDate;
    }

    public Date getMinOccurDate() {
        return minOccurDate;
    }

    public void setMinOccurDate(Date minOccurDate) {
        this.minOccurDate = minOccurDate;
    }

    public TreatAnalyseDetailStat getTreatStat() {
        return treatStat;
    }

    public void setTreatStat(TreatAnalyseDetailStat treatStat) {
        this.treatStat = treatStat;
    }

    public BigDecimal getAvgWeight() {
        return avgWeight;
    }

    public void setAvgWeight(BigDecimal avgWeight) {
        this.avgWeight = avgWeight;
    }

    public BigDecimal getAvgHeight() {
        return avgHeight;
    }

    public void setAvgHeight(BigDecimal avgHeight) {
        this.avgHeight = avgHeight;
    }
}
