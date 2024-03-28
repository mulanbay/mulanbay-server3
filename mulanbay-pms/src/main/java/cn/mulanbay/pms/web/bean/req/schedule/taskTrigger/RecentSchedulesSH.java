package cn.mulanbay.pms.web.bean.req.schedule.taskTrigger;

import cn.mulanbay.web.bean.request.PageSearch;

/**
 * @author fenghong
 * @date 2024/3/10
 */
public class RecentSchedulesSH extends PageSearch {

    /**
     * 距离现在的小时数
     */
    private Integer hours = 24;

    private Boolean period = true;

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Boolean isPeriod() {
        return period;
    }

    public void setPeriod(Boolean period) {
        this.period = period;
    }
}
