package cn.mulanbay.pms.handler.bean.message;

import java.util.Date;
import java.util.Objects;

/**
 * @author fenghong
 * @date 2024/3/8
 */
public class DelayMessageBean implements java.io.Serializable{

    private static final long serialVersionUID = 1L;

    private Long msgId;

    private Date expectSendTime;

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Date getExpectSendTime() {
        return expectSendTime;
    }

    public void setExpectSendTime(Date expectSendTime) {
        this.expectSendTime = expectSendTime;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DelayMessageBean bean) {
            if(bean.getMsgId()==null&&this.getMsgId()==null){
                return super.equals(other);
            }
            return bean.getMsgId().equals(this.getMsgId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(msgId);
    }
}
