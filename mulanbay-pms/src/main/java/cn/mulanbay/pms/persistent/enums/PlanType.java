package cn.mulanbay.pms.persistent.enums;

/**
 * 计划类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum PlanType {

    DAY(0, "天",PeriodType.DAILY),
    WEEK(1, "周",PeriodType.WEEKLY),
    MONTH(2, "月",PeriodType.MONTHLY),
    QUARTER(3, "季度",PeriodType.QUARTERLY),
    YEAR(4, "年",PeriodType.YEARLY);

    private int value;

    private String name;

    private PeriodType periodType;

    PlanType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    PlanType(int value, String name, PeriodType periodType) {
        this.value = value;
        this.name = name;
        this.periodType = periodType;
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

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }
}
