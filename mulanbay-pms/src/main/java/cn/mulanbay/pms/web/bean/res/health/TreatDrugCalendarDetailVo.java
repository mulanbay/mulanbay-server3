package cn.mulanbay.pms.web.bean.res.health;

import java.util.Date;

/**
 * @Description:
 * @Author: fenghong
 * @Create : 2021/3/5
 */
public class TreatDrugCalendarDetailVo {

    private Long drugId;
    private String drugName;
    private String disease;
    //每多少天一次
    private Integer perDay;
    private Integer perTimes;
    //每次食用的单位
    private String eu;
    //每次食用的量
    private Integer ec;
    private String useWay;
    private Long detailId;
    private Date occurTime;
    //用药时间和预期是否匹配
    private boolean match=true;

    public Long getDrugId() {
        return drugId;
    }

    public void setDrugId(Long drugId) {
        this.drugId = drugId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public Integer getPerDay() {
        return perDay;
    }

    public void setPerDay(Integer perDay) {
        this.perDay = perDay;
    }

    public Integer getPerTimes() {
        return perTimes;
    }

    public void setPerTimes(Integer perTimes) {
        this.perTimes = perTimes;
    }

    public String getEu() {
        return eu;
    }

    public void setEu(String eu) {
        this.eu = eu;
    }

    public Integer getEc() {
        return ec;
    }

    public void setEc(Integer ec) {
        this.ec = ec;
    }

    public String getUseWay() {
        return useWay;
    }

    public void setUseWay(String useWay) {
        this.useWay = useWay;
    }

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }
}
