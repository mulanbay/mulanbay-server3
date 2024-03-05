package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.pms.handler.PmsScheduleHandler;
import cn.mulanbay.pms.persistent.dto.schedule.CheckLogDTO;
import cn.mulanbay.pms.persistent.service.PmsScheduleService;
import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.domain.TaskTrigger;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.enums.TriggerType;
import cn.mulanbay.schedule.job.AbstractBaseJob;

import java.util.*;

/**
 * 检查没有执行的任务
 * @author fenghong
 * @date 2024/2/21
 */
public class CheckUndoJob extends AbstractBaseJob {

    CheckUndoJobPara para;

    @Override
    public TaskResult doTask() {
        TaskResult tr = new TaskResult();
        PmsScheduleService service = BeanFactoryUtil.getBean(PmsScheduleService.class);
        List<Long> triggerIdList = service.getCheckTriggerIdList();
        if(StringUtil.isEmpty(triggerIdList)){
            tr.setResult(JobResult.SKIP);
            tr.setComment("待检查的调度器为空");
            return tr;
        }
        Date bussDay = this.getBussDay();
        Date minDate = DateUtil.getDate(-para.getDays(),bussDay);
        List<CheckLogDTO> list = service.getCheckLogList(minDate,bussDay);
        Map<String,Long> logMap = new HashMap<>();
        int counts =0;
        PmsScheduleHandler scheduleHandler = BeanFactoryUtil.getBean(PmsScheduleHandler.class);
        for(CheckLogDTO dto : list){
            TriggerType type = TriggerType.getType(dto.getTriggerType().intValue());
            String key = dto.getTriggerId()+"_"+this.getBussKey(type,dto.getBussDate());
            logMap.put(key,dto.getLogId());
        }
        for(Long triggerId : triggerIdList){
            TaskTrigger trigger = service.getTaskTrigger(triggerId);
            TriggerType type = trigger.getTriggerType();
            Date dd = minDate;
            while (dd.before(bussDay)){
                String key = triggerId+"_"+this.getBussKey(type,dd);
                Long log = logMap.get(key);
                if(log==null){
                    //手动执行
                    scheduleHandler.manualStart(triggerId,dd,false,null,"调度任务自检Job发起");
                    counts++;
                }
                //获取下一个时间
                dd = this.getNextDate(type,dd);
            }
        }
        tr.setResult(JobResult.SUCCESS);
        tr.setComment("一共补全了"+counts+"条调度任务");
        return tr;
    }

    private Date getNextDate(TriggerType triggerType,Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        switch (triggerType){
            case YEAR -> {
                c.add(Calendar.YEAR, 1);
            }
            case SEASON -> {
                c.add(Calendar.MONTH, 3);
            }
            case MONTH -> {
                c.add(Calendar.MONTH, 1);
            }
            case WEEK -> {
                c.add(Calendar.DATE, 7);
            }
            case DAY -> {
                c.add(Calendar.DATE, 1);
            }
            case HOUR -> {
                c.add(Calendar.HOUR, 1);
            }
            case MINUTE -> {
                c.add(Calendar.MINUTE, 1);
            }
            case NOW,SECOND,CRON-> {
                //不支持
            }
        }
        return c.getTime();
    }

    /**
     * 业务KEY
     * @param triggerType
     * @param date
     * @return
     */
    private String getBussKey(TriggerType triggerType,Date date){
        String bussKey = null;
        switch (triggerType){
            case YEAR -> {
                bussKey = DateUtil.getFormatDate(date,"yyyy");
            }
            case SEASON -> {
                bussKey = DateUtil.getFormatDate(date,"yyyy");
                int month = DateUtil.getMonth(date);
                bussKey+=("-S"+((month+2)/3));
            }
            case MONTH -> {
                Date dd = DateUtil.getMonthFirst(date);
                bussKey = DateUtil.getFormatDate(dd,"yyyyMM");
            }
            case WEEK -> {
                bussKey = DateUtil.getFormatDate(date,"yyyy");
                bussKey+=("-W"+DateUtil.getWeek(date));
            }
            case DAY -> {
                bussKey = DateUtil.getFormatDate(date,"yyyyMMdd");
                int month = DateUtil.getMonth(date);
                bussKey+=("-S"+((month+2)/3));
            }
            case HOUR -> {
                bussKey = DateUtil.getFormatDate(date,"yyyyMMddHH");
            }
            case MINUTE -> {
                bussKey = DateUtil.getFormatDate(date,"yyyyMMddHHmm");
            }
            case NOW,SECOND,CRON-> {
                bussKey = DateUtil.getFormatDate(date,"yyyyMMddHHmmss");
            }
        }
        return bussKey;
    }

    @Override
    public ParaCheckResult checkTriggerPara() {
        para = this.getTriggerParaBean();
        if (para == null) {
            para = new CheckUndoJobPara();
        }
        return DEFAULT_SUCCESS_PARA_CHECK;
    }

    @Override
    public Class getParaDefine() {
        return CheckUndoJobPara.class;
    }
}
