package cn.mulanbay.pms.web.bean.req.sport.exercise;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.web.bean.req.BaseYoyStatSH;
import jakarta.validation.constraints.NotNull;

public class ExerciseYoyStatSH extends BaseYoyStatSH implements BindUser {

    private Long userId;

    @NotNull(message = "运动类型编号不能为空")
    private Long sportId;

    //是否统计值
    private Boolean sumValue;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Boolean getSumValue() {
        return sumValue;
    }

    public void setSumValue(Boolean sumValue) {
        this.sumValue = sumValue;
    }
}
