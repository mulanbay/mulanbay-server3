package cn.mulanbay.pms.web.controller.report;

import cn.mulanbay.ai.ml.processor.bean.PlanReportER;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.handler.ReportHandler;
import cn.mulanbay.pms.handler.UserScoreHandler;
import cn.mulanbay.pms.persistent.domain.PlanReport;
import cn.mulanbay.pms.persistent.domain.PlanReportTimeline;
import cn.mulanbay.pms.persistent.domain.UserPlan;
import cn.mulanbay.pms.persistent.dto.report.PlanReportAvgStat;
import cn.mulanbay.pms.persistent.dto.report.PlanReportResultGroupStat;
import cn.mulanbay.pms.persistent.dto.report.PlanValueDTO;
import cn.mulanbay.pms.persistent.enums.ManualStatType;
import cn.mulanbay.pms.persistent.enums.PlanStatResult;
import cn.mulanbay.pms.persistent.enums.PlanType;
import cn.mulanbay.pms.persistent.enums.PlanValueCompareType;
import cn.mulanbay.pms.persistent.service.PlanService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.pms.util.bean.PeriodDateBean;
import cn.mulanbay.pms.web.bean.req.report.plan.*;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 用户计划报告
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("planReport")
public class PlanReportController extends BaseController {

    @Autowired
    PlanService planService;

    @Autowired
    UserScoreHandler userScoreHandler;

    @Autowired
    ReportHandler reportHandler;

    private static Class<PlanReport> beanClass = PlanReport.class;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(PlanReportSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("bussDay", Sort.DESC);
        pr.addSort(s);
        PageResult<PlanReport> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }


    /**
     * 手动统计
     *
     * @return
     */
    @RequestMapping(value = "/manualStat", method = RequestMethod.POST)
    public ResultBean manualStat(@RequestBody @Valid PlanReportManualStatForm sf) {
        if(sf.getPlanId()==null&&sf.getPlanType()==null){
            return callbackErrorInfo("请选择计划类型或者具体计划");
        }
        UserPlanSH upsh = new UserPlanSH();
        upsh.setUserId(sf.getUserId());
        upsh.setPlanType(sf.getPlanType());
        upsh.setPlanId(sf.getPlanId());
        PageRequest pr = upsh.buildQuery();
        pr.setBeanClass(UserPlan.class);
        List<UserPlan> list = baseService.getBeanList(pr);
        Date bussDay = sf.getBussDay();
        for(UserPlan userPlan:list){
            String bussKey = BussUtil.getBussKey(userPlan.getPlanType().getPeriodType(),bussDay);
            PlanValueDTO upc = planService.getPlanValue(PlanValueCompareType.LATEST,bussKey,userPlan.getPlanId());
            planService.manualStat(userPlan,bussKey,sf.getStatType(),upc);
        }
        return callback(null);
    }

    /**
     * 清洗数据
     *
     * @return
     */
    @RequestMapping(value = "/cleanData", method = RequestMethod.POST)
    public ResultBean cleanData(@RequestBody @Valid PlanReportDataCleanForm sf) {
        planService.deletePlanReportData(sf);
        return callback(null);
    }

    /**
     * 重新统计
     *
     * @return
     */
    @RequestMapping(value = "/reStat", method = RequestMethod.POST)
    public ResultBean reStat(@RequestBody @Valid PlanReportReStatForm sf) {
        PlanReport planReport = baseService.getObject(beanClass,sf.getReportId());
        UserPlan userPlan = planReport.getPlan();
        String bussKey = planReport.getBussKey();
        PlanValueDTO upc = planService.getPlanValue(PlanValueCompareType.ORIGINAL,bussKey,userPlan.getPlanId());
        planService.manualStat(userPlan,bussKey, ManualStatType.RE_STAT,upc);
        return callback(null);
    }

    /**
     * 总统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(@Valid PlanReportResultGroupStatSH sf) {
        //先统计count类型
        List<PlanReportResultGroupStat> list = planService.getPlanReportResultGroupStat(sf);
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("计划报告结果分析");
        chartPieData.setUnit("次");
        ChartPieSerieData seriesData = new ChartPieSerieData();
        seriesData.setName("完成结果");
        for (PlanReportResultGroupStat bean : list) {
            String name = getStatResultTypeName(bean.getResultType());
            chartPieData.getXdata().add(name);
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(name);
            dataDetail.setValue(bean.getTotalCount());
            seriesData.getData().add(dataDetail);
        }
        chartPieData.getDetailData().add(seriesData);
        return callback(chartPieData);
    }

    private String getStatResultTypeName(Short resultType) {
        if (resultType == null) {
            return "未知";
        } else {
            PlanStatResult ps = PlanStatResult.getType(resultType.intValue());
            if (ps == null) {
                return "未知";
            } else {
                return ps.getName();
            }
        }
    }

    /**
     * 平均值统计
     *
     * @return
     */
    @RequestMapping(value = "/avgStat")
    public ResultBean avgStat(PlanReportAvgStatSH sf) {
        List<PlanReportAvgStat> list = planService.statPlanReportAvg(sf);
        if(StringUtil.isEmpty(list)){
            return null;
        }
        PlanReportAvgStat stat = list.get(0);
        UserPlan up = baseService.getObject(UserPlan.class, sf.getPlanId());
        stat.setPlan(up);
        return callback(stat);
    }

    /**
     * 按照日期统计
     *
     * @return
     */
    @RequestMapping(value = "/dateStat")
    public ResultBean dateStat(PlanReportDateStatSH sf) {
        PageRequest pageRequest = sf.buildQuery();
        pageRequest.setBeanClass(beanClass);
        Sort s = new Sort("bussDay", Sort.ASC);
        pageRequest.addSort(s);
        PageResult<PlanReport> pr = baseService.getBeanResult(pageRequest);
        UserPlan userPlan = baseService.getObject(UserPlan.class, sf.getPlanId());
        ChartData chartData = null;
        if (sf.isOriginal()) {
            chartData = createOriginalStatChartData(pr.getBeanList(), userPlan, sf);
        } else {
            chartData = createPercentStatChartData(pr.getBeanList(), userPlan, sf);
        }
        return callback(chartData);

    }


    /**
     * 原始数据类型
     *
     * @param list
     * @param userPlan
     * @return
     */
    private ChartData createOriginalStatChartData(List<PlanReport> list, UserPlan userPlan, PlanReportDateStatSH sf) {
        ChartData chartData = new ChartData();
        chartData.setTitle("["+userPlan.getTitle() + "]执行统计");
        chartData.setLegendData(new String[]{"统计值","次数"});
        String unit = userPlan.getUnit();
        //混合图形下使用
        chartData.addYAxis("统计值",unit);
        chartData.addYAxis("次数","次");
        ChartYData yData1 = new ChartYData("次数","次");
        ChartYData yData2 = new ChartYData("统计值",unit);

        for (PlanReport bean : list) {
            chartData.getXdata().add(bean.getBussKey());
            yData1.getData().add(bean.getReportCountValue());
            yData2.getData().add(bean.getReportValue());
        }
        chartData.getYdata().add(yData1);
        chartData.getYdata().add(yData2);
        return chartData;
    }

    /**
     * 百分比数据类型
     *
     * @param list
     * @param userPlan
     * @return
     */
    private ChartData createPercentStatChartData(List<PlanReport> list, UserPlan userPlan, PlanReportDateStatSH sf) {
        ChartData chartData = new ChartData();
        chartData.setTitle("["+userPlan.getTitle() + "]执行统计(百分比)");
        chartData.setLegendData(new String[]{"统计值","次数"});
        String unit = userPlan.getUnit();
        //混合图形下使用
        chartData.addYAxis("统计值","%");
        chartData.addYAxis("次数","%");
        ChartYData yData1 = new ChartYData("次数","%");
        ChartYData yData2 = new ChartYData("统计值","%");

        for (PlanReport bean : list) {
            chartData.getXdata().add(bean.getBussKey());
            yData1.getData().add(NumberUtil.getPercent(bean.getReportCountValue(), bean.getPlanCountValue(), 1));
            yData2.getData().add((NumberUtil.getPercent(bean.getReportValue(), bean.getPlanValue(), 1)));
        }
        chartData.getYdata().add(yData1);
        chartData.getYdata().add(yData2);
        return chartData;
    }

    /**
     * 时间线统计
     *
     * @return
     */
    @RequestMapping(value = "/timelineStat")
    public ResultBean timelineStat(@Valid PlanReportTimelineStatSH sf) {
        PlanType planType = sf.getPlanType();
        String bussKey = sf.getBussKey();
        PeriodDateBean pdb = BussUtil.calPeriod(bussKey,planType.getPeriodType());
        List<PlanReportTimeline> list = planService.getTimelineList(pdb.getStartDate(),pdb.getEndDate(),sf.getPlanId());
        ChartData chartData = new ChartData();
        if (list.isEmpty()) {
            return callback(chartData);
        }
        PlanReportTimeline pr0 = list.get(0);
        UserPlan userPlan = pr0.getPlan();
        chartData.setTitle(userPlan.getTitle() + "统计");
        chartData.setUnit("%");
        chartData.setSubTitle("计划次数:" + pr0.getPlanCountValue() + ",计划值:" + pr0.getPlanValue() + "(" + userPlan.getUnit() + ")");
        List<String> legends = new ArrayList<>();
        legends.add("计划次数完成进度");
        legends.add("计划值完成进度");
        legends.add("时间进度");
        ChartYData countData = new ChartYData("计划次数完成进度","%");
        ChartYData valueData = new ChartYData("计划值完成进度","%");
        ChartYData timeData = new ChartYData("时间进度","%");
        boolean predict = sf.getPredict();
        Map<String, Integer> scoreMap = null;
        ChartYData predictCountData = new ChartYData();
        ChartYData predictValueData = new ChartYData();
        if(predict){
            scoreMap = userScoreHandler.getUserScoreMap(sf.getUserId(),pdb.getStartDate(),pdb.getEndDate(),planType.getPeriodType());
            legends.add("次数预测值");
            predictCountData.setName("次数预测值");
            predictCountData.setUnit("%");
            legends.add("值预测值");
            predictValueData.setName("值预测值");
            predictValueData.setUnit("%");
        }
        chartData.setLegendData(legends.toArray(new String[legends.size()]));
        //缓存计划报告
        Map<String,PlanReportTimeline> prMap = new HashMap<>();
        for (PlanReportTimeline tl : list){
            int passDays=0;
            if(planType == PlanType.YEAR){
                passDays = DateUtil.getDayOfYear(tl.getBussDay());
            }else{
                passDays = DateUtil.getDayOfMonth(tl.getBussDay());
            }
            prMap.put(passDays+"",tl);
        }
        PlanReportTimeline lastPr = list.get(list.size()-1);
        //需要以完整的天数为准，因为BudgetTimeline有可能缺失，而且如果是当月的，后续数据也不全
        int totalDays = pdb.getTotalDays();
        Long userId = sf.getUserId();
        int month = DateUtil.getMonth(pdb.getBussDay());
        Date bussDay = BussUtil.getBussDay(planType.getPeriodType(),bussKey);
        for(int i=1;i<=totalDays;i++){
            String key = i+"";
            if (planType == PlanType.MONTH) {
                chartData.getXdata().add(i + "号");
            } else {
                Date date = DateUtil.getDate(i-1,bussDay);
                chartData.getXdata().add(DateUtil.getFormatDate(date, "MM-dd"));
            }
            chartData.getIntXData().add(i);
            PlanReportTimeline pr = prMap.get(key);
            double rate = NumberUtil.getPercent(i, totalDays, 0);
            if(pr==null){
                countData.getData().add(null);
                valueData.getData().add(null);
            }else{
                double planCountRate = NumberUtil.getPercent(pr.getReportCountValue(), pr.getPlanCountValue(), 2);
                double planValueRate = NumberUtil.getPercent(pr.getReportValue(), pr.getPlanValue(), 2);
                countData.getData().add(planCountRate);
                valueData.getData().add(planValueRate);
            }
            timeData.getData().add(rate);
            if(predict){
                Integer score = userScoreHandler.getScore(scoreMap,i);
                long templateId = pr.getPlan().getTemplate().getTemplateId();
                PlanReportER predictValue = null;
                if (planType == PlanType.MONTH) {
                    predictValue = reportHandler.predictMonthRate(userId,templateId,month,score,i);
                }else{
                    predictValue = reportHandler.predictYearRate(userId,templateId,score,i);
                }
                predictCountData.getData().add(NumberUtil.getValue(predictValue.getCountRate()*100,0));
                predictValueData.getData().add(NumberUtil.getValue(predictValue.getValueRate()*100,0));

            }
        }
        chartData.getYdata().add(countData);
        chartData.getYdata().add(valueData);
        chartData.getYdata().add(timeData);
        //预测
        if(predict){
            chartData.getYdata().add(predictCountData);
            chartData.getYdata().add(predictValueData);
        }
        return callback(chartData);
    }

    /**
     * 重新统计时间线
     *
     * @return
     */
    @RequestMapping(value = "/reStatTimeline", method = RequestMethod.POST)
    public ResultBean reStatTimeline(@RequestBody @Valid PlanReportReStatTimelineForm form) {
        UserPlan userPlan = baseService.getObject(UserPlan.class,form.getPlanId());
        String bussKey = form.getBussKey();
        PeriodDateBean pdb = BussUtil.calPeriod(bussKey,userPlan.getPlanType().getPeriodType(),false);
        Date firstDay = pdb.getStartDate();
        Date lastDay = pdb.getEndDate();
        //最多只能统计到昨天
        Date max = DateUtil.getDate(-1, new Date());
        if (lastDay.after(max)) {
            lastDay = max;
        }
        List<PlanReportTimeline> datas = new ArrayList<>();
        Date cc = firstDay;
        PlanValueDTO upc = planService.getPlanValue(PlanValueCompareType.ORIGINAL, bussKey,userPlan.getPlanId());
        while (!cc.after(lastDay)) {
            //统计指定日期
            PlanReport planReport = planService.statPlanReport(userPlan, bussKey,firstDay,cc, upc);
            if (planReport == null) {
                continue;
            }
            PlanReportTimeline timeline = new PlanReportTimeline();
            BeanCopy.copy(planReport, timeline);
            //需要设置为该天的
            timeline.setBussDay(cc);
            datas.add(timeline);
            timeline.setTimelineName(planReport.getReportName()+"["+DateUtil.getFormatDate(cc,DateUtil.FormatDay1)+"]");
            cc = DateUtil.getDate(1, cc);
        }
        if(datas.size()>0){
            planService.reSavePlanReportTimeline(datas,bussKey,form.getUserId(),userPlan.getPlanId());
            return callback(null);
        }else{
            return callbackErrorInfo("无相关统计数据");
        }
    }

}




