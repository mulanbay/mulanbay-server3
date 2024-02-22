package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.pms.persistent.domain.DBClean;
import cn.mulanbay.pms.persistent.service.DBCleanService;
import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;

import java.util.List;

/**
 * 数据库清理任务
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class DBCleanJob extends AbstractBaseJob {

    @Override
    public TaskResult doTask() {
        DBCleanService dbCleanService = BeanFactoryUtil.getBean(DBCleanService.class);
        List<DBClean> list = dbCleanService.getActiveList();
        for (DBClean dc : list) {
            dbCleanService.exeClean(dc);
        }
        return new TaskResult(JobResult.SUCCESS);
    }

    @Override
    public ParaCheckResult checkTriggerPara() {
        return DEFAULT_SUCCESS_PARA_CHECK;
    }

    @Override
    public Class getParaDefine() {
        return null;
    }
}
