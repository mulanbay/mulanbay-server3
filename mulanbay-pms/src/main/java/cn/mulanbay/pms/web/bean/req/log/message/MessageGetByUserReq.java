package cn.mulanbay.pms.web.bean.req.log.message;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotNull;

public class MessageGetByUserReq implements BindUser {

    private Long userId;

    @NotNull(message = "消息编号不能为空")
    private Long msgId;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }
}
