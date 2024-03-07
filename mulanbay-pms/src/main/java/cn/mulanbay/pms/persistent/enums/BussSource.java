package cn.mulanbay.pms.persistent.enums;

import cn.mulanbay.pms.persistent.domain.*;

/**
 * 业务来源
 *
 * 对应的是具体的业务类
 */
public enum BussSource {

    MANUAL(0, "手动", UserCalendar.class),
    STAT(1, "提醒", UserStat.class),
    PLAN(2, "计划", UserPlan.class),
    COMMON_DATA(3, "通用", CommonData.class),
    BUDGET(4, "预算", Budget.class),
    TREAT_OPERATION(5, "手术", TreatOperation.class),
    TREAT_DRUG(6, "用药",TreatDrug.class),
    CONSUME(7, "消费",Consume.class),
    BUDGET_LOG(8, "预算日志", BudgetLog.class),
    DREAM(9, "梦想", Dream.class),
    OPERATION(10, "用户操作", OperLog.class),
    EXERCISE(11, "锻炼", Exercise.class),
    EXPERIENCE(12, "人生经历", Experience.class);

    private int value;

    private String name;

    private Class beanClass;

    BussSource(int value, String name) {
        this.value = value;
        this.name = name;
    }

    BussSource(int value, String name, Class beanClass) {
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


    public static BussSource getType(int ordinal) {
        for (BussSource bt : BussSource.values()) {
            if (bt.ordinal() == ordinal) {
                return bt;
            }
        }
        return null;
    }
}
