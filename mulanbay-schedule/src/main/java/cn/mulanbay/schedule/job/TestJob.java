package cn.mulanbay.schedule.job;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.ScheduleCode;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ${DESCRIPTION}
 * 只供测试使用
 *
 * @author fenghong
 * @create 2017-10-19 10:13
 **/
public class TestJob extends AbstractBaseJob {

    private static final Logger logger = LoggerFactory.getLogger(TestJob.class);

    private TestJobPara para;

    @Override
    public TaskResult doTask() {
        long sleeps = para.getSleeps();
        if(sleeps>0){
            try {
                Thread.sleep(sleeps);
            } catch (Exception e) {
                logger.error("sleep error",e);
            }
        }
        if(para.isRandom()){
            int n = Integer.valueOf(NumberUtil.getRandNum(1));
            if(n<=3){
                return new TaskResult(JobResult.SUCCESS);
            }else if(n<=6){
                return new TaskResult(JobResult.FAIL);
            }else {
                return new TaskResult(JobResult.SKIP);
            }
        }else{
            return new TaskResult();
        }

    }

    @Override
    public ParaCheckResult checkTriggerPara() {
        ParaCheckResult rb = new ParaCheckResult();
        rb.setMessage("参数格式为：1. 存储过程名称 ,2. 时间参数类型（0不需要,1只需要单天,2一天的时间段,3一个月的时间段）");
        para = this.getTriggerParaBean();
        if(para==null){
            rb.setErrorCode(ScheduleCode.TRIGGER_PARA_NULL);
        }
        return rb;
    }

    @Override
    public Class getParaDefine() {
        return TestJobPara.class;
    }
}
