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

    @NotNull(message = "出发国家不能为空")
    private Long startCountryId;

    //@NotNull(message = "省份不能为空")
    private Long startProvinceId;

    //@NotNull(message = "城市不能为空")
    private Long startCityId;

    //@NotNull(message = "县不能为空")
    private Long startDistrictId;

    @NotNull(message = "抵达国家不能为空")
    private Long arriveCountryId;

    //@NotNull(message = "省份不能为空")
    private Long arriveProvinceId;

    //@NotNull(message = "城市不能为空")
    private Long arriveCityId;

    //@NotNull(message = "县不能为空")
    private Long arriveDistrictId;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "出发日期不能为空")
    private Date occurDate;

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

    public Long getStartCountryId() {
        return startCountryId;
    }

    public void setStartCountryId(Long startCountryId) {
        this.startCountryId = startCountryId;
    }

    public Long getStartProvinceId() {
        return startProvinceId;
    }

    public void setStartProvinceId(Long startProvinceId) {
        this.startProvinceId = startProvinceId;
    }

    public Long getStartCityId() {
        return startCityId;
    }

    public void setStartCityId(Long startCityId) {
        this.startCityId = startCityId;
    }

    public Long getStartDistrictId() {
        return startDistrictId;
    }

    public void setStartDistrictId(Long startDistrictId) {
        this.startDistrictId = startDistrictId;
    }

    public Long getArriveCountryId() {
        return arriveCountryId;
    }

    public void setArriveCountryId(Long arriveCountryId) {
        this.arriveCountryId = arriveCountryId;
    }

    public Long getArriveProvinceId() {
        return arriveProvinceId;
    }

    public void setArriveProvinceId(Long arriveProvinceId) {
        this.arriveProvinceId = arriveProvinceId;
    }

    public Long getArriveCityId() {
        return arriveCityId;
    }

    public void setArriveCityId(Long arriveCityId) {
        this.arriveCityId = arriveCityId;
    }

    public Long getArriveDistrictId() {
        return arriveDistrictId;
    }

    public void setArriveDistrictId(Long arriveDistrictId) {
        this.arriveDistrictId = arriveDistrictId;
    }

    public Date getOccurDate() {
        return occurDate;
    }

    public void setOccurDate(Date occurDate) {
        this.occurDate = occurDate;
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
