package cn.mulanbay.pms.persistent.enums;

/**
 * 提醒类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum NotifyType {

    LESS(0, "小于"),
    MORE(1, "大于");
    private int value;
    private String name;

    NotifyType(int value, String name) {
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
