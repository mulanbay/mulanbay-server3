package cn.mulanbay.pms.persistent.enums;

/**
 * 消费管理类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum ConsumeReferType {

    TREAT(0, "看病记录"),
    INCOME(1, "收入");

    private int value;

    private String name;

    ConsumeReferType(int value, String name) {
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
