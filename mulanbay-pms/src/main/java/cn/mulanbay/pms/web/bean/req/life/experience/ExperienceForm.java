package cn.mulanbay.pms.web.bean.req.life.experience;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.ExperienceType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class ExperienceForm implements BindUser {

    private Long expId;
    private Long userId;

    @NotEmpty(message = "名称不能为空")
    private String expName;

    @NotNull(message = "类型不能为空")
    private ExperienceType type;

    @NotNull(message = "天数不能为空")
    private Integer days;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "开始日期不能为空")
    private Date startDate;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "结束日期不能为空")
    private Date endDate;

    private String tags;

    @NotNull(message = "国家不能为空")
    private Long countryId;

    //@NotNull(message = "省份不能为空")
    private Long provinceId;

    //@NotNull(message = "城市不能为空")
    private Long cityId;

    //@NotNull(message = "县不能为空")
    private Long districtId;

    private String remark;

    public Long getExpId() {
        return expId;
    }

    public void setExpId(Long expId) {
        this.expId = expId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getExpName() {
        return expName;
    }

    public void setExpName(String expName) {
        this.expName = expName;
    }

    public ExperienceType getType() {
        return type;
    }

    public void setType(ExperienceType type) {
        this.type = type;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
