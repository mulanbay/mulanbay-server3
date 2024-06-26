package cn.mulanbay.pms.handler.job;

import cn.mulanbay.schedule.para.AbstractTriggerPara;
import cn.mulanbay.schedule.para.EditType;
import cn.mulanbay.schedule.para.JobParameter;

public class SendRedisDelayMessageJobPara extends AbstractTriggerPara {

    @JobParameter(name = "发送失败最大次数", dataType = Integer.class, desc = "次", editType = EditType.NUMBER)
    private int maxFails = 3;

    @JobParameter(name = "发送失败后延迟秒数", dataType = Integer.class, desc = "秒", editType = EditType.NUMBER)
    private int delaySeconds = 60;

    @JobParameter(name = "失效时间", dataType = Integer.class, desc = "秒", editType = EditType.NUMBER)
    private int expires = 3*3600*24;

    public int getMaxFails() {
        return maxFails;
    }

    public void setMaxFails(int maxFails) {
        this.maxFails = maxFails;
    }

    public int getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }
}
