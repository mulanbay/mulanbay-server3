package cn.mulanbay.pms.persistent.enums;

/**
 * 商品过期类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum ConsumeInvalidType {

    EXPIRED(0, "过期"),
    SOLD(1, "售出"),
    REFUND(2, "退款");

    private int value;

    private String name;

    ConsumeInvalidType(int value, String name) {
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
