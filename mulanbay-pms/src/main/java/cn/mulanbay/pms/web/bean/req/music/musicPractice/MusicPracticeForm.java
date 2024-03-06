package cn.mulanbay.pms.web.bean.req.music.musicPractice;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class MusicPracticeForm implements BindUser {

    private Long practiceId;
    private Long userId;

    @NotNull(message = "乐器不能为空")
    private Long instrumentId;

    @NotNull(message = "时长不能为空")
    private Integer minutes;

    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @NotNull(message = "开始时间不能为空")
    private Date startTime;

    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @NotNull(message = "结束时间不能为空")
    private Date endTime;

    private String remark;

    public Long getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(Long practiceId) {
        this.practiceId = practiceId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Long instrumentId) {
        this.instrumentId = instrumentId;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
