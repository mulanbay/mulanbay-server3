package cn.mulanbay.pms.persistent.enums;

/**
 * 资源类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum ResourcesType {

    PICTURE(0, "图片"),
    VIDEO(1, "视频");

    private int value;

    private String name;

    ResourcesType(int value, String name) {
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
