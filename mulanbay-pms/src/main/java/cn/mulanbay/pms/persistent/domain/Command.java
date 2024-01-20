package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 命令配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "command")
public class Command implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    //简单的四位数字代码，目前用于微信的公众号
    @Column(name = "scode", nullable = false)
    private String scode;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "level", nullable = false)
    private LogLevel level;

    @Column(name = "order_index", nullable = false)
    private Short orderIndex;

    @Column(name = "status", nullable = false)
    private CommonStatus status;

    @Column(name = "remark")
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time")
    private Date modifyTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
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

    @Transient
    public String getLevelName() {
        if (level != null) {
            return level.getName();
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Command bean) {
            return bean.getId().equals(this.getId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
