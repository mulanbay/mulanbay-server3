package cn.mulanbay.pms.web.bean.req.health.body;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BodyInfoForm implements BindUser {

    private Long id;

    private Long userId;

    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @NotNull(message = "记录时间不能为空")
    private Date recordTime;

    @NotNull(message = "体重不能为空")
    private Double weight;

    @NotNull(message = "身高不能为空")
    private Integer height;

    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
