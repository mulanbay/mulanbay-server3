package cn.mulanbay.pms.persistent.dto.schedule;

import java.util.Date;

/**
 * @author fenghong
 * @date 2024/3/5
 */
public class CheckLogDTO {

    private Long logId;

    private Long triggerId;

    private Date bussDate;

    private Short triggerType;

    public CheckLogDTO(Long logId, Long triggerId, Date bussDate) {
        this.logId = logId;
        this.triggerId = triggerId;
        this.bussDate = bussDate;
    }

    public CheckLogDTO(Long logId, Long triggerId, Date bussDate, Short triggerType) {
        this.logId = logId;
        this.triggerId = triggerId;
        this.bussDate = bussDate;
        this.triggerType = triggerType;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

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

    public Short getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(Short triggerType) {
        this.triggerType = triggerType;
    }
}
