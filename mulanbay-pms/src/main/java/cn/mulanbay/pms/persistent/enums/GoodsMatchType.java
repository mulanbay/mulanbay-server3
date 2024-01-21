package cn.mulanbay.pms.persistent.enums;

/**
 * 匹配类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum GoodsMatchType {

    CONSUME(0, "消费记录"),
    GOODS_TYPE(1, "商品类型");

    private int value;

    private String name;

    GoodsMatchType(int value, String name) {
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
