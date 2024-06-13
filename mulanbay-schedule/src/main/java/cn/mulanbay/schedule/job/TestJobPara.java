package cn.mulanbay.schedule.job;

import cn.mulanbay.schedule.para.AbstractTriggerPara;
import cn.mulanbay.schedule.para.EditType;
import cn.mulanbay.schedule.para.JobParameter;

public class TestJobPara extends AbstractTriggerPara {

    @JobParameter(name = "休息时间",dataType = Integer.class,desc = "毫秒",editType = EditType.NUMBER)
    private long sleeps;

    @JobParameter(name = "是否随机",dataType = Boolean.class,desc = "随机",editType = EditType.BOOLEAN)
    private boolean random=false;

    public long getSleeps() {
        return sleeps;
    }

    public void setSleeps(long sleeps) {
        this.sleeps = sleeps;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }
}
