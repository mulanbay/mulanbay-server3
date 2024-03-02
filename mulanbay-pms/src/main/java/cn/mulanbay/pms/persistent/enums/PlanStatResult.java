package cn.mulanbay.pms.persistent.enums;

/**
 * 计划报告数据统计结果
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum PlanStatResult {

    ACHIEVED(0, "已实现"),
    UN_ACHIEVED(1, "未实现"),
    SKIP(2, "忽略");

    private int value;

    private String name;

    PlanStatResult(int value, String name) {
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

    public static PlanStatResult getType(int ordinal) {
        for (PlanStatResult ds : PlanStatResult.values()) {
            if (ds.ordinal() == ordinal) {
                return ds;
            }
        }
        return null;
    }
}
