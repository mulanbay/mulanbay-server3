package cn.mulanbay.pms.web.bean.req.life.detail;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public class ExperienceDetailForm implements BindUser {

    private Long detailId;
    private Long userId;

    @NotNull(message = "经历编号不能为空")
    private Long expId;

    @NotNull(message = "国家编号不能为空")
    private Long countryId;

    @NotNull(message = "国家地理位置不能为空")
    private String countryLocation;

    @NotNull(message = "省份编号不能为空")
    private Long provinceId;

    @NotNull(message = "城市编号不能为空")
    private Long cityId;

    @NotNull(message = "县市编号不能为空")
    private Long districtId;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "出发日期不能为空")
    private Date occurDate;

    @NotNull(message = "出发城市不能为空")
    private String startCity;
    //出发城市地理位置
    @NotNull(message = "出发城市不能为空")
    private String scLocation;

    @NotNull(message = "出发城市不能为空")
    private String arriveCity;

    //抵达城市地理位置
    @NotNull(message = "出发城市位置不能为空")
    private String acLocation;

    private BigDecimal cost;

    //是否加入到地图的绘制
    private Boolean mapStat;
    //国际线路
    private Boolean international;
    private String remark;

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getExpId() {
        return expId;
    }

    public void setExpId(Long expId) {
        this.expId = expId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getCountryLocation() {
        return countryLocation;
    }

    public void setCountryLocation(String countryLocation) {
        this.countryLocation = countryLocation;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public Date getOccurDate() {
        return occurDate;
    }

    public void setOccurDate(Date occurDate) {
        this.occurDate = occurDate;
    }

    public String getStartCity() {
        return startCity;
    }

    public void setStartCity(String startCity) {
        this.startCity = startCity;
    }

    public String getScLocation() {
        return scLocation;
    }

    public void setScLocation(String scLocation) {
        this.scLocation = scLocation;
    }

    public String getArriveCity() {
        return arriveCity;
    }

    public void setArriveCity(String arriveCity) {
        this.arriveCity = arriveCity;
    }

    public String getAcLocation() {
        return acLocation;
    }

    public void setAcLocation(String acLocation) {
        this.acLocation = acLocation;
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
}
