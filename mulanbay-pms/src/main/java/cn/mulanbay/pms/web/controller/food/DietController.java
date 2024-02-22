package cn.mulanbay.pms.web.controller.food;

import cn.mulanbay.ai.nlp.processor.NLPProcessor;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.DietHandler;
import cn.mulanbay.pms.persistent.domain.Diet;
import cn.mulanbay.pms.persistent.domain.DietVarietyLog;
import cn.mulanbay.pms.persistent.domain.FoodCategory;
import cn.mulanbay.pms.persistent.dto.food.*;
import cn.mulanbay.pms.persistent.dto.life.NameCountDTO;
import cn.mulanbay.pms.persistent.enums.*;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.persistent.service.DietService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.util.FundUtil;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.util.bean.PeriodDateBean;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.food.category.FoodCategorySH;
import cn.mulanbay.pms.web.bean.req.food.diet.*;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

import static cn.mulanbay.pms.common.Constant.ROUNDING_MODE;

/**
 * 饮食
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/diet")
public class DietController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DietController.class);

    private static Class<Diet> beanClass = Diet.class;

    @Value("${mulanbay.food.cate.days:14}")
    int cateDays;

    @Autowired
    DietService dietService;

    @Autowired
    AuthService authService;

    @Autowired
    DietHandler dietHandler;

    @Autowired
    NLPProcessor nlpProcessor;

    /**
     * 食物列表
     *
     * @return
     */
    @RequestMapping(value = "/cateTree")
    public ResultBean cateTree(DietCateSH sf) {
        if(sf.getStartDate()==null&&sf.getEndDate()==null){
            Date end = new Date();
            Date start = DateUtil.getDate(-cateDays);
            sf.setStartDate(start);
            sf.setEndDate(end);
        }
        List<String> list = dietService.getDietCateList(sf);
        //去重
        Set<String> foodsSet = TreeBeanUtil.deleteDuplicate(list);
        return callback(TreeBeanUtil.creatTreeList(foodsSet, sf.getNeedRoot()));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(DietSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("occurTime", Sort.DESC);
        pr.addSort(s);
        PageResult<Diet> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid DietForm form) {
        Diet bean = new Diet();
        BeanCopy.copy(form, bean);
        baseService.saveObject(bean);
        return callback(bean);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "dietId") Long dietId) {
        Diet bean = baseService.getObject(beanClass,dietId);
        return callback(bean);
    }

    /**
     * 获取最近一次的饮食
     *
     * @return
     */
    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    public ResultBean latest(@Valid LastDietSH sh) {
        Diet diet = dietService.getLastDiet(sh);
        return callback(diet);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid DietForm form) {
        Diet bean = baseService.getObject(beanClass,form.getDietId());
        BeanCopy.copy(form, bean);
        baseService.updateObject(bean);
        return callback(bean);
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
     * 用户行为统计（带饼图分析）
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(@Valid DietStatSH sf) {
        if (sf.getDateGroupType() == DateGroupType.MONTH) {
            return callback(this.createMonthStat(sf));
        } else {
            return callback(this.createYearStat(sf));
        }
    }

    /**
     * 月份统计
     * @param sf
     * @return
     */
    private ChartCalendarPieData createMonthStat(DietStatSH sf) {
        Date date = DateUtil.getDate(sf.getYear() + "-" + sf.getMonth() + "-01", DateUtil.FormatDay1);
        Date startTime = DateUtil.getMonthFirst(date);
        Date endDate = DateUtil.getMonthLast(date);
        Date endTime = DateUtil.tillMiddleNight(endDate);
        ChartCalendarPieData pieData = new ChartCalendarPieData(UserBehaviorType.LIFE);
        String monthString = DateUtil.getFormatDate(startTime, "yyyyMM");
        pieData.setTitle(monthString + "饮食习惯分析");
        pieData.setUnit("次");
        pieData.setStartDate(startTime);
        //获取列表
        DietSH ds = new DietSH();
        BeanCopy.copy(sf,ds);
        ds.setStartDate(startTime);
        ds.setEndDate(endTime);
        PageRequest prds = ds.buildQuery();
        prds.setBeanClass(beanClass);
        prds.setPage(PageRequest.NO_PAGE);
        List<Diet> list = baseService.getBeanList(prds);
        for (Diet stat : list) {
            String dd = DateUtil.getFormatDate(stat.getOccurTime(), DateUtil.FormatDay1);
            pieData.addData(dd, stat.getDietType().getName(), 1, false, 1, true);
        }
        return pieData;
    }

    /**
     * 年份统计
     * @param sf
     * @return
     */
    private ChartCalendarCompareData createYearStat(DietStatSH sf) {
        if (sf.getDietType() == null) {
            throw new ApplicationException(PmsCode.DIET_TYPE_NULL);
        }
        Date startTime = DateUtil.getDate(sf.getYear() + "-01-01 00:00:00", DateUtil.Format24Datetime);
        Date endTime = DateUtil.getDate(sf.getYear() + "-12-31 23:59:59", DateUtil.Format24Datetime);

        ChartCalendarCompareData chartData = new ChartCalendarCompareData();
        chartData.setTitle(sf.getYear() + "饮食习惯分析");
        chartData.setUnit("次");
        chartData.setYear(sf.getYear());
        //获取列表
        DietSH ds = new DietSH();
        BeanCopy.copy(sf,ds);
        ds.setStartDate(startTime);
        ds.setEndDate(endTime);
        PageRequest prds = ds.buildQuery();
        prds.setBeanClass(beanClass);
        prds.setPage(PageRequest.NO_PAGE);
        List<Diet> list = baseService.getBeanList(prds);
        Date beginFlag = list.get(0).getOccurTime();
        for (Diet stat : list) {
            String dd = DateUtil.getFormatDate(stat.getOccurTime(), DateUtil.FormatDay1);
            chartData.addSerieData(dd, 1, false, 1, 1);
            addMissDate(beginFlag, stat.getOccurTime(), chartData);
            beginFlag = stat.getOccurTime();
        }
        String s1 = sf.getDietType().getName();
        String s2 = "没吃" + sf.getDietType().getName();
        chartData.setLegendData(new String[]{s1, s2});

        return chartData;
    }

    /**
     * 统计没吃某餐的数据
     *
     * @param start
     * @param end
     * @param chartData
     */
    private void addMissDate(Date start, Date end, ChartCalendarCompareData chartData) {
        Date startDate = DateUtil.getDate(start, DateUtil.FormatDay1);
        Date endDate = DateUtil.getDate(end, DateUtil.FormatDay1);
        int n = DateUtil.getIntervalDays(startDate, endDate);
        if (n <= 1) {
            return;
        } else {
            for (int i = 2; i <= n; i++) {
                String dd = DateUtil.getFormatDate(DateUtil.getDate((i - 1), startDate), DateUtil.FormatDay1);
                chartData.addSerieData(dd, 1, false, 1, 2);
            }
        }
    }

    /**
     * 根据时间点的统计
     *
     * @return
     */
    @RequestMapping(value = "/timeStat", method = RequestMethod.GET)
    public ResultBean timeStat(@Valid DietTimeStatSH sf) {
        ScatterChartData chartData = new ScatterChartData();
        chartData.setTitle("饮食时间点分析");
        chartData.setxUnit(sf.getGroupType().getName());
        chartData.setyUnit("点");
        List<DietTimeStat> list = dietService.timeStatDiet(sf);
        chartData.addLegent("时间点");
        ScatterChartDetailData detailData = new ScatterChartDetailData();
        detailData.setName("时间点");
        double totalX = 0;
        int n = 0;
        for (DietTimeStat stat : list) {
            detailData.addData(new Object[]{stat.getTimeGroupValue(), stat.getDoubleValue()});
            totalX += stat.getTimeGroupValue();
            n++;
        }
        detailData.setxAxisAverage(totalX / n);
        chartData.addSeriesData(detailData);
        return callback(chartData);
    }

    /**
     * 分析
     *
     * @return
     */
    @RequestMapping(value = "/analyse", method = RequestMethod.GET)
    public ResultBean analyse(@Valid DietAnalyseSH sf) {
        if (sf.getChartType() == ChartType.TREE_MAP) {
            //只有按照商品子类型的才能
            if (sf.getField() != DietStatField.CLASS_NAME) {
                return callbackErrorInfo("只有按照小类分组的才支持该分析图型");
            }
            return callback(this.createAnalyseStatTreeMapData(sf));
        }
        if (sf.getChartType() == ChartType.PIE) {
            return callback(this.createAnalyseStatPieData(sf));
        } else {
            return callback(this.createAnalyseStatBarData(sf));
        }
    }

    /**
     * 价格分析
     *
     * @return
     */
    @RequestMapping(value = "/priceAnalyse", method = RequestMethod.GET)
    public ResultBean priceAnalyse(@Valid DietPriceAnalyseSH sf) {
        DietStatField statField = sf.getStatField();
        if (statField==DietStatField.DIET_SOURCE || statField==DietStatField.FOOD_TYPE || statField==DietStatField.DIET_TYPE) {
            return callback(createAnalyseTypeStatPieData(sf));
        }
        List<DietPriceAnalyseStat> list = dietService.getDietPriceAnalyseStat(sf);
        if (sf.getDateGroupType() == DateGroupType.DAYCALENDAR) {
            return callback(createAnalyseStatPieData(list, sf));
        }else {
            return callback(createAnalyseStatLineData(list, sf));
        }
    }

    /**
     * 饮食价格分析折线图
     *
     * @param list
     * @param sf
     * @return
     */
    private ChartData createAnalyseStatLineData(List<DietPriceAnalyseStat> list, DietPriceAnalyseSH sf){
        ChartData chartData = new ChartData();
        chartData.setTitle("饮食价格统计");
        chartData.setSubTitle(this.getDateTitle(sf));
        chartData.setLegendData(new String[]{"消费","次数","预测"});
        //混合图形下使用
        chartData.addYAxis("消费","元");
        chartData.addYAxis("次数","次");
        ChartYData yData1 = new ChartYData("次数","次");
        ChartYData yData2 = new ChartYData("消费","元");
        ChartYData yData3 = new ChartYData("预测","元");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        //总的值
        BigDecimal totalCount = new BigDecimal(0);
        boolean predict = sf.getPredict();
        for (DietPriceAnalyseStat bean : list) {
            chartData.addXData(bean, sf.getDateGroupType());
            yData1.getData().add(bean.getTotalCount());
            yData2.getData().add(bean.getTotalPrice());
            if(predict){
                double predictPrice = this.predictPrice(sf.getUserId(),bean.getDateIndexValue(),sf.getDateGroupType(),sf.getStartDate());
                yData3.getData().add(NumberUtil.getValue(predictPrice,2));
            }
            totalCount = totalCount.add(new BigDecimal(bean.getTotalCount()));
            totalValue = totalValue.add(bean.getTotalPrice());
        }
        chartData.getYdata().add(yData2);
        chartData.getYdata().add(yData3);
        chartData.getYdata().add(yData1);
        String subTitle = this.getDateTitle(sf, totalCount.longValue() + "次，" + totalValue.doubleValue() + "元");
        chartData.setSubTitle(subTitle);
        chartData = ChartUtil.completeDate(chartData, sf);
        return chartData;
    }

    /**
     * 预测价格
     * @param userId
     * @param indexValue
     * @param dateGroupType
     * @param startDate
     * @return
     */
    private double predictPrice(Long userId,int indexValue,DateGroupType dateGroupType,Date startDate){
        PeriodType period = null;
        Date bussDay = null;
        if(dateGroupType==DateGroupType.YEAR){
            period = PeriodType.YEARLY;
            bussDay = DateUtil.getDate(indexValue + "-01" + "-01", DateUtil.FormatDay1);
        }else{
            period = PeriodType.MONTHLY;
            //只能在一年内
            int year = DateUtil.getYear(startDate);
            bussDay = DateUtil.getDate(year + "-" + indexValue + "-01", DateUtil.FormatDay1);
        }
        PeriodDateBean pdb = FundUtil.calPeriod(bussDay,period);
        double price = dietHandler.predictPrice(userId,indexValue,pdb.getStartDate(),pdb.getEndDate(),period);
        return price;
    }

    /**
     * 饮食价格分析饼图：以价格区间分析
     *
     * @param list
     * @param sf
     * @return
     */
    private ChartPieData createAnalyseStatPieData(List<DietPriceAnalyseStat> list, DietPriceAnalyseSH sf) {
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setUnit("元");
        chartPieData.setTitle("饮食价格区间分析");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("价格区间");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        Map<String, Integer> map = new HashMap<>();
        for (DietPriceAnalyseStat bean : list) {
            int p = bean.getTotalPrice().intValue();
            int x = p / 5;
            String key = x * 5 + "-" + (x + 1) * 5 + "元";
            Integer n = map.get(key);
            if (n == null) {
                map.put(key, 1);
            } else {
                map.put(key, n + 1);
            }
            totalValue = totalValue.add(bean.getTotalPrice());
        }
        for (String key : map.keySet()) {
            chartPieData.getXdata().add(key);
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(key);
            dataDetail.setValue(map.get(key));
            serieData.getData().add(dataDetail);
        }
        String subTitle = this.getDateTitle(sf, totalValue.doubleValue() + "元");
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(serieData);
        return chartPieData;
    }

    /**
     * 饮食价格分析饼图：以饮食的分类来统计
     *
     * @param sf
     * @return
     */
    private ChartPieData createAnalyseTypeStatPieData(DietPriceAnalyseSH sf) {
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("饮食分析");
        chartPieData.setUnit("元");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("价格");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        List<DietPriceAnalyseTypeStat> list = dietService.getDietPriceAnalyseTypeStat(sf);
        for (DietPriceAnalyseTypeStat bean : list) {
            chartPieData.getXdata().add(bean.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(bean.getName());
            dataDetail.setValue(NumberUtil.getValue(bean.getTotalPrice(),2));
            serieData.getData().add(dataDetail);
            totalValue = totalValue.add(bean.getTotalPrice());
        }
        String subTitle = this.getDateTitle(sf, totalValue.doubleValue() + "元");
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(serieData);
        return chartPieData;
    }

    /**
     * 获取有效的食品分类列表
     *
     * @return
     */
    private List<FoodCategory> getActiveFoodCategoryList() {
        FoodCategorySH sf = new FoodCategorySH();
        sf.setStatus(CommonStatus.ENABLE);
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(FoodCategory.class);
        Sort sort = new Sort("orderIndex", Sort.ASC);
        pr.addSort(sort);
        List<FoodCategory> list = baseService.getBeanList(pr);
        return list;
    }

    private FoodCategory findDietCategory(List<FoodCategory> list, String s) {
        for (FoodCategory dc : list) {
            String[] ks = dc.getTags().split(",");
            for (String k : ks) {
                if (s.contains(k)) {
                    return dc;
                }
            }
        }
        logger.warn("饮食[" + s + "]找不到食品类型分析");
        return null;
    }

    /**
     * 比对
     *
     * @return
     */
    @RequestMapping(value = "/compare", method = RequestMethod.GET)
    public ResultBean compare(@Valid DietCompareSH sf) {
        List<DietCompareStat> list = dietService.getDietCompareStat(sf);
        ChartData chartData = new ChartData();
        chartData.setTitle("饮食比对");
        chartData.setUnit(sf.getStatField().getUnit());
        List dietTypeVs = new ArrayList<>();
        for (DietType dt : DietType.values()) {
            dietTypeVs.add(0);
            chartData.getXdata().add(dt.getName());
        }
        List<String> dietSourceNames = new ArrayList<>();
        for (DietSource ds : DietSource.values()) {
            dietSourceNames.add(ds.getName());
            ChartYData yData = new ChartYData();
            yData.setName(ds.getName());
            List values = new ArrayList();
            values.addAll(dietTypeVs);
            yData.setData(values);
            chartData.getYdata().add(yData);
        }
        String[] strings = new String[dietSourceNames.size()];
        chartData.setLegendData(dietSourceNames.toArray(strings));
        for (DietCompareStat m : list) {
            ChartYData yData = chartData.findYData(m.getDietSource().getName());
            List values = yData.getData();
            Object v = 0;
            if (sf.getStatField() == DietCompareSH.StatField.COUNTS) {
                v = m.getTotalCount().longValue();
            } else if (sf.getStatField() == DietCompareSH.StatField.TOTAL_PRICE) {
                v = NumberUtil.getValue(m.getTotalPrice(),0);
            } else if (sf.getStatField() == DietCompareSH.StatField.AVG_PRICE) {
                v = NumberUtil.getValue(m.getTotalPrice().divide(new BigDecimal(m.getTotalCount()), ROUNDING_MODE),0);
            } else if (sf.getStatField() == DietCompareSH.StatField.AVG_SCORE) {
                v = NumberUtil.getValue((new BigDecimal(m.getTotalScore())).divide(new BigDecimal(m.getTotalCount()), ROUNDING_MODE),0);
            }
            values.set(m.getDietType().getValue(), v);
        }
        String subTitle = this.getDateTitle(sf);
        chartData.setSubTitle(subTitle);
        return callback(chartData);
    }

    /**
     * 封装消费记录分析的树形图数据
     *
     * @param sf
     * @return
     */
    private ChartTreeMapData createAnalyseStatTreeMapData(DietAnalyseSH sf) {
        ChartTreeMapData chartData = new ChartTreeMapData();
        chartData.setTitle("饮食分析");
        chartData.setName("食物");
        chartData.setUnit("次");
        Map<String, ChartTreeMapDetailData> dataMap = new HashMap<>();
        List<DietAnalyseStat> list = dietService.getDietAnalyseStat(sf);
        List<FoodCategory> dcList = this.getActiveFoodCategoryList();
        for (DietAnalyseStat bean : list) {
            FoodCategory dc = this.findDietCategory(dcList, bean.getName());
            if (dc == null) {
                if (!sf.isIncludeUnknown()) {
                    continue;
                }
                dc = new FoodCategory();
                dc.setClassName(bean.getName());
                dc.setType("未知");
            }
            ChartTreeMapDetailData mdd = dataMap.get(dc.getType());
            //只有两层结构
            if (mdd == null) {
                mdd = new ChartTreeMapDetailData(1, dc.getType(), dc.getType());
                dataMap.put(dc.getType(), mdd);
            }
            mdd.findAndAppendChild(bean.getTotalCount().doubleValue(), dc.getClassName(), dc.getType() + "/" + dc.getClassName());
        }
        chartData.setData(new ArrayList<>(dataMap.values()));
        return chartData;

    }

    /**
     * 封装消费分析的饼状图数据
     *
     * @param sf
     * @return
     */
    private ChartPieData createAnalyseStatPieData(DietAnalyseSH sf) {
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("饮食分析");
        chartPieData.setUnit("次");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("食物");
        Map<String, DietAnalyseStat> map = this.getDietAnalyseStat(sf);
        for (String key : map.keySet()) {
            chartPieData.getXdata().add(key);
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(key);
            dataDetail.setValue(map.get(key).getTotalCount().longValue());
            serieData.getData().add(dataDetail);
        }
        String subTitle = this.getDateTitle(sf);
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(serieData);
        return chartPieData;
    }

    /**
     * 封装消费记录分析的柱状图数据
     *
     * @param sf
     * @return
     */
    private ChartData createAnalyseStatBarData(DietAnalyseSH sf) {
        ChartData chartData = new ChartData();
        chartData.setTitle("饮食分析");
        chartData.setUnit("次");
        chartData.setLegendData(new String[]{"食物"});
        ChartYData yData = new ChartYData("食物","次");
        Map<String, DietAnalyseStat> map = this.getDietAnalyseStat(sf);
        // 将map.entrySet()转换成list
        List<Map.Entry<String, DietAnalyseStat>> list = new ArrayList<>(map.entrySet());
        // 通过比较器来实现排序
        Collections.sort(list, new Comparator<Map.Entry<String, DietAnalyseStat>>() {
            @Override
            public int compare(Map.Entry<String, DietAnalyseStat> o1, Map.Entry<String, DietAnalyseStat> o2) {
                // 降序排序
                return o2.getValue().getTotalCount().compareTo(o1.getValue().getTotalCount());
            }
        });
        for (Map.Entry<String, DietAnalyseStat> m : list) {
            chartData.getXdata().add(m.getKey());
            yData.getData().add(m.getValue().getTotalCount().longValue());
        }
        String subTitle = this.getDateTitle(sf);
        chartData.setSubTitle(subTitle);
        chartData.getYdata().add(yData);
        return chartData;

    }

    private Map<String, DietAnalyseStat> getDietAnalyseStat(DietAnalyseSH sf) {
        List<FoodCategory> dcList = null;
        DietStatField statField = sf.getField();
        //是否根据类别统计
        boolean isDs = false;
        if (statField == DietStatField.CLASS_NAME || statField == DietStatField.TYPE) {
            isDs = true;
            dcList = this.getActiveFoodCategoryList();
        }
        List<DietAnalyseStat> list = null;
        if (sf.getField() == DietStatField.FOOD_TYPE ||
                sf.getField() == DietStatField.DIET_SOURCE ||
                sf.getField() == DietStatField.DIET_TYPE) {
            list = dietService.getDietAnalyseTypeStat(sf);
        } else {
            list = dietService.getDietAnalyseStat(sf);
        }
        Map<String, DietAnalyseStat> map = new HashMap<>();
        for (DietAnalyseStat da : list) {
            FoodCategory dc = null;
            if (isDs) {
                dc = this.findDietCategory(dcList, da.getName());
                if (dc == null) {
                    if (!sf.isIncludeUnknown()) {
                        continue;
                    }
                    dc = new FoodCategory();
                    dc.setClassName("未知");
                    dc.setType("未知");
                }
                String key = null;
                if (statField == DietStatField.CLASS_NAME) {
                    key = dc.getClassName();
                } else if (statField == DietStatField.TYPE) {
                    key = dc.getType();
                }
                DietAnalyseStat dass = map.get(key);
                if (dass == null) {
                    dass = new DietAnalyseStat();
                    dass.setName(da.getName());
                    dass.setTotalCount(da.getTotalCount());
                    map.put(key, dass);
                } else {
                    //往里面追加值
                    dass.setTotalCount(dass.getTotalCount()+da.getTotalCount());
                }
            } else {
                map.put(da.getName(), da);
            }
        }
        return map;
    }

    /**
     * 食物的平均相似度
     *
     * @return
     */
    @RequestMapping(value = "/foodsAvgSim", method = RequestMethod.GET)
    public ResultBean foodsAvgSim(@Valid DietVarietySH sf) {
        return callback(dietHandler.getFoodsAvgSim(sf));
    }

    /**
     * 食物的词云
     *
     * @return
     */
    @RequestMapping(value = "/wordCloudStat", method = RequestMethod.GET)
    public ResultBean wordCloudStat(@Valid DietWordCloudSH sf) {
        List<NameCountDTO> tagsList = dietService.statTags(sf);
        ChartWorldCloudData chartData = new ChartWorldCloudData();
        for(NameCountDTO s : tagsList){
            ChartNameValueVo dd = new ChartNameValueVo();
            dd.setName(s.getName());
            dd.setValue(s.getCounts().intValue());
            chartData.addData(dd);
        }
        chartData.setTitle("饮食词云统计");
        return callback(chartData);
    }

    /**
     * 食物的相似度
     *
     * @return
     */
    @RequestMapping(value = "/simLogStat", method = RequestMethod.GET)
    public ResultBean simLogStat(@Valid DietAvgVarietyLogSH sf) {
        sf.setPage(PageRequest.NO_PAGE);
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(DietVarietyLog.class);
        Sort s = new Sort("endDate", Sort.ASC);
        pr.addSort(s);
        List<DietVarietyLog> list = baseService.getBeanList(pr);
        ChartData chartData = new ChartData();
        DietType dietType = sf.getDietType();
        String dietTypeName = dietType == null ? "" : dietType.getName();
        chartData.setTitle("[" + dietTypeName + "]多样性");
        chartData.setUnit("%");
        chartData.setLegendData(new String[]{"重复度(%)"});
        ChartYData yData1 = new ChartYData();
        yData1.setName("重复度(%)");
        for (DietVarietyLog bean : list) {
            chartData.getXdata().add(DateUtil.getFormatDate(bean.getEndDate(), DateUtil.FormatDay1));
            yData1.getData().add(NumberUtil.getValue(bean.getVariety() * 100,0));
        }
        chartData.getYdata().add(yData1);
        return callback(chartData);
    }
}
