package cn.mulanbay.pms.handler.bean.data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户持续操作的缓存类
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class UserOpBean implements Serializable {
    private static final long serialVersionUID = 2139963442875877148L;

    //第一次时间
    private Date fistDay;

    //最近的时间
    private Date lastDay;

    //持续天数
    private int days;

    public void addDay() {
        days++;
    }

    public Date getFistDay() {
        return fistDay;
    }

    public void setFistDay(Date fistDay) {
        this.fistDay = fistDay;
    }

    public Date getLastDay() {
        return lastDay;
    }

    public void setLastDay(Date lastDay) {
        this.lastDay = lastDay;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
