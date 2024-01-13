package cn.mulanbay.pms.web.controller.schedule;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.handler.PmsScheduleHandler;
import cn.mulanbay.pms.persistent.service.PmsScheduleService;
import cn.mulanbay.pms.web.bean.req.schedule.taskLog.TaskLogCostTimeStatSH;
import cn.mulanbay.pms.web.bean.req.schedule.taskLog.TaskLogSH;
import cn.mulanbay.pms.web.bean.res.chart.ChartData;
import cn.mulanbay.pms.web.bean.res.chart.ChartYData;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.schedule.domain.TaskLog;
import cn.mulanbay.schedule.domain.TaskTrigger;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 调度日志
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/taskLog")
public class TaskLogController extends BaseController {

    private static Class<TaskLog> beanClass = TaskLog.class;

    @Autowired
    PmsScheduleHandler pmsScheduleHandler;

    @Autowired
    PmsScheduleService pmsScheduleService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(TaskLogSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s1 = new Sort("startTime", Sort.DESC);
        pr.addSort(s1);
        PageResult<TaskLog> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 重做
     *
     * @return
     */
    @RequestMapping(value = "/redo", method = RequestMethod.GET)
    public ResultBean redo(@RequestParam(name = "logId") Long logId) {
        pmsScheduleHandler.manualRedo(logId, false);
        return callback(null);
    }

    /**
     * 花费时间统计
     *
     * @return
     */
    @RequestMapping(value = "/costTimeStat")
    public ResultBean costTimeStat(TaskLogCostTimeStatSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s1 = new Sort("startTime", Sort.ASC);
        pr.addSort(s1);
        PageResult<TaskLog> qr = baseService.getBeanResult(pr);
        TaskTrigger tt = baseService.getObject(TaskTrigger.class, sf.getTriggerId());
        ChartData chartData = new ChartData();
        chartData.setTitle("[" + tt.getTriggerName() + "]执行时间统计");
        chartData.setUnit("毫秒");
        chartData.setLegendData(new String[]{"时长"});
        ChartYData yData1 = new ChartYData("时长","毫秒");
        List<TaskLog> list = qr.getBeanList();
        for (TaskLog bean : list) {
            //chartData.getIntXData().add(1);
            chartData.getXdata().add(DateUtil.getFormatDate(bean.getStartTime(), DateUtil.Format24Datetime));
            yData1.getData().add(bean.getCostTime());
        }
        chartData.getYdata().add(yData1);
        return callback(chartData);
    }

    /**
     * 获取最近一次的调度日志
     *
     * @return
     */
    @RequestMapping(value = "/lastTaskLog", method = RequestMethod.GET)
    public ResultBean lastTaskLog(@RequestParam(name = "triggerId") Long triggerId) {
        return callback(pmsScheduleService.getLastTaskLog(triggerId));
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "logId") Long logId) {
        TaskLog br = baseService.getObject(beanClass, logId);
        return callback(br);
    }

}
