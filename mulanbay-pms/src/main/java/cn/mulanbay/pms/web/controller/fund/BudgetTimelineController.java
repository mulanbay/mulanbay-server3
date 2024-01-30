package cn.mulanbay.pms.web.controller.fund;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.pms.handler.BudgetHandler;
import cn.mulanbay.pms.handler.UserScoreHandler;
import cn.mulanbay.pms.handler.bean.fund.BudgetAmountBean;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.domain.BudgetLog;
import cn.mulanbay.pms.persistent.domain.BudgetTimeline;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.service.BudgetService;
import cn.mulanbay.pms.persistent.service.ConsumeService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.BussDayUtil;
import cn.mulanbay.pms.util.bean.PeriodDateBean;
import cn.mulanbay.pms.web.bean.req.fund.budgetTimeline.BudgetTimelineReStatForm;
import cn.mulanbay.pms.web.bean.req.fund.budgetTimeline.BudgetTimelineSH;
import cn.mulanbay.pms.web.bean.res.chart.ChartData;
import cn.mulanbay.pms.web.bean.res.chart.ChartYData;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

import static cn.mulanbay.pms.common.Constant.SCALE;

/**
 * 预算
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/budgetTimeline")
public class BudgetTimelineController extends BaseController {

    private static Class<BudgetTimeline> beanClass = BudgetTimeline.class;

    @Autowired
    BudgetService budgetService;

    @Autowired
    BudgetHandler budgetHandler;

    @Autowired
    UserScoreHandler userScoreHandler;

    @Autowired
    ConsumeService consumeService;

    /**
     * 时间线统计
     *
     * @return
     */
    @RequestMapping(value = "/stat")
    public ResultBean stat(@Valid BudgetTimelineSH sf) {
        Date bussDay = sf.getBussDay();
        Long userId = sf.getUserId();
        Boolean needOutBurst = sf.getNeedOutBurst();
        String bussKey = budgetHandler.createBussKey(sf.getPeriod(), bussDay);
        List<BudgetTimeline> list = budgetService.selectBudgetTimelineList(bussKey, userId);
        PeriodDateBean pdb = BussDayUtil.calPeriod(bussDay,sf.getPeriod());
        int month = DateUtil.getMonth(bussDay);
        ChartData chartData = new ChartData();
        chartData.setTitle("[" + DateUtil.getFormatDate(bussDay, pdb.getDateFormat()) + "]预算与消费统计");
        List<String> legends = new ArrayList<>();
        if (sf.getStatType() == BudgetTimelineSH.StatType.RATE) {
            legends.add("消费/预算比例");
            legends.add("时间进度");
            chartData.setUnit("%");
        } else {
            legends.add("消费金额");
            legends.add("预算金额");
            chartData.setUnit("元");
        }
        boolean predict = sf.getPredict();
        Map<String, Integer> scoreMap = null;
        ChartYData predictData = new ChartYData();
        if(predict){
            legends.add("预测值");
            scoreMap = userScoreHandler.getUserScoreMap(userId,pdb.getStartDate(),pdb.getEndDate(),sf.getPeriod());
            predictData.setName(legends.get(2));
        }
        chartData.setLegendData(legends.toArray(new String[legends.size()]));
        ChartYData cbData = new ChartYData(legends.get(0),chartData.getUnit());
        ChartYData timeData = new ChartYData(legends.get(1),chartData.getUnit());
        if (list.isEmpty()) {
            return callback(chartData);
        }
        //缓存预算
        Map<String,BudgetTimeline> budgetMap = new HashMap<>();
        for (BudgetTimeline tl : list){
            budgetMap.put(tl.getPassDays().toString(),tl);
        }
        BudgetTimeline lastBudget = list.get(list.size()-1);
        //需要以完整的天数为准，因为BudgetTimeline有可能缺失，而且如果是当月的，后续数据也不全
        int totalDays = pdb.getTotalDays();
        for(int i=1;i<=totalDays;i++){
            String key = i+"";
            if (sf.getPeriod() == PeriodType.MONTHLY) {
                chartData.getXdata().add(i + "号");
            } else {
                Date date = DateUtil.getDate(i-1,bussDay);
                chartData.getXdata().add(DateUtil.getFormatDate(date, "MM-dd"));
            }
            chartData.getIntXData().add(i);
            BudgetTimeline timeline = budgetMap.get(key);
            if(timeline==null){
                cbData.getData().add(null);
                timeData.getData().add(null);
            }else{
                BigDecimal consumeAmount = timeline.getNcAmount().add(timeline.getBcAmount());
                if(needOutBurst){
                    consumeAmount = consumeAmount.add(timeline.getBcAmount()) ;
                }
                if (sf.getStatType() == BudgetTimelineSH.StatType.RATE) {
                    BigDecimal cbRate = NumberUtil.getPercentValue(consumeAmount, timeline.getBudgetAmount(), 0);
                    double dayRate = NumberUtil.getPercentValue(timeline.getPassDays(), timeline.getTotalDays(), 0);
                    cbData.getData().add(cbRate);
                    timeData.getData().add(dayRate);
                } else {
                    cbData.getData().add(NumberUtil.getValue(consumeAmount,SCALE));
                    timeData.getData().add(NumberUtil.getValue(timeline.getBudgetAmount(),SCALE));
                }
            }
            if(predict){
                Double pv = null;
                Integer score = userScoreHandler.getScore(scoreMap,i);
                if (sf.getPeriod() == PeriodType.MONTHLY) {
                    pv = budgetHandler.predictMonthRate(userId,month,score,i,needOutBurst);
                }else{
                    pv = budgetHandler.predictYearRate(userId,score,i,needOutBurst);
                }
                if (sf.getStatType() == BudgetTimelineSH.StatType.RATE){
                    predictData.getData().add(NumberUtil.getValue(pv*100,0));
                }else{
                    BudgetTimeline tt = budgetMap.get(key);
                    if(tt==null){
                        tt = lastBudget;
                    }
                    predictData.getData().add(NumberUtil.getValue(tt.getBudgetAmount().doubleValue()*pv,SCALE));
                }

            }
        }

        BudgetTimeline end = list.get(list.size() - 1);
        BigDecimal tt = end.getNcAmount().add(end.getBcAmount()).add(end.getTrAmount()) ;
        String subTitle = "当前消费总金额：" + NumberUtil.getValue(tt,SCALE) + "元，预算：" + NumberUtil.getValue( end.getBudgetAmount(),SCALE) + "元。";
        if (tt.compareTo(end.getBudgetAmount()) ==1 ) {
            subTitle += "(已经超出预算)";
        }
        chartData.setSubTitle(subTitle);
        chartData.getYdata().add(cbData);
        chartData.getYdata().add(timeData);
        //预测
        if(predict){
            chartData.getYdata().add(predictData);
        }
        return callback(chartData);
    }

    /**
     * 重新统计时间线
     *
     * @return
     */
    @RequestMapping(value = "/reStat", method = RequestMethod.POST)
    public ResultBean reStat(@RequestBody @Valid BudgetTimelineReStatForm re) {
        Date firstDay;
        Date lastDay;
        if (re.getPeriod() == PeriodType.MONTHLY) {
            firstDay = DateUtil.getMonthFirst(re.getBussDay());
            lastDay = DateUtil.getMonthLast(re.getBussDay());
        } else {
            firstDay = DateUtil.getYearFirst(re.getBussDay());
            lastDay = DateUtil.getYearLast(re.getBussDay());
        }
        //最多只能统计到昨天
        Date max = DateUtil.getDate(-1, new Date());
        if (lastDay.after(max)) {
            lastDay = max;
        }
        List<BudgetTimeline> datas = new ArrayList<>();
        String bussKey = budgetHandler.createBussKey(re.getPeriod(), re.getBussDay());
        Date cc = firstDay;
        //总天数
        int tds;
        List<Budget> list = budgetService.getActiveUserBudget(re.getUserId(), null);
        BudgetAmountBean bab = budgetHandler.calcBudgetAmount(list, firstDay);
        BigDecimal budgetAmount;
        if (re.getPeriod() == PeriodType.MONTHLY) {
            //当月总天数
            tds = DateUtil.getMonthDays(firstDay);
            budgetAmount = bab.getMonthBudget();
        } else {
            //当年总天数
            tds = DateUtil.getDays(Integer.valueOf(DateUtil.getYear(firstDay)));
            budgetAmount = bab.getYearBudget();
        }

        while (!cc.after(lastDay)) {
            //已经过去天数
            int pds;
            if (re.getPeriod() == PeriodType.MONTHLY) {
                //当月已经过去几天
                pds = DateUtil.getDayOfMonth(cc);
            } else {
                //当年已经过去几天
                pds = DateUtil.getDayOfYear(cc);
            }
            //增加时间线流水
            BudgetTimeline timeline = new BudgetTimeline();
            BudgetLog bl = budgetHandler.statBudget(re.getUserId(), budgetAmount, firstDay, cc, bussKey, false, re.getPeriod());
            BeanCopy.copyProperties(bl, timeline);
            timeline.setCreatedTime(new Date());
            timeline.setModifyTime(null);
            timeline.setBussDay(cc);
            timeline.setTotalDays(tds);
            timeline.setPassDays(pds);
            timeline.setBussKey(bussKey);
            timeline.setTimelineId(null);
            timeline.setRemark("手动重新统计");
            datas.add(timeline);
            cc = DateUtil.getDate(1, cc);
        }
        budgetService.reSaveBudgetTimeline(datas, bussKey, re.getUserId());
        return callback(null);
    }
}
