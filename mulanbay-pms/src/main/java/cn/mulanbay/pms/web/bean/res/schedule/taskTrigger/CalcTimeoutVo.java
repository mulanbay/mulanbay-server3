package cn.mulanbay.pms.web.bean.res.schedule.taskTrigger;

import cn.mulanbay.pms.persistent.dto.life.NameCountDTO;
import cn.mulanbay.pms.web.bean.res.NameValueVo;
import cn.mulanbay.schedule.domain.TaskTrigger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fenghong
 * @date 2024/6/13
 */
public class CalcTimeoutVo {

    private TaskTrigger trigger;

    private Integer days;

    private List<NameValueVo> valueList = new ArrayList<>();

    public void addTimeout(String name,Long value){
        NameValueVo vo = new NameValueVo(name,value);
        valueList.add(vo);
    }

    public TaskTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(TaskTrigger trigger) {
        this.trigger = trigger;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public List<NameValueVo> getValueList() {
        return valueList;
    }

    public void setValueList(List<NameValueVo> valueList) {
        this.valueList = valueList;
    }
}
