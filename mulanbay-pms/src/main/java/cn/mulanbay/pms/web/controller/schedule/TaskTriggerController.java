package cn.mulanbay.pms.web.controller.schedule;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.queue.LimitQueue;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.StringCoderUtil;
import cn.mulanbay.pms.handler.PmsScheduleHandler;
import cn.mulanbay.pms.persistent.dto.schedule.TaskTriggerStat;
import cn.mulanbay.pms.persistent.service.PmsScheduleService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.schedule.taskTrigger.*;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.bean.res.schedule.taskTrigger.TaskTriggerVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.schedule.ScheduleCode;
import cn.mulanbay.schedule.ScheduleInfo;
import cn.mulanbay.schedule.domain.TaskTrigger;
import cn.mulanbay.schedule.enums.TriggerStatus;
import cn.mulanbay.schedule.job.AbstractBaseJob;
import cn.mulanbay.schedule.para.ParaBuilder;
import cn.mulanbay.schedule.para.ParaDefine;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 调度触发器
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/taskTrigger")
public class TaskTriggerController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(TaskTriggerController.class);

    private static Class<TaskTrigger> beanClass = TaskTrigger.class;

    @Autowired
    PmsScheduleHandler pmsScheduleHandler;

    @Autowired
    PmsScheduleService pmsScheduleService;

    /**
     * 获取调度触发器列表
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(@RequestParam(name = "needRoot") Boolean needRoot) {
        try {
            TaskTriggerSH sf = new TaskTriggerSH();
            sf.setPage(BaseHibernateDao.NO_PAGE);
            //sf.setGroupName(groupName);
            PageRequest pr = sf.buildQuery();
            pr.setBeanClass(beanClass);
            Sort s1 = new Sort("groupName", Sort.ASC);
            Sort s2 = new Sort("nextExecuteTime", Sort.ASC);
            pr.addSort(s1);
            pr.addSort(s2);
            PageResult<TaskTrigger> qr = baseService.getBeanResult(pr);
            List<TaskTrigger> gtList = qr.getBeanList();

            List<TreeBean> result = new ArrayList<>();
            String current = gtList.get(0).getGroupName();
            TreeBean typeTreeBean = new TreeBean();
            typeTreeBean.setId(current);
            typeTreeBean.setText(current);
            int n = gtList.size();
            for (int i = 0; i < n; i++) {
                TaskTrigger pc = gtList.get(i);
                TreeBean tb = new TreeBean();
                tb.setId(pc.getTriggerId());
                tb.setText(pc.getTriggerName());
                if (pc.getGroupName().equals(current)) {
                    typeTreeBean.addChild(tb);
                }
                if (!pc.getGroupName().equals(current)) {
                    current = pc.getGroupName();
                    result.add(typeTreeBean);
                    typeTreeBean = new TreeBean();
                    typeTreeBean.setId(current);
                    typeTreeBean.setText(current);
                    typeTreeBean.addChild(tb);
                }
                if (i == n - 1) {
                    //最后一个
                    result.add(typeTreeBean);
                }
            }
            return callback(TreeBeanUtil.addRoot(result, needRoot));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取调度触发器列表异常",
                    e);
        }
    }

    /**
     * 获取名称或者分组的各种分类归类
     *
     * @return
     */
    @RequestMapping(value = "/categoryTree")
    public ResultBean categoryTree(TaskTriggerCategorySH sf) {
        try {
            List<String> categoryList = pmsScheduleService.getTaskTriggerCategoryList(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            int i = 0;
            for (String ss : categoryList) {
                TreeBean tb = new TreeBean();
                tb.setId(ss);
                tb.setText(ss);
                list.add(tb);
                i++;
            }
            return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取名称或者分组的各种分类归类异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(TaskTriggerSH sf) {
        boolean isSchedule = pmsScheduleHandler.isEnableSchedule();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("isSchedule", isSchedule);
        data.put("scheduleStatus", pmsScheduleHandler.getScheduleStatus());
        PageResult<TaskTrigger> pr = getTaskTrigger(sf);
        List<TaskTrigger> beanList = pr.getBeanList();
        List<TaskTriggerVo> newList = new ArrayList<>();
        for (TaskTrigger tt : beanList) {
            TaskTriggerVo tb = new TaskTriggerVo();
            BeanCopy.copy(tt, tb);
            if (tt.getTriggerStatus() == TriggerStatus.ENABLE) {
                boolean executing = pmsScheduleHandler.isExecuting(tt.getTriggerId());
                tb.setExecuting(executing);
            }
            newList.add(tb);
        }
        data.put("total", pr.getMaxRow());
        data.put("rows", newList);
        data.put("currentlyExecutingJobsCount",
                pmsScheduleHandler.getCurrentlyExecutingJobsCount());
        data.put("scheduleJobsCount", pmsScheduleHandler.getScheduleJobsCount());
        return callback(data);
    }

    private PageResult<TaskTrigger> getTaskTrigger(TaskTriggerSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s1 = new Sort("triggerStatus", Sort.DESC);
        Sort s2 = new Sort("nextExecuteTime", Sort.ASC);
        pr.addSort(s1);
        pr.addSort(s2);
        PageResult<TaskTrigger> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid TaskTriggerForm formRequest) {
        TaskTrigger bean = new TaskTrigger();
        BeanCopy.copy(formRequest, bean);
        bean.setFailCount(0L);
        bean.setTotalCount(0L);
        bean.setCreatedTime(new Date());
        String triggerParas = StringCoderUtil.decodeJson(formRequest.getTriggerParas());
        String execTimePeriods = StringCoderUtil.decodeJson(formRequest.getExecTimePeriods());
        bean.setTriggerParas(triggerParas);
        bean.setExecTimePeriods(execTimePeriods);
        baseService.saveObject(bean);
        return callback(null);
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "triggerId") Long triggerId) {
        TaskTrigger br = baseService.getObject(beanClass, triggerId);
        return callback(br);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid TaskTriggerForm formRequest) {
        TaskTrigger bean = baseService.getObject(beanClass, formRequest.getTriggerId());
        BeanCopy.copy(formRequest, bean);
        String triggerParas = StringCoderUtil.decodeJson(formRequest.getTriggerParas());
        String execTimePeriods = StringCoderUtil.decodeJson(formRequest.getExecTimePeriods());
        bean.setTriggerParas(triggerParas);
        bean.setExecTimePeriods(execTimePeriods);
        bean.setModifyTime(new Date());
        baseService.updateObject(bean);
        return callback(null);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        baseService.deleteObjects(beanClass, NumberUtil.stringToLongArray(deleteRequest.getIds()));
        return callback(null);
    }

    /**
     * 手动执行
     *
     * @return
     */
    @RequestMapping(value = "/manualStart", method = RequestMethod.POST)
    public ResultBean manualStart(@RequestBody TaskManualStartForm tmnr) {
        pmsScheduleHandler.manualStart(tmnr.getTriggerId(), tmnr.getBussDate(), tmnr.isSync(), null, null);
        return callback(null);
    }

    /**
     * 获取参数定义
     *
     * @return
     */
    @RequestMapping(value = "/paraDefine", method = RequestMethod.GET)
    public ResultBean paraDefine(@RequestParam(name = "className") String className) {
        try {
            AbstractBaseJob job = (AbstractBaseJob) Class.forName(className).getDeclaredConstructor().newInstance();
            Class cls = job.getParaDefine();
            if (cls == null) {
                return callbackErrorInfo("该Job没有参数配置项.");
            }
            List<ParaDefine> list = ParaBuilder.buildDefine(cls);
            if (StringUtil.isEmpty(list)) {
                return callbackErrorInfo("该调度没有参数定义，无需配置");
            }
            Object defaultBean = Class.forName(job.getParaDefine().getName()).getDeclaredConstructor().newInstance();
            String defaultConfig = JsonUtil.beanToJson(defaultBean);
            Map res = new HashMap();
            res.put("paraDefine", list);
            res.put("defaultConfig", defaultConfig);
            return callback(res);
        } catch (Exception e) {
            logger.error("getParaDefine异常", e);
            return callbackErrorInfo(e.getMessage());
        }
    }

    /**
     * 设置触发器状态
     *
     * @return
     */
    @RequestMapping(value = "/editStatus", method = RequestMethod.POST)
    public ResultBean editStatus(@RequestBody @Valid EditTriggerStatusForm ets) {
        TaskTrigger bean = baseService.getObject(beanClass, ets.getTriggerId());
        if(bean.getTriggerStatus()==ets.getTriggerStatus()){
            return callbackErrorInfo("状态未改变");
        }
        bean.setTriggerStatus(ets.getTriggerStatus());
        bean.setModifyTime(new Date());
        baseService.updateObject(bean);
        return callback(null);
    }

    /**
     * 设置调度状态
     *
     * @return
     */
    @RequestMapping(value = "/changeScheduleStatus", method = RequestMethod.POST)
    public ResultBean changeScheduleStatus(@RequestBody @Valid ChangeStatusForm cssr) {
        boolean enableSchedule = pmsScheduleHandler.isEnableSchedule();
        if (!enableSchedule) {
            return callbackErrorInfo("调度未开启，无法进行设置状态");
        }
        pmsScheduleHandler.setScheduleStatus(cssr.isAfterStatus());
        return callback(null);
    }

    /**
     * 刷新调度
     *
     * @return
     */
    @RequestMapping(value = "/refreshSchedule", method = RequestMethod.POST)
    public ResultBean refreshSchedule(@RequestBody @Valid RefreshScheduleForm rsr) {
        boolean enableSchedule = pmsScheduleHandler.isEnableSchedule();
        if (!enableSchedule) {
            return callbackErrorInfo("调度未开启，无法进行刷新");
        }
        boolean b;
        if (rsr.getTriggerId() != null) {
            //单个
            TaskTrigger tt = pmsScheduleService.getTaskTrigger(rsr.getTriggerId());
            boolean cr = pmsScheduleHandler.checkCanRun(tt);
            if (!cr) {
                return callbackErrorCode(ScheduleCode.TRIGGER_CANNOT_RUN_HERE);
            }
            b = pmsScheduleHandler.refreshTask(tt);
        } else {
            b = pmsScheduleHandler.checkAndRefreshSchedule(rsr.getForce());
        }
        if (b) {
            return callback(null);
        } else {
            return callbackErrorCode(ScheduleCode.REFRESH_TRIGGER_FAIL);
        }
    }

    /**
     * 获取调度信息
     *
     * @return
     */
    @RequestMapping(value = "/scheduleInfo", method = RequestMethod.GET)
    public ResultBean scheduleInfo() {
        ScheduleInfo si = pmsScheduleHandler.getScheduleInfo();
        List<TaskTrigger> ceJobs = pmsScheduleHandler.getCurrentlyExecutingJobs();
        Map res = new HashMap();
        res.put("scheduleInfo", si);
        res.put("currentlyExecutingJobs", ceJobs);
        return callback(res);
    }

    /**
     * 重置调度总执行次数
     *
     * @return
     */
    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public ResultBean reset(@RequestBody @Valid TriggerResetForm trf) {
        //单个
        TaskTrigger tt = pmsScheduleService.getTaskTrigger(trf.getTriggerId());
        boolean cr = pmsScheduleHandler.checkCanRun(tt);
        if (!cr) {
            return callbackErrorCode(ScheduleCode.TRIGGER_CANNOT_RUN_HERE);
        }
        tt.setTotalCount(trf.getTotalCount());
        tt.setFailCount(trf.getFailCount());
        tt.setModifyTime(new Date());
        baseService.updateObject(tt);
        //需要重新加载
        pmsScheduleHandler.refreshTask(trf.getTriggerId());
        return callback(null);
    }

    /**
     * 获取调度信息
     *
     * @return
     */
    @RequestMapping(value = "/scheduleDetail", method = RequestMethod.GET)
    public ResultBean scheduleDetail(@RequestParam(name = "triggerId") Long triggerId) {
        Map<String, Object> res = new HashMap<>();
        TaskTrigger dbInfo = pmsScheduleService.getTaskTrigger(triggerId);
        res.put("dbInfo", dbInfo);
        TaskTrigger scheduleInfo = pmsScheduleHandler.getScheduledTaskTrigger(dbInfo.getTriggerId(), dbInfo.getGroupName());
        res.put("scheduleInfo", scheduleInfo);
        Date addToScheduleTime = pmsScheduleHandler.getAddTime(dbInfo.getTriggerId(), dbInfo.getGroupName());
        res.put("addToScheduleTime", addToScheduleTime);
        boolean isExecuting = pmsScheduleHandler.isTaskTriggerExecuting(dbInfo.getTriggerId());
        res.put("isExecuting", isExecuting);
        return callback(res);
    }

    /**
     * 最近的调度
     *
     * @return
     */
    @RequestMapping(value = "/recentSchedules", method = RequestMethod.GET)
    public ResultBean recentSchedules(RecentSchedulesSH sf) {
        Date start = new Date();
        Date end = new Date(start.getTime()+sf.getHours()*3600*1000L);
        List<TaskTrigger> list = pmsScheduleService.getRecentSchedules(start,end,sf.getPage(),sf.getPageSize(),sf.isPeriod());
        return callback(list);
    }

    /**
     * 统计分析
     *
     * @return
     */
    @RequestMapping(value = "/stat")
    public ResultBean stat(TaskTriggerStatSH sf){
        List<TaskTriggerStat> list = pmsScheduleService.getTaskTriggerStat(sf);
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("调度任务分析");
        chartPieData.setUnit("个");
        ChartPieSerieData seriesData = new ChartPieSerieData();
        seriesData.setName("类别");
        for (TaskTriggerStat bean : list) {
            chartPieData.getXdata().add(bean.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(bean.getName());
            dataDetail.setValue(bean.getTotalCount());
            seriesData.getData().add(dataDetail);
        }
        chartPieData.getDetailData().add(seriesData);
        return callback(chartPieData);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/scheduleStat", method = RequestMethod.GET)
    public ResultBean scheduleStat() {
        LimitQueue<ScheduleInfo> queue = pmsScheduleHandler.getMonitorQueue();
        if (queue == null) {
            queue = new LimitQueue<>(0);
        }
        ChartData chartData = new ChartData();
        chartData.setTitle("调度监控");
        chartData.setLegendData(new String[]{"调度任务数", "正在运行调度任务数","线程池活跃数","线程池已执行任务数"});
        chartData.setUnit("个");
        ChartYData yData1 = new ChartYData("调度任务数","个");
        ChartYData yData2 = new ChartYData("正在运行调度任务数","个");
        ChartYData yData3 = new ChartYData("线程池活跃数","个");
        ChartYData yData4 = new ChartYData("线程池已执行任务数","个");
        int n = queue.size();
        for (int i = 0; i < n; i++) {
            ScheduleInfo si = queue.get(i);
            chartData.getXdata().add(DateUtil.getFormatDate(si.getDate(), "HH:mm"));
            yData1.getData().add(si.getScheduleJobsCount());
            yData2.getData().add(si.getCurrentlyExecutingJobsCount());
            yData3.getData().add(si.getThreadPoolActiveCount());
            yData4.getData().add(si.getThreadPoolCompletedTaskCount());
        }
        chartData.getYdata().add(yData1);
        chartData.getYdata().add(yData2);
        chartData.getYdata().add(yData3);
        chartData.getYdata().add(yData4);
        return callback(chartData);
    }


}
