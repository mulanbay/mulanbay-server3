package cn.mulanbay.pms.persistent.enums;

/**
 * 人生经历花费统计类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum ExerciseItemStatField {

    VALUE("锻炼值", "value",""),
    DURATION("锻炼时间", "duration","分钟"),
    SPEED("速度", "speed","公里/小时"),
    MAX_SPEED("最大速度", "max_speed","公里/小时"),
    PACE("配速", "pace","分钟/公里"),
    MAX_PACE("最大配速", "max_pace","分钟/公里"),
    MAX_HEART_RACE("最大心率", "max_heart_rate","下/分钟"),
    AVG_HEART_RACE("平均心率", "avg_heart_rate","下/分钟");

    private String name;

    private String field;

    private String unit;

    ExerciseItemStatField(String name, String field, String unit) {
        this.name = name;
        this.field = field;
        this.unit = unit;
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

    /**
     * 通用解析使用
     * @return
     */
    public String getValue(){
        return field;
    }
}
