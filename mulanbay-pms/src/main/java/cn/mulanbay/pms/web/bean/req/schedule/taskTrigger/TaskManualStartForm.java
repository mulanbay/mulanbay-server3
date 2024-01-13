package cn.mulanbay.pms.web.bean.req.schedule.taskTrigger;

import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class TaskManualStartForm {

    private Long triggerId;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    private Date bussDate;

    private boolean sync;

    public Long getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(Long triggerId) {
        this.triggerId = triggerId;
    }

    public Date getBussDate() {
        return bussDate;
    }

    public void setBussDate(Date bussDate) {
        this.bussDate = bussDate;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }
}
