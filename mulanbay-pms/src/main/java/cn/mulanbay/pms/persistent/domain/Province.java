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
 * 省份信息
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "province")
public class Province implements java.io.Serializable {

    private static final long serialVersionUID = 7063829351544112618L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "province_id", unique = true, nullable = false)
    private Long provinceId;

    @Column(name = "province_name", nullable = false, length = 32)
    private String provinceName;
    //地图显示使用，百度的echarts的省份等名称和第三方的有点不一样，比如第三方：浙江省，百度的echarts需要的只是：浙江
    @Column(name = "map_name")
    private String mapName;

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

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
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
