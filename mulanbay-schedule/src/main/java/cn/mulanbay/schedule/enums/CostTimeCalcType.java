package cn.mulanbay.schedule.enums;

/**
 * 花费时间计算类型
 *
 * @author fenghong
 * @create 2017-10-19 13:51
 **/
public enum CostTimeCalcType {

    AVG(0,"avg","平均值"),
    MIN(1,"min","最小值"),
    MAX(2,"max","最大值"),
    LAST(3,"last","最近值");

    private Integer value;

    private String method;

    private String name;

    CostTimeCalcType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    CostTimeCalcType(Integer value, String method, String name) {
        this.value = value;
        this.method = method;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static CostTimeCalcType getType(int ordinal) {
        for (CostTimeCalcType bt : CostTimeCalcType.values()) {
            if (bt.ordinal() == ordinal) {
                return bt;
            }
        }
        return null;
    }
}
