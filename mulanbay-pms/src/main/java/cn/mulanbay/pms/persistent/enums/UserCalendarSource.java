package cn.mulanbay.pms.persistent.enums;

import cn.mulanbay.pms.persistent.domain.*;

/**
 * 日历来源
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum UserCalendarSource {

    MANUAL(0, "手动", UserCalendar.class),
    STAT(1, "提醒", UserStat.class),
    //PLAN(2, "计划", UserPlan.class),
    COMMON_DATA(3, "通用", CommonData.class),
    BUDGET(4, "预算", Budget.class),
    TREAT_OPERATION(5, "手术", TreatOperation.class),
    TREAT_DRUG(6, "用药",TreatDrug.class),
    CONSUME(7, "消费",Consume.class);

    private int value;

    private String name;

    private Class beanClass;

    UserCalendarSource(int value, String name) {
        this.value = value;
        this.name = name;
    }

    UserCalendarSource(int value, String name, Class beanClass) {
        this.value = value;
        this.name = name;
        this.beanClass = beanClass;
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

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

}
