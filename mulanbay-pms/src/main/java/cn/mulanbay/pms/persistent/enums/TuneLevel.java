package cn.mulanbay.pms.persistent.enums;

/**
 * 曲子练习水平
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum TuneLevel {

    PRACTICE(0, "练习"),
    SKILLED(1, "熟练"),
    RECORD(2, "录音"),
    PERFORMANCE(3, "演奏");
    private int value;
    private String name;

    TuneLevel(int value, String name) {
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

    public static TuneLevel getLevel(int ordinal) {
        for (TuneLevel level : TuneLevel.values()) {
            if (level.ordinal() == ordinal) {
                return level;
            }
        }
        return null;
    }
}
