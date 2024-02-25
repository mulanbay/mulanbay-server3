package cn.mulanbay.pms.web.bean.req.work.businessTrip;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import java.util.Date;


public class BusinessTripForm implements BindUser {

    private Long tripId;

    private Long userId;

    @NotNull(message = "公司不能为空")
    private Long companyId;

    @NotNull(message = "国家不能为空")
    private Long countryId;

    //@NotNull(message = "省份不能为空")
    private Long provinceId;

    //@NotNull(message = "城市不能为空")
    private Long cityId;

    //@NotNull(message = "县不能为空")
    private Long districtId;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "出差时间不能为空")
    private Date tripDate;

    @NotNull(message = "天数不能为空不能为空")
    private Integer days;

    private String remark;

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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

    public Date getTripDate() {
        return tripDate;
    }

    public void setTripDate(Date tripDate) {
        this.tripDate = tripDate;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
