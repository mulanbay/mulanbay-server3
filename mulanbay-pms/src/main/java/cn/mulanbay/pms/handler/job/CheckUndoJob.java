package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.pms.handler.PmsScheduleHandler;
import cn.mulanbay.pms.persistent.dto.schedule.CheckLogDTO;
import cn.mulanbay.pms.persistent.service.PmsScheduleService;
import cn.mulanbay.schedule.para.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.domain.TaskTrigger;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.enums.TriggerType;
import cn.mulanbay.schedule.job.AbstractBaseJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 检查没有执行的任务
 * @author fenghong
 * @date 2024/2/21
 */
public class CheckUndoJob extends AbstractBaseJob {

    private static final Logger logger = LoggerFactory.getLogger(CheckUndoJob.class);

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
        for(CheckLogDTO dto : list){
            TriggerType type = TriggerType.getType(dto.getTriggerType().intValue());
            String key = this.getKey(dto.getTriggerId(),type,dto.getBussDate());
            logMap.put(key,dto.getLogId());
        }
        int success = 0;
        int fail = 0;
        for(Long triggerId : triggerIdList){
            TaskTrigger trigger = service.getTaskTrigger(triggerId);
            TriggerType type = trigger.getTriggerType();
            Date dd = minDate;
            while (dd.before(bussDay)){
                String key = this.getKey(triggerId,type,dd);
                Long log = logMap.get(key);
                if(log==null){
                    //手动执行
                    boolean b = this.startJob(triggerId,dd);
                    if (b) {
                        success++;
                    } else {
                        fail++;
                    }
                    counts++;
                }
                //获取下一个时间
                dd = this.getNextDate(type,dd);
            }
        }
        tr.setResult(JobResult.SUCCESS);
        tr.setComment("一共补全了" + counts + "条调度任务,成功:" + success + "个,失败" + fail + "个");
        return tr;
    }

    /**
     * 启动任务
     * @param triggerId
     * @param date
     * @return
     */
    private boolean startJob(Long triggerId,Date date){
        try {
            PmsScheduleHandler scheduleHandler = BeanFactoryUtil.getBean(PmsScheduleHandler.class);
            scheduleHandler.manualStart(triggerId,date,false,null,"调度任务自检Job发起");
            return true;
        } catch (Exception e) {
            logger.error("启动调度任务异常,triggerId="+triggerId,e);
            return false;
        }
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
     * key
     * @param triggerId
     * @param triggerType
     * @param date
     * @return
     */
    private String getKey(Long triggerId,TriggerType triggerType,Date date){
        return triggerId+"_"+this.getBussKey(triggerType,date);
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
