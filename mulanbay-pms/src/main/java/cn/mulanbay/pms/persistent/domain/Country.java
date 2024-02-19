package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.CommonStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 国家
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "country")
public class Country implements java.io.Serializable {

    private static final long serialVersionUID = -7957057082541835L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "country_id", unique = true, nullable = false)
    private Long countryId;
    /**
     * 中文名称
     */
    @Column(name = "cn_name", nullable = false, length = 32)
    private String cnName;
    /**
     * 英文名称
     */
    @Column(name = "en_name", nullable = false, length = 32)
    private String enName;
    /**
     * 两位英文代码
     */
    @Column(name = "en_code2")
    private String enCode2;
    /**
     * 三位英文代码
     */
    @Column(name = "en_code3")
    private String enCode3;
    /**
     * 数字编号
     */
    @Column(name = "code")
    private String code;

    /**
     * 地理坐标，经纬度
     */
    @Column(name = "location")
    private String location;

    @Column(name = "order_index")
    private Short orderIndex;

    @Column(name = "status")
    private CommonStatus status;

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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getEnCode2() {
        return enCode2;
    }

    public void setEnCode2(String enCode2) {
        this.enCode2 = enCode2;
    }

    public String getEnCode3() {
        return enCode3;
    }

    public void setEnCode3(String enCode3) {
        this.enCode3 = enCode3;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Short getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Short orderIndex) {
        this.orderIndex = orderIndex;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
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
}
