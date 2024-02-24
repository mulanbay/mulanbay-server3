package cn.mulanbay.pms.persistent.enums;

/**
 * 地图字段
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum MapField {

    COUNTRY(0, "国家","country_id"),
    PROVINCE(1, "省份","province_id"),
    CITY(2, "城市","city_id"),
    DISTRICT(3, "县","district_id");

    private int value;

    private String name;

    private String field;

    MapField(int value, String name) {
        this.value = value;
        this.name = name;
    }

    MapField(int value, String name, String field) {
        this.value = value;
        this.name = name;
        this.field = field;
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
}
