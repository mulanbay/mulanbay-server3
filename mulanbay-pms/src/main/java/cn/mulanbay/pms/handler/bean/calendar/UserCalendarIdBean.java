package cn.mulanbay.pms.handler.bean.calendar;

import cn.mulanbay.pms.persistent.enums.BussType;

/**
 * @author fenghong
 * @title: UserCalendarIdBean
 * @description: TODO
 * @date 2023/5/17 22:36
 */
public class UserCalendarIdBean {

    private BussType source;

    private Long id;

    public BussType getSource() {
        return source;
    }

    public void setSource(BussType source) {
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
