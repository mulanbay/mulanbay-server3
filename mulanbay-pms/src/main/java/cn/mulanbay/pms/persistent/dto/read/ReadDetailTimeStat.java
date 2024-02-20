package cn.mulanbay.pms.persistent.dto.read;

import java.util.Date;

public class ReadDetailTimeStat {

    //最先开始时间
    private Date minDate;

    //最晚开始时间
    private Date maxDate;

    public ReadDetailTimeStat(Date minDate, Date maxDate) {
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }
}
