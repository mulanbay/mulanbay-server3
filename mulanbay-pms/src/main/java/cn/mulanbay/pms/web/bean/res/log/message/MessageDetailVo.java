package cn.mulanbay.pms.web.bean.res.log.message;

import cn.mulanbay.pms.persistent.domain.Message;

/**
 * 消息详情
 * @author fenghong
 * @date 2024/1/14
 */
public class MessageDetailVo {

    private Message message;

    private MessageUserVo user;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public MessageUserVo getUser() {
        return user;
    }

    public void setUser(MessageUserVo user) {
        this.user = user;
    }
}
