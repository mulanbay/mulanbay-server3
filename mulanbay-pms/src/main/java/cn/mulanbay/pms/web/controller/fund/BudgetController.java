package cn.mulanbay.pms.web.controller.fund;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.handler.BudgetHandler;
import cn.mulanbay.pms.handler.UserScoreHandler;
import cn.mulanbay.pms.handler.bean.fund.BudgetAmountBean;
import cn.mulanbay.pms.handler.bean.fund.FundStatBean;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.domain.BudgetLog;
import cn.mulanbay.pms.persistent.domain.BudgetTimeline;
import cn.mulanbay.pms.persistent.dto.consume.ConsumeBudgetStat;
import cn.mulanbay.pms.persistent.dto.consume.ConsumeRealTimeStat;
import cn.mulanbay.pms.persistent.dto.fund.BudgetStat;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.service.BudgetService;
import cn.mulanbay.pms.persistent.service.ConsumeService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.BussDayUtil;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.util.bean.PeriodDateBean;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.pms.web.bean.req.consume.consume.ConsumeAnalyseStatSH;
import cn.mulanbay.pms.web.bean.req.fund.budget.*;
import cn.mulanbay.pms.web.bean.req.fund.budgetTimeline.BudgetTimelineReStatForm;
import cn.mulanbay.pms.web.bean.req.fund.budgetTimeline.BudgetTimelineSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.bean.res.fund.budget.BudgetAnalyseVo;
import cn.mulanbay.pms.web.bean.res.fund.budget.BudgetDetailVo;
import cn.mulanbay.pms.web.bean.res.fund.budget.BudgetInfoVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

import static cn.mulanbay.persistent.query.PageRequest.NO_PAGE;
import static cn.mulanbay.pms.common.Constant.SCALE;

/**
 * 预算
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/budget")
public class BudgetController extends BaseController {

    private static Class<Budget> beanClass = Budget.class;

    @Autowired
    BudgetService budgetService;

    @Autowired
    BudgetHandler budgetHandler;

    @Autowired
    UserScoreHandler userScoreHandler;

    @Autowired
    ConsumeService consumeService;

    /**
     * 预算树
     * @param needRoot
     * @param filterEmpty 过滤空树
     * @return
     */
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public ResultBean tree(@RequestParam(name = "needRoot")Boolean needRoot, @RequestParam(name = "filterEmpty")Boolean filterEmpty) {
        try {
            BudgetSH sf = new BudgetSH();
            PageRequest pr = sf.buildQuery();
            pr.setPage(NO_PAGE);
            pr.setBeanClass(beanClass);
            Sort s1 = new Sort("period", Sort.ASC);
            pr.addSort(s1);
            List<Budget> gtList = baseService.getBeanList(pr);
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (PeriodType period : PeriodType.values()) {
                TreeBean tb = new TreeBean();
                tb.setId("p" + period.getValue());
                tb.setText(period.getName());
                for (Budget nc : gtList) {
                    if (nc.getPeriod() == period) {
                        TreeBean child = new TreeBean();
                        child.setId(nc.getBudgetId());
                        child.setText(nc.getBudgetName());
                        tb.addChild(child);
                    }
                }
                //去掉空列表
                if((filterEmpty==null||!filterEmpty)|| (filterEmpty&&tb.hasChildren())){
                    list.add(tb);
                }
            }
            return callback(TreeBeanUtil.addRoot(list, needRoot));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取预算列表树异常",
                    e);
        }
    }

    /**
     * 获取列表
     * @param sf
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(BudgetSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s1 = new Sort("status", Sort.DESC);
        pr.addSort(s1);
        Sort s2 = new Sort("createdTime", Sort.DESC);
        pr.addSort(s2);
        PageResult<Budget> qr = baseService.getBeanResult(pr);
        PageResult<BudgetDetailVo> res = new PageResult<>(qr);
        List<BudgetDetailVo> list = new ArrayList<>();
        Date now = new Date();
        for (Budget bg : qr.getBeanList()) {
            BudgetDetailVo bdb = this.getDetail(bg,now);
            list.add(bdb);
        }
        res.setBeanList(list);
        return callbackDataGrid(res);
    }

    /**
     * 获取详情
     * @param bg
     * @param now
     * @return
     */
    private BudgetDetailVo getDetail(Budget bg,Date now){
        BudgetDetailVo bdb = new BudgetDetailVo();
        BeanCopy.copy(bg, bdb);
        if (bg.getStatus() == CommonStatus.ENABLE) {
            //直接根据实际花费实时查询
            if (bg.getGoodsTypeId()!=null) {
                ConsumeBudgetStat bs= budgetHandler.getActualAmount(bg,now);
                if (bs.getTotalPrice() != null) {
                    bdb.setCpPaidTime(bs.getMaxConsumeDate());
                    bdb.setCpPaidAmount(bs.getTotalPrice());
                }else{
                    //计算下一次支付时间
                    Date nextPayTime = budgetHandler.getNextPayTime(bg, now);
                    bdb.setNextPayTime(nextPayTime);
                    Integer ld = DateUtil.getIntervalDays(now, nextPayTime);
                    bdb.setLeftDays(ld);
                }
            }
        }
        return bdb;
    }
    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid BudgetForm form) {
        Budget budget = new Budget();
        BeanCopy.copy(form, budget);
        budget.setCreatedTime(new Date());
        baseService.saveObject(budget);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "budgetId") Long budgetId) {
        Budget budget = baseService.getObject(beanClass,budgetId);
        return callback(budget);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid BudgetForm form) {
        //如果设置为无效，查看今年是否已经有记录
        if(form.getPeriod()==PeriodType.MONTHLY&&form.getStatus()==CommonStatus.DISABLE){
            int year = DateUtil.getYear(new Date());
            long n = budgetService.countMonthBudgetSnapshot(form.getBudgetId(),year);
            if(n>0){
                return this.callbackErrorInfo("该预算已经在本年度的"+n+"个月度快照中存在，无法直接修改为无效。请设置为预算金额为0或者第二年再设置为无效");
            }
        }
        Budget budget = baseService.getObject(beanClass,form.getBudgetId());
        BeanCopy.copyProperties(form, budget);
        budget.setModifyTime(new Date());
        baseService.updateObject(budget);
        return callback(null);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        baseService.deleteObjects(beanClass, NumberUtil.stringArrayToLongArray(deleteRequest.getIds().split(",")));
        return callback(null);
    }

    /**
     *
     * @param sf
     * @return
     */
    @RequestMapping(value = "/infoList", method = RequestMethod.GET)
    public ResultBean infoList(@Valid BudgetInfoListSH sf) {
        String bussKey = budgetHandler.createBussKey(sf.getPeriod(), new Date());
        List<Budget> list = budgetService.getActiveUserBudget(sf.getUserId(), null);
        BudgetAmountBean bab = budgetHandler.calcBudgetAmount(list, new Date());
        List<Budget> newList = null;
        if (sf.getPeriod() == PeriodType.MONTHLY) {
            newList = bab.getMonthBudgetList();
        } else if (sf.getPeriod() == PeriodType.YEARLY) {
            newList = bab.getYearBudgetList();
        } else {
            return callbackErrorInfo("不支持的周期查询类型:" + sf.getPeriod());
        }
        List<BudgetDetailVo> res = new ArrayList<>();
        Date now = new Date();
        for (Budget bg : newList) {
            BudgetDetailVo bdb = this.getDetail(bg,now);
            res.add(bdb);
        }
        return callback(res);
    }

    /**
     * 预算分析
     *
     * @return
     */
    @RequestMapping(value = "/analyse", method = RequestMethod.GET)
    public ResultBean analyse(@Valid BudgetAnalyseSH sf) {
        Date[] ds = budgetHandler.getDateRange(sf.getPeriod(), sf.getDate(), true);
        BudgetAnalyseVo res = new BudgetAnalyseVo();
        FundStatBean fundStat = budgetHandler.statConsumeIncome(ds[0],ds[1],sf.getUserId());
        List<Budget> budgetList = budgetService.getActiveUserBudget(sf.getUserId(), null);
        BudgetAmountBean bab = budgetHandler.calcBudgetAmount(budgetList, sf.getDate());
        if (sf.getPeriod() == PeriodType.MONTHLY) {
            res.setTitle(DateUtil.getFormatDate(sf.getDate(), "yyyy-MM") + "月度预算分析");
            fundStat.setBudget(bab.getMonthBudget());
        } else {
            res.setTitle(DateUtil.getFormatDate(sf.getDate(), "yyyy") + "年度预算分析");
            fundStat.setBudget(bab.getYearBudget());
        }
        List<BudgetInfoVo> voList = new ArrayList<>();
        for(Budget budget : budgetList){

        }
        res.setBudgetList(budgetList);
        res.setFundStat(fundStat);
        return callback(res);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(BudgetStatSH as) {
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("年预算费用分析");
        chartPieData.setUnit("元");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("预算(年费用)");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        List<BudgetStat> list = budgetService.statBudget(as.getUserId(), as.getType(), as.getPeriod(), as.getStatType());
        for (BudgetStat bean : list) {
            chartPieData.getXdata().add(bean.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(bean.getName());
            dataDetail.setValue(bean.getValue());
            serieData.getData().add(dataDetail);
            totalValue = totalValue.add(bean.getValue());
        }
        String subTitle = "年预算总计：" + String.valueOf(totalValue.longValue()) + "元";
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(serieData);
        return callback(chartPieData);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/tagsStat", method = RequestMethod.GET)
    public ResultBean tagsStat(BudgetTagsStatSH as) {
        ConsumeAnalyseStatSH basf = new ConsumeAnalyseStatSH();
        basf.setUserId(as.getUserId());
        basf.setGroupField(as.getGroupField());
        basf.setStartDate(as.getStartDate());
        basf.setEndDate(as.getEndDate());
        Budget budget = baseService.getObject(beanClass,as.getBudgetId());
        basf.setTags(budget.getTags());
        basf.setType(GroupType.TOTALPRICE);
        List<ConsumeRealTimeStat> list = consumeService.getAnalyseStat(basf);
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("[" + budget.getBudgetName() + "]预算的消费分析");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("费用");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        for (ConsumeRealTimeStat bean : list) {
            chartPieData.getXdata().add(bean.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(bean.getName());
            dataDetail.setValue(bean.getValue());
            serieData.getData().add(dataDetail);
            totalValue = totalValue.add(bean.getValue());
        }
        String subTitle = "预算金额：" + NumberUtil.getValue(budget.getAmount(),SCALE) + "元,实际花费:" + NumberUtil.getValue( totalValue,SCALE) + "元";
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(serieData);
        return callback(chartPieData);
    }

    /**
     * 时间线统计
     *
     * @return
     */
    @RequestMapping(value = "/timelineStat")
    public ResultBean timelineStat(@Valid BudgetTimelineSH sf) {
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
    @RequestMapping(value = "/reStatTimeline", method = RequestMethod.POST)
    public ResultBean reStatTimeline(@RequestBody @Valid BudgetTimelineReStatForm re) {
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
