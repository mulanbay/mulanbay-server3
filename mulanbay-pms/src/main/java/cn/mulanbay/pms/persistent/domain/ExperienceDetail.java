package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 人生经历每天的明细
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "experience_detail")
public class ExperienceDetail implements java.io.Serializable {
    private static final long serialVersionUID = 1618796530128502244L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "detail_id", unique = true, nullable = false)
    private Long detailId;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "exp_id", nullable = true)
    private Experience experience;

    @ManyToOne
    @JoinColumn(name = "start_country_id")
    private Country startCountry;

    @ManyToOne
    @JoinColumn(name = "start_province_id")
    private Province startProvince;

    @ManyToOne
    @JoinColumn(name = "start_city_id")
    private City startCity;

    @ManyToOne
    @JoinColumn(name = "start_district_id")
    private District startDistrict;

    @ManyToOne
    @JoinColumn(name = "arrive_country_id")
    private Country arriveCountry;

    @ManyToOne
    @JoinColumn(name = "arrive_province_id")
    private Province arriveProvince;

    @ManyToOne
    @JoinColumn(name = "arrive_city_id")
    private City arriveCity;

    @ManyToOne
    @JoinColumn(name = "arrive_district_id")
    private District arriveDistrict;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "occur_date")
    private Date occurDate;

    @Column(name = "cost",precision = 9,scale = 2)
    private BigDecimal cost;
    //是否加入到地图的绘制
    @Column(name = "map_stat")
    private Boolean mapStat;
    //国际线路
    @Column(name = "international")
    private Boolean international;
    @Column(name = "remark")
    private String remark;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time",updatable = false)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time",insertable = false)
    private Date modifyTime;

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }

    public Country getStartCountry() {
        return startCountry;
    }

    public void setStartCountry(Country startCountry) {
        this.startCountry = startCountry;
    }

    public Province getStartProvince() {
        return startProvince;
    }

    public void setStartProvince(Province startProvince) {
        this.startProvince = startProvince;
    }

    public City getStartCity() {
        return startCity;
    }

    public void setStartCity(City startCity) {
        this.startCity = startCity;
    }

    public District getStartDistrict() {
        return startDistrict;
    }

    public void setStartDistrict(District startDistrict) {
        this.startDistrict = startDistrict;
    }

    public Country getArriveCountry() {
        return arriveCountry;
    }

    public void setArriveCountry(Country arriveCountry) {
        this.arriveCountry = arriveCountry;
    }

    public Province getArriveProvince() {
        return arriveProvince;
    }

    public void setArriveProvince(Province arriveProvince) {
        this.arriveProvince = arriveProvince;
    }

    public City getArriveCity() {
        return arriveCity;
    }

    public void setArriveCity(City arriveCity) {
        this.arriveCity = arriveCity;
    }

    public District getArriveDistrict() {
        return arriveDistrict;
    }

    public void setArriveDistrict(District arriveDistrict) {
        this.arriveDistrict = arriveDistrict;
    }

    public Date getOccurDate() {
        return occurDate;
    }

    public void setOccurDate(Date occurDate) {
        this.occurDate = occurDate;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Boolean getMapStat() {
        return mapStat;
    }

    public void setMapStat(Boolean mapStat) {
        this.mapStat = mapStat;
    }

    public Boolean getInternational() {
        return international;
    }

    public void setInternational(Boolean international) {
        this.international = international;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ExperienceDetail bean) {
            return bean.getDetailId().equals(this.getDetailId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(detailId);
    }
}
