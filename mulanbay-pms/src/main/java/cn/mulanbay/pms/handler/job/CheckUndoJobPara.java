package cn.mulanbay.pms.handler.job;

import cn.mulanbay.schedule.para.AbstractTriggerPara;
import cn.mulanbay.schedule.para.JobParameter;

/**
 * 检查没有执行的任务的参数定义
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class CheckUndoJobPara extends AbstractTriggerPara {

    @JobParameter(name = "检查时长", dataType = Integer.class, desc = "天数",notNull = false)
    private int days = 30;

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
