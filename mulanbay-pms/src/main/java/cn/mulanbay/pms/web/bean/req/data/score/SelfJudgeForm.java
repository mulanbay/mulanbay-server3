package cn.mulanbay.pms.web.bean.req.data.score;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotNull;

public class SelfJudgeForm implements BindUser {

    private Long userId;

    @NotNull(message = "评分组号不能为空")
    private Long scoreGroupId;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getScoreGroupId() {
        return scoreGroupId;
    }

    public void setScoreGroupId(Long scoreGroupId) {
        this.scoreGroupId = scoreGroupId;
    }

}
