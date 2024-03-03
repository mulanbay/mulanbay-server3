package cn.mulanbay.pms.handler.job;

import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.schedule.para.AbstractTriggerPara;
import cn.mulanbay.schedule.para.EditType;
import cn.mulanbay.schedule.para.JobParameter;

public class SystemStatusJobPara extends AbstractTriggerPara {

    @JobParameter(name = "停止代码", dataType = Integer.class, desc = "", editType = EditType.NUMBER)
    private int code = PmsCode.SERVER_AUTO_STOP;

    @JobParameter(name = "提示消息", dataType = String.class, desc = "", editType = EditType.TEXT)
    private String message;

    @JobParameter(name = "停止时间", dataType = String.class, desc = "格式:01:00-02:00,多个以逗号分隔", editType = EditType.TEXT)
    private String stopPeriod;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStopPeriod() {
        return stopPeriod;
    }

    public void setStopPeriod(String stopPeriod) {
        this.stopPeriod = stopPeriod;
    }
}
