package cn.mulanbay.pms.persistent.enums;

/**
 * 消息发送状态
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum MessageSendStatus {

    UN_SEND(0, "未发送"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败"),
    SKIP(3, "放弃");

    private int value;

    private String name;

    MessageSendStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
