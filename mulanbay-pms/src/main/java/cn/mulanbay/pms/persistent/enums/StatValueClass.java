package cn.mulanbay.pms.persistent.enums;


/**
 * 字段值类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum StatValueClass {

    LONG(0, "数字:Long"),
    INTEGER(1, "数字:Integer"),
    SHORT(2, "数字:Short"),
    STRING(3, "字符类型"),
    BOOLEAN(4, "布尔类型");

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
