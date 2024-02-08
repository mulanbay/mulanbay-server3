package cn.mulanbay.pms.web.bean.req;

/**
 * 分组字段汇总定义
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum GroupType {
    COUNT(0, "次数", "","次"),
    TOTAL_PRICE(1, "花费", "total_price","元"),
    SHIPMENT(2, "运费", "shipment","元"),
    HOURS(3, "时长","小时","小时"),
    VALUE(4, "锻炼值", "value", "值"),
    DURATION(5, "锻炼时间", "duration", "分钟"),
    SPEED(6, "平均速度", "speed", "公里/小时"),
    PACE(7, "平均配速", "pace", "分钟/公里"),
    MAX_HEART_RATE(8, "最大心率", "maxHeartRate", "次/分钟"),
    AVG_HEART_RATE(9, "平均心率", "averageHeartRate", "次/分钟"),
    HEIGHT(10, "身高","","CM"),
    WEIGHT(11, "体重","","KG"),
    BMI(12, "BMI"),
    DAYS(13, "天数","天","天"),
    PAYMENT(14, "支付方式"),
    SHOP_NAME(15, "店铺"),
    MAX_SPEED(16, "最佳速度", "maxSpeed","公里/小时"),
    MAX_PACE(17, "最佳配速", "maxPace","分钟/公里"),
    EXERCISE_TIME(18, "锻炼日期", "exerciseTime");

    private int value;
    private String name;
    private String field;
    private String unit;

    GroupType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    GroupType(int value, String name, String field) {
        this.value = value;
        this.name = name;
        this.field = field;
    }

    GroupType(int value, String name, String field, String unit) {
        this.value = value;
        this.name = name;
        this.field = field;
        this.unit = unit;
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

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
