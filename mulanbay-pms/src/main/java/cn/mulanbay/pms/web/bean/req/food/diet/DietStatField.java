package cn.mulanbay.pms.web.bean.req.food.diet;

public enum DietStatField {

    FOODS("foods"),
    TAGS("tags"),
    SHOP("shop"),
    CLASS_NAME("class_name"),
    TYPE("type"),
    DIET_SOURCE("diet_source"),
    FOOD_TYPE("food_type"),
    DIET_TYPE("diet_type");
    private String fieldName;

    DietStatField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

}
