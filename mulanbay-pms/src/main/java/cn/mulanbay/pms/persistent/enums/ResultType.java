package cn.mulanbay.pms.persistent.enums;

/**
 * 提醒值返回类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum ResultType {
    DATE(0, "日期"),
    NUMBER(1, "数字"),
    DATE_NAME(2, "日期-名称"),
    NUMBER_NAME(3, "数字-名称");
    private int value;
    private String name;

    ResultType(int value, String name) {
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

