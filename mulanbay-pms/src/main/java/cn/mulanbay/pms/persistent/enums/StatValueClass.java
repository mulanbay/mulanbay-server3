package cn.mulanbay.pms.persistent.enums;


/**
 * 字段值类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum StatValueClass {

    LONG(0, "Long"),
    INTEGER(1, "Integer"),
    SHORT(2, "Short"),
    STRING(3, "String"),
    BOOLEAN(4, "Boolean");

    private int value;

    private String name;

    StatValueClass(int value, String name) {
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
