package cn.mulanbay.pms.web.bean.req.music.musicPracticeDetail;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.TuneLevel;
import cn.mulanbay.pms.persistent.enums.TuneType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class MusicPracticeDetailForm implements BindUser {

    private Long detailId;
    private Long userId;

    @NotNull(message = "练习编号不能为空")
    private Long practiceId;

    @NotEmpty(message = "曲子不能为空")
    private String tune;

    @NotNull(message = "时长不能为空")
    private Integer times;

    @NotNull(message = "练习水平不能为空")
    private TuneLevel level;

    @NotNull(message = "曲子类型不能为空")
    private TuneType tuneType;
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

    public Long getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(Long practiceId) {
        this.practiceId = practiceId;
    }

    public String getTune() {
        return tune;
    }

    public void setTune(String tune) {
        this.tune = tune;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public TuneLevel getLevel() {
        return level;
    }

    public void setLevel(TuneLevel level) {
        this.level = level;
    }

    public TuneType getTuneType() {
        return tuneType;
    }

    public void setTuneType(TuneType tuneType) {
        this.tuneType = tuneType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
