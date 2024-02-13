package cn.mulanbay.pms.web.bean.req.health.body;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class BodyAbnormalForm implements BindUser {

    private Long id;

    private Long userId;

    //器官
    @NotEmpty(message = "器官不能为空")
    private String organ;

    // 疾病
    @NotEmpty(message = "疾病不能为空")
    private String disease;

    // 疼痛级别(1-10)
    @NotNull(message = "疼痛级别不能为空")
    private Integer painLevel;

    // 重要等级(0-5)
    @NotNull(message = "重要等级不能为空")
    private Double important;

    // 发生日期
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "发生日期不能为空")
    private Date occurDate;

    // 结束日期
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "结束日期不能为空")
    private Date finishDate;

    //持续天数
    @NotNull(message = "持续天数不能为空")
    private Integer days;

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

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public Integer getPainLevel() {
        return painLevel;
    }

    public void setPainLevel(Integer painLevel) {
        this.painLevel = painLevel;
    }

    public Double getImportant() {
        return important;
    }

    public void setImportant(Double important) {
        this.important = important;
    }

    public Date getOccurDate() {
        return occurDate;
    }

    public void setOccurDate(Date occurDate) {
        this.occurDate = occurDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
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
