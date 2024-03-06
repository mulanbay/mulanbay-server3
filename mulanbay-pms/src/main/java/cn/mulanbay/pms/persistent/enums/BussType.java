package cn.mulanbay.pms.persistent.enums;

import cn.mulanbay.pms.persistent.domain.*;

/**
 * 业务类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum BussType {

    CONSUME(0, Consume.class, "消费"),
    DREAM(1, Dream.class, "梦想"),
    EXPERIENCE(2, Experience.class, "人生经历"),
    MUSIC_PRACTICE(3, MusicPractice.class, "音乐练习"),
    BOOK(4, Book.class,  "阅读"),
    EXERCISE(5, Exercise.class,"锻炼"),
    TREAT(6, Treat.class, "健康"),
    COMMON_DATA(7, CommonData.class, "通用记录"),
    DIARY(8, "日记"),
    WORK_OVER_TIME(9, WorkOvertime.class,  "加班"),
    SLEEP(10, Sleep.class, "睡眠"),
    DIET(11, Diet.class, "饮食"),
    ACCOUNT(12, Account.class, "资金"),
    INCOME(13, Income.class,"收入"),
    BODY_INFO(14, BodyInfo.class,"身体数据"),
    BUDGET_LOG(15, BudgetLog.class,"预算日志");

    private int value;

    private Class beanClass;

    private String name;

    BussType(int value, Class beanClass, String name) {
        this.value = value;
        this.beanClass = beanClass;
        this.name = name;
    }

    BussType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
