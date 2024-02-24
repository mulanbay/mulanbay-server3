package cn.mulanbay.pms.web.controller.fund;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.NullType;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.BudgetHandler;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.domain.BudgetLog;
import cn.mulanbay.pms.persistent.dto.fund.UserBudgetAndIncomeStat;
import cn.mulanbay.pms.persistent.enums.BudgetLogSource;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.service.BudgetService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.fund.budgetLog.*;
import cn.mulanbay.pms.web.bean.res.chart.ChartData;
import cn.mulanbay.pms.web.bean.res.chart.ChartYData;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static cn.mulanbay.pms.common.Constant.BUDGET_PERIOD_PX;
import static cn.mulanbay.pms.common.Constant.SCALE;

/**
 * 预算日志
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/budgetLog")
public class BudgetLogController extends BaseController {

    private static Class<BudgetLog> beanClass = BudgetLog.class;

    @Autowired
    BudgetService budgetService;

    @Autowired
    BudgetHandler budgetHandler;

    /**
     * 获取列表
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(BudgetLogSH sf) {
        String budgetKey = sf.getBudgetKey();
        if (StringUtil.isNotEmpty(budgetKey)) {
            if (budgetKey.startsWith(BUDGET_PERIOD_PX)) {
                //说明是分组过来的，即period的大节点
                int period = Integer.valueOf(sf.getBudgetKey().replace(BUDGET_PERIOD_PX, ""));
                PeriodType bp = PeriodType.getPeriodType(period);
                sf.setPeriod(bp);
            } else {
                Long budgetId = Long.valueOf(sf.getBudgetKey());
                sf.setBudgetId(budgetId);
            }
        }
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s1 = new Sort("createdTime", Sort.DESC);
        pr.addSort(s1);
        PageResult<BudgetLog> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建(单次)
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid BudgetLogForm form) {
        BudgetLog budgetLog = new BudgetLog();
        BeanCopy.copy(form, budgetLog, true);
        Budget budget = baseService.getObject(Budget.class, form.getBudgetId());
        budgetLog.setBudget(budget);
        //计算业务key
        String bussKey = budgetHandler.createBussKey(budget.getPeriod(), budgetLog.getBussDay());
        boolean isBussKeyExit = budgetService.isBudgetLogExit(bussKey, form.getUserId(), form.getLogId(), budget.getBudgetId());
        if (isBussKeyExit) {
            return callbackErrorCode(PmsCode.BUDGET_LOG_EXIT);
        }
        budgetLog.setStatPeriod(budget.getPeriod());
        budgetLog.setBussKey(bussKey);
        budgetLog.setBudgetAmount(budget.getAmount());
        budgetLog.setNcAmount(form.getAmount());
        budgetLog.setBcAmount(new BigDecimal(0));
        budgetLog.setTrAmount(new BigDecimal(0));
        budgetLog.setIncomeAmount(new BigDecimal(0));
        budgetLog.setSource(BudgetLogSource.MANUAL);
        budgetService.saveBudgetLog(budgetLog, false);
        return callback(null);
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "logId") Long logId) {
        BudgetLog budgetLog = baseService.getObject(beanClass, logId);
        return callback(budgetLog);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid BudgetLogForm form) {
        BudgetLog budgetLog = baseService.getObject(beanClass, form.getLogId());
        BeanCopy.copy(form, budgetLog, true);
        Budget budget = baseService.getObject(Budget.class, form.getBudgetId());
        budgetLog.setBudget(budget);
        //计算业务key
        String bussKey = budgetHandler.createBussKey(budget.getPeriod(), budgetLog.getBussDay());
        boolean isBussKeyExit = budgetService.isBudgetLogExit(bussKey, form.getUserId(), form.getLogId(), budget.getBudgetId());
        if (isBussKeyExit) {
            return callbackErrorCode(PmsCode.BUDGET_LOG_EXIT);
        }
        budgetLog.setStatPeriod(budget.getPeriod());
        budgetLog.setBudgetAmount(budget.getAmount());
        budgetLog.setNcAmount(form.getAmount());
        budgetLog.setBcAmount(new BigDecimal(0));
        budgetLog.setTrAmount(new BigDecimal(0));
        budgetLog.setIncomeAmount(new BigDecimal(0));
        budgetLog.setBussKey(bussKey);
        baseService.updateObject(budgetLog);
        return callback(null);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        String[] ids = deleteRequest.getIds().split(",");
        for(String s : ids){
            budgetService.deleteBudgetLog(Long.valueOf(s));
        }
        return callback(null);
    }

    /**
     * 统计(这里的收入是实时获取的，以前的接口中收入还没有统计到BudgetLog中)
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(@Valid BudgetLogStatSH sf) {
        if (sf.getBudgetKey().startsWith(BUDGET_PERIOD_PX)) {
            return callback(this.createPeriodStat(sf));
        } else {
            return callbackErrorInfo("请选择具体周期值");
        }
    }

    /**
     * 周期性统计
     * @param sf
     * @return
     */
    private ChartData createPeriodStat(BudgetLogStatSH sf){
        //说明是分组过来的，即period的大节点
        int period = Integer.valueOf(sf.getBudgetKey().replace(BUDGET_PERIOD_PX, ""));
        PeriodType bp = PeriodType.getPeriodType(period);
        List<UserBudgetAndIncomeStat> list = budgetService.statUserBudgetAndIncome(sf.getStartDate(), sf.getEndDate(),
                sf.getUserId(), bp);
        ChartData chartData = new ChartData();
        chartData.setTitle(bp.getName() + "预算统计");
        chartData.setLegendData(new String[]{"预算", "实际花费", "收入","花费/预算比率"});
        ChartYData budgetData = new ChartYData("预算","元");
        ChartYData consumeData = new ChartYData("实际花费","元");
        ChartYData incomeData = new ChartYData("收入","元");
        ChartYData rateData = new ChartYData("花费/预算比率","%");
        //混合图形下使用
        chartData.addYAxis("金额","元");
        chartData.addYAxis("比率","%");
        for (UserBudgetAndIncomeStat bean : list) {
            String xformat = DateUtil.FormatDay1;
            if (bp == PeriodType.YEARLY) {
                xformat = "yyyy";
            } else if (bp == PeriodType.MONTHLY) {
                xformat = "yyyy-MM";
            }
            chartData.getXdata().add(DateUtil.getFormatDate(bean.getBussDay(), xformat));
            budgetData.getData().add(bean.getBudgetAmount());
            BigDecimal consume = bean.getNcAmount().add(bean.getTrAmount());
            if (sf.getNeedOutBurst() != null && sf.getNeedOutBurst()) {
                consume = consume.add(bean.getBcAmount());
            }
            consumeData.getData().add(NumberUtil.getValue(consume,0));
            double rate = NumberUtil.getPercent(consume.doubleValue(),bean.getBudgetAmount().doubleValue(),0);
            rateData.getData().add(rate);
            incomeData.getData().add(bean.getTotalIncome() == null ? 0 : bean.getTotalIncome());
        }
        chartData.getYdata().add(budgetData);
        chartData.getYdata().add(consumeData);
        chartData.getYdata().add(incomeData);
        chartData.getYdata().add(rateData);
        return chartData;
    }

    /**
     * 实际的账户变化与系统计算的存款变化
     *
     * @return
     */
    @RequestMapping(value = "/biasStat", method = RequestMethod.GET)
    public ResultBean biasStat(@Valid BudgetLogBiasStatSH sf) {
        sf.setAccountChangeAmount(NullType.NOT_NULL);
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s1 = new Sort("bussKey", Sort.ASC);
        pr.addSort(s1);
        List<BudgetLog> list = baseService.getBeanList(pr);
        ChartData chartData = new ChartData();
        chartData.setTitle("账户变化与系统计算误差统计");
        chartData.setLegendData(new String[]{"误差值","误差率"});
        //混合图形下使用
        chartData.addYAxis("金额","元");
        chartData.addYAxis("比率","%");
        ChartYData yData1 = new ChartYData("误差值","元");
        ChartYData yData2 = new ChartYData("误差率","%");
        for (BudgetLog bean : list) {
            chartData.getIntXData().add(Integer.valueOf(bean.getBussKey()));
            chartData.getXdata().add(bean.getBussKey());
            //存款变化
            BigDecimal mv = bean.getIncomeAmount().subtract((bean.getBcAmount().add(bean.getNcAmount()).add(bean.getTrAmount())));
            //账户变化
            BigDecimal ev = bean.getAccountChangeAmount();
            BigDecimal e = ev.subtract(mv);
            yData1.getData().add(NumberUtil.getValue(e,SCALE));
            double pp = NumberUtil.getPercent(e.doubleValue(),Math.abs(mv.doubleValue()),0);
            yData2.getData().add(pp);
        }
        chartData.getYdata().add(yData1);
        chartData.getYdata().add(yData2);
        return callback(chartData);
    }

    /**
     * 统计（这里的收入是日终统计到BudgetLog中了，后期新增了字段）
     *
     * @return
     */
    @RequestMapping(value = "/periodStat", method = RequestMethod.GET)
    public ResultBean periodStat(@Valid BudgetLogPeriodStatSH sf) {
        BudgetLog bl = budgetService.selectBudgetLog(sf.getBussKey(), sf.getUserId());
        return callback(bl);
    }

    /**
     * 重新保存
     *
     * @return
     */
    @RequestMapping(value = "/reSave", method = RequestMethod.POST)
    public ResultBean reSave(@RequestBody @Valid BudgetLogReSaveForm form) {
        BudgetLog budgetLog = baseService.getObject(beanClass, form.getLogId());
        Date bussDay = budgetLog.getBussDay();
        List<Budget> list = budgetService.getActiveUserBudget(form.getUserId(), null);
        //默认是每月、每年的统计的日志
        boolean useLastDay = true;
        if (budgetLog.getBudget() != null) {
            //重新统计统计的是当时那一天为主的值
            useLastDay = false;
        }
        budgetHandler.statAndSaveBudgetLog(list, form.getUserId(), bussDay, budgetLog.getBussKey(), true, budgetLog.getStatPeriod(), useLastDay);
        return callback(null);
    }


}
