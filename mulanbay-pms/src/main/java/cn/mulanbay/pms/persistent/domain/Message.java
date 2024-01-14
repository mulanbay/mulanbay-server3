package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import cn.mulanbay.pms.persistent.enums.MessageSendStatus;
import cn.mulanbay.pms.persistent.enums.MessageType;
import cn.mulanbay.pms.persistent.enums.MonitorBussType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.util.Date;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 消息
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "message")
public class Message implements java.io.Serializable {

    // Fields

    /**
     *
     */
    private static final long serialVersionUID = 961922489014144054L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "msg_id", unique = true, nullable = false)
    private Long msgId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "msg_type")
    private MessageType msgType;

    @Column(name = "buss_type")
    private MonitorBussType bussType;

    @Column(name = "send_status")
    private MessageSendStatus sendStatus;

    @Column(name = "log_level")
    private LogLevel logLevel;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "fail_count")
    private Integer failCount;

    /**
     * 期望发送时间，用户可以设置提醒时间，让自己可以选择什么时候发送
     */
    @Column(name = "expect_send_time")
    private Date expectSendTime;

    @Column(name = "last_send_time")
    private Date lastSendTime;

    /**
     * 哪台服务器节点
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * 代码（错误代码）
     */
    @Column(name = "code")
    private Integer code;

    /**
     * 微信的跳转地址
     */
    @Column(name = "url")
    private String url;
    @Column(name = "remark", length = 200)
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time", length = 19)
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time", length = 19)
    private Date modifyTime;


    // Constructors

    /**
     * default constructor
     */
    public Message() {
    }


    // Property accessors

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }

    public MonitorBussType getBussType() {
        return bussType;
    }

    public void setBussType(MonitorBussType bussType) {
        this.bussType = bussType;
    }

    public MessageSendStatus getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(MessageSendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public Date getExpectSendTime() {
        return expectSendTime;
    }

    public void setExpectSendTime(Date expectSendTime) {
        this.expectSendTime = expectSendTime;
    }

    public Date getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(Date lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
    public String getSendStatusName() {
        return sendStatus == null ? null : sendStatus.getName();
    }

    @Transient
    public String getMsgTypeName() {
        return msgType == null ? null : msgType.getName();
    }

    @Transient
    public String getBussTypeName() {
        return bussType == null ? null : bussType.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Message bean) {
            return bean.getMsgId().equals(this.getMsgId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
