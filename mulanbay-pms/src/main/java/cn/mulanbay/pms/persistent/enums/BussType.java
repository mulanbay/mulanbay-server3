package cn.mulanbay.pms.persistent.enums;

import cn.mulanbay.pms.persistent.domain.Account;
import cn.mulanbay.pms.persistent.domain.Consume;
import cn.mulanbay.pms.persistent.domain.Exercise;
import cn.mulanbay.pms.persistent.domain.Income;

/**
 * 业务类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum BussType {

    CONSUME(0, Consume.class, "消费记录"),
    DREAM(1, "梦想"),
    LIFE_EXPERIENCE(2, "人生经历"),
    MUSIC_PRACTICE(3, "音乐练习"),
    READING_RECORD(4,  "阅读"),
    EXERCISE(5, Exercise.class,"锻炼"),
    TREAT_RECORD(6, "看病记录"),
    COMMON_RECORD(7, "通用记录"),
    DIARY(8, "日记"),
    WORK_OVER_TIME(9,  "加班记录"),
    SLEEP(10, "睡眠"),
    DIET(11, "饮食"),
    ACCOUNT(12, Account.class, "资金"),
    INCOME(13, Income.class,"收入");

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
