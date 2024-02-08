package cn.mulanbay.pms.web.controller.sport;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Exercise;
import cn.mulanbay.pms.persistent.domain.Sport;
import cn.mulanbay.pms.persistent.domain.SportMilestone;
import cn.mulanbay.pms.persistent.dto.sport.ExerciseDateStat;
import cn.mulanbay.pms.persistent.dto.sport.ExerciseMultiStat;
import cn.mulanbay.pms.persistent.dto.sport.ExerciseOverallStat;
import cn.mulanbay.pms.persistent.dto.sport.ExerciseStat;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.NextMilestoneType;
import cn.mulanbay.pms.persistent.service.ExerciseService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.pms.web.bean.req.sport.exercise.*;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 运动锻炼管理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/exercise")
public class ExerciseController extends BaseController {

    private static Class<Exercise> beanClass = Exercise.class;

    /**
     * 消费记录的缓存队列的过期时间
     */
    @Value("${mulanbay.sport.maxHeartRate}")
    int maxHeartRate;

    @Autowired
    ExerciseService exerciseService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(ExerciseSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort(sf.getSortField().getField(), sf.getSortType());
        pr.addSort(s);
        PageResult<Exercise> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 总的概要统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(ExerciseStatSH sf) {
        ExerciseStat data = exerciseService.getStat(sf);
        return callback(data);
    }

    /**
     * 获取锻炼的多重统计：最大、最小、平均
     *
     * @return
     */
    @RequestMapping(value = "/multiStat", method = RequestMethod.GET)
    public ResultBean multiStat(ExerciseMultiStatSH sf) {
        ExerciseMultiStat data = exerciseService.getMultiStat(sf);
        return callback(data);
    }

    /**
     * 获取锻炼的多重统计：最大、最小、平均
     *
     * @return
     */
    @RequestMapping(value = "/getByMultiStat", method = RequestMethod.GET)
    public ResultBean getByMultiStat(ExerciseByMultiStatSH sf) {
        Long data = exerciseService.getExerciseIdByMultiStat(sf);
        return callback(data);
    }


    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid ExerciseForm form) {
        Exercise bean = new Exercise();
        BeanCopy.copy(form, bean);
        Sport sport = baseService.getObject(Sport.class,form.getSportId());
        bean.setSport(sport);
        exerciseService.saveExercise(bean, true);
        return callback(bean);
    }

    /**
     * 以某天的模板创建
     *
     * @return
     */
    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public ResultBean copy(@RequestBody @Valid ExerciseCopyForm form) {
        Date temEndTime = DateUtil.tillMiddleNight(form.getTemplateDate());
        ExerciseSH sf = new ExerciseSH();
        sf.setStartDate(form.getTemplateDate());
        sf.setEndDate(temEndTime);
        sf.setUserId(form.getUserId());
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("exerciseTime", Sort.ASC);
        pr.addSort(s);
        List<Exercise> list = baseService.getBeanList(pr);
        if (list.isEmpty()) {
            return callbackErrorInfo("无法找到[" + DateUtil.getFormatDate(form.getTemplateDate(), DateUtil.FormatDay1) + "]的锻炼记录");
        } else {
            Date ed = form.getBeginTime();
            List<Exercise> newList = new ArrayList<>();
            for (Exercise se : list) {
                Exercise nn = new Exercise();
                BeanCopy.copy(se, nn);
                nn.setExerciseId(null);
                nn.setCreatedTime(new Date());
                nn.setModifyTime(null);
                nn.setExerciseTime(ed);
                nn.setRemark("以模板新增");
                ed = new Date(ed.getTime() + nn.getDuration() * 60 * 1000);
                newList.add(nn);
            }
            exerciseService.saveExerciseList(newList, true);
        }
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "exerciseId") Long exerciseId) {
        Exercise bean = baseService.getObject(beanClass, exerciseId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid ExerciseForm form) {
        Exercise bean = baseService.getObject(beanClass, form.getExerciseId());
        BeanCopy.copy(form, bean);
        Sport sport = baseService.getObject(Sport.class,form.getSportId());
        bean.setSport(sport);
        exerciseService.saveExercise(bean, true);
        return callback(bean);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        //需要删除对应的里程碑信息
        String[] ss = deleteRequest.getIds().split(",");
        for (String s : ss) {
            exerciseService.deleteExercise(Long.valueOf(s));
        }
        return callback(null);
    }

    /**
     * 达到的里程碑，不管以前的锻炼是否已经有实现过
     * 前端采用grid，因此这里采用grid返回格式
     *
     * @return
     */
    @RequestMapping(value = "/achieveMilestones", method = RequestMethod.GET)
    public ResultBean achieveMilestones(@RequestParam(name = "exerciseId") Long exerciseId) {
        List<SportMilestone> list = exerciseService.getAchieveMilestones(exerciseId);
        PageResult<SportMilestone> qr = new PageResult<>();
        qr.setMaxRow(list.size());
        qr.setPageSize(list.size());
        qr.setBeanList(list);
        return callbackDataGrid(qr);
    }

    /**
     * 获取下一个待达到的里程碑
     *
     * @return
     */
    @RequestMapping(value = "/nextAchieveMilestone", method = RequestMethod.GET)
    public ResultBean nextAchieveMilestone(@RequestParam(name = "exerciseId") Long exerciseId,@RequestParam(name = "type") NextMilestoneType type) {
        Long sm = exerciseService.getNextAchieveMilestoneId(exerciseId, type);
        return callback(sm);
    }

    /**
     * 刷新最佳统计
     *
     * @return
     */
    @RequestMapping(value = "/refreshMaxStat", method = RequestMethod.GET)
    public ResultBean refreshMaxStat(@RequestParam(name = "sportId") Long sportId) {
        exerciseService.updateAndRefreshExerciseMaxStat(sportId);
        return callback(null);
    }

    /**
     * 锻炼管理的统计接口
     * 界面上使用echarts展示图表，后端返回的是核心模块的数据，不再使用Echarts的第三方jar包封装（比较麻烦）
     *
     * @return
     */
    @RequestMapping(value = "/dateStat", method = RequestMethod.GET)
    public ResultBean dateStat(@Valid ExerciseDateStatSH sf) {
        switch (sf.getDateGroupType()){
            case DAYCALENDAR :
                return callback(createChartCalendarData(sf));
            case HOURMINUTE :
                return callback(createScatterChartData(sf));
            default:
                return callback(createDateStatChart(sf));
        }
    }

    private ChartData createDateStatChart(ExerciseDateStatSH sf){
        ChartData chartData = new ChartData();
        chartData.setTitle("锻炼统计");
        //混合图形下使用
        chartData.addYAxis("数值","");
        chartData.addYAxis("次数","次");
        //总的值
        BigDecimal totalCount = new BigDecimal(0);
        BigDecimal totalKilometres = new BigDecimal(0);
        DateGroupType dateGroupType = sf.getDateGroupType();
        String unit="";
        if(sf.getSportId()!=null){
            Sport sport = baseService.getObject(Sport.class, sf.getSportId());
            unit = sport.getUnit();
        }
        String totalKey = "总" + unit + "数";
        String averKey = "平均" + unit + "数";
        //详细统计才加各种统计信息
        if (sf.getFullStat()) {
            if (dateGroupType == DateGroupType.DAY) {
                chartData.setLegendData(new String[]{totalKey, "锻炼时间", "平均速度", "配速", "最大心率", "平均心率", "锻炼次数"});
            } else {
                //非天计算的则要添加平均公里数
                chartData.setLegendData(new String[]{totalKey, averKey, "锻炼时间", "平均速度", "配速", "最大心率", "平均心率", "锻炼次数"});
            }
        } else {
            chartData.setLegendData(new String[]{totalKey, "锻炼次数"});
        }
        ChartYData kilometresData = new ChartYData(totalKey,unit);
        ChartYData averageKilometresData = new ChartYData(averKey,unit);
        ChartYData minutesData = new ChartYData("锻炼时间","分钟");
        ChartYData speedData = new ChartYData("平均速度","公里/小时");
        ChartYData paceData = new ChartYData("配速","分钟/公里");
        ChartYData maxHeartRateData = new ChartYData("最大心率","次/分钟");
        ChartYData averageHeartRateData = new ChartYData("平均心率","次/分钟");
        ChartYData countData = new ChartYData("锻炼次数","次");
        List<ExerciseDateStat> list = exerciseService.getDateStat(sf);
        for (ExerciseDateStat bean : list) {
            chartData.addXData(bean, sf.getDateGroupType());
            kilometresData.getData().add(bean.getTotalValue());
            averageKilometresData.getData().add(NumberUtil.getAvg(bean.getTotalValue(), bean.getTotalCount(), 2));
            minutesData.getData().add(NumberUtil.getAvg(bean.getTotalDuration().doubleValue(), bean.getTotalCount().intValue(), 0));
            speedData.getData().add(NumberUtil.getAvg(bean.getTotalSpeed(), bean.getTotalCount(), 1));
            paceData.getData().add(NumberUtil.getAvg(bean.getTotalPace(), bean.getTotalCount(), 1));
            maxHeartRateData.getData().add(NumberUtil.getAvg(bean.getTotalMaxHeartRate(), bean.getTotalCount(), 0));
            averageHeartRateData.getData().add(NumberUtil.getAvg(bean.getTotalAvgHeartRate().doubleValue(), bean.getTotalCount().intValue(), 0));
            countData.getData().add(bean.getTotalCount());
            totalCount = totalCount.add(new BigDecimal(bean.getTotalCount()));
            totalKilometres = totalKilometres.add(bean.getTotalValue());
        }
        String totalString = totalCount.longValue() + "(次)," + totalKilometres.doubleValue() + "(" + unit + ")";
        chartData.setSubTitle(this.getDateTitle(sf)+",总计:"+totalString);
        chartData.getYdata().add(kilometresData);
        if (sf.getFullStat()) {
            if (dateGroupType != DateGroupType.DAY) {
                chartData.getYdata().add(averageKilometresData);
            }
            chartData.getYdata().add(minutesData);
            chartData.getYdata().add(speedData);
            chartData.getYdata().add(paceData);
            chartData.getYdata().add(maxHeartRateData);
            chartData.getYdata().add(averageHeartRateData);
        }
        chartData.getYdata().add(countData);
        chartData = ChartUtil.completeDate(chartData, sf);
        return chartData;
    }

    private ScatterChartData createScatterChartData(ExerciseDateStatSH sf){
        //散点图
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        List<Date> dateList = exerciseService.getExerciseDateList(sf);
        return ChartUtil.createHMChartData(dateList,"锻炼分析","锻炼时间点");
    }

    private ChartCalendarData createChartCalendarData(ExerciseDateStatSH sf){
        //日历
        List<ExerciseDateStat> list = exerciseService.getDateStat(sf);
        ChartCalendarData calendarData = ChartUtil.createChartCalendarData("锻炼统计", "公里数", "km", sf, list);
        if (!StringUtil.isEmpty(sf.getBestField())) {
            //获取最佳记录
            List<Exercise> bests = exerciseService.getBestMilestoneExerciseList(sf);
            for (Exercise se : bests) {
                if ("mileageBest".equals(sf.getBestField())) {
                    calendarData.addGraph(se.getExerciseTime(), se.getValue());
                } else {
                    calendarData.addGraph(se.getExerciseTime(), se.getMaxSpeed());
                }
            }
        }
        return calendarData;
    }

    /**
     * 同期统计
     *
     * @return
     */
    @RequestMapping(value = "/yoyStat")
    public ResultBean yoyStat(@Valid ExerciseYoyStatSH sf) {
        if (sf.getDateGroupType() == DateGroupType.DAY) {
            return callback(createChartCalendarMultiData(sf));
        }
        Sport sp = baseService.getObject(Sport.class,sf.getSportId());
        String title = "["+sp.getSportName()+"]运动锻炼同期对比";
        ChartData chartData = initYoyCharData(sf, title, null);
        String unit = sp.getUnit();
        if(sf.getGroupType()== GroupType.VALUE){
            unit = sp.getUnit();
        }else{
            unit = sf.getGroupType().getUnit();
        }
        chartData.setUnit(unit);
        String[] legendData = new String[sf.getYears().size()];
        //是否统计值（参数只对公里数、锻炼时间有效）
        boolean isSum = sf.getSumValue() == null ? true : sf.getSumValue();
        for (int i = 0; i < sf.getYears().size(); i++) {
            legendData[i] = sf.getYears().get(i).toString();
            //数据,为了代码复用及统一，统计还是按照日期的统计
            ChartYData yData = new ChartYData(sf.getYears().get(i).toString(),unit);
            ExerciseDateStatSH dateSearch = generateSearch(sf.getYears().get(i), sf);
            List<ExerciseDateStat> list = exerciseService.getDateStat(dateSearch);
            //临时内容，作为补全用
            ChartData temp = new ChartData();
            for (ExerciseDateStat bean : list) {
                temp.addXData(bean, sf.getDateGroupType());
                Long cc = 1L;
                if (!isSum) {
                    // 如果取平均值的话要除以总条数
                    cc = bean.getTotalCount();
                }
                yData.getData().add(this.getStatValue(sf.getGroupType(), bean, cc));
            }
            //临时内容，作为补全用
            temp.getYdata().add(yData);
            dateSearch.setCompleteDate(true);
            temp = ChartUtil.completeDate(temp, dateSearch);
            //设置到最终的结果集中
            chartData.getYdata().add(temp.getYdata().get(0));
        }
        chartData.setLegendData(legendData);

        return callback(chartData);
    }

    /**
     * 基于日历的热点图
     *
     * @param sf
     * @return
     */
    private ChartCalendarMultiData createChartCalendarMultiData(ExerciseYoyStatSH sf) {
        ChartCalendarMultiData data = new ChartCalendarMultiData();
        data.setTitle("运动锻炼同期对比");
        data.setUnit(sf.getGroupType().getUnit());
        for (int i = 0; i < sf.getYears().size(); i++) {
            Long cc = 1L;
            ExerciseDateStatSH dateSearch = generateSearch(sf.getYears().get(i), sf);
            List<ExerciseDateStat> list = exerciseService.getDateStat(dateSearch);
            for (ExerciseDateStat bean : list) {
                String dateString = DateUtil.getFormatDateString(bean.getDateIndexValue().toString(), "yyyyMMdd", "yyyy-MM-dd");
                data.addData(sf.getYears().get(i), dateString, this.getStatValue(sf.getGroupType(), bean, cc));
            }
        }
        return data;
    }

    private ExerciseDateStatSH generateSearch(int year, ExerciseYoyStatSH sf) {
        ExerciseDateStatSH dateSearch = new ExerciseDateStatSH();
        dateSearch.setSportId(sf.getSportId());
        dateSearch.setDateGroupType(sf.getDateGroupType());
        dateSearch.setStartDate(DateUtil.getDate(year + "-01-01", DateUtil.FormatDay1));
        dateSearch.setEndDate(DateUtil.getDate(year + "-12-31", DateUtil.FormatDay1));
        dateSearch.setUserId(sf.getUserId());
        return dateSearch;
    }

    private Object getStatValue(GroupType groupType, ExerciseDateStat bean, Long cc) {
        if (groupType == GroupType.COUNT) {
            return bean.getTotalCount();
        } else if (groupType == GroupType.AVG_HEART_RATE) {
            return NumberUtil.getAvg(bean.getTotalAvgHeartRate(), bean.getTotalCount(), 0);
        } else if (groupType == GroupType.MAX_HEART_RATE) {
            return NumberUtil.getAvg(bean.getTotalMaxHeartRate(), bean.getTotalCount(), 0);
        } else if (groupType == GroupType.VALUE) {
            return NumberUtil.getAvg(bean.getTotalValue(), cc, 0);
        } else if (groupType == GroupType.DURATION) {
            return NumberUtil.getAvg(bean.getTotalValue(), cc, 0);
        } else if (groupType == GroupType.PACE) {
            return NumberUtil.getAvg(bean.getTotalPace(), bean.getTotalCount(), 2);
        } else if (groupType == GroupType.SPEED) {
            return NumberUtil.getAvg(bean.getTotalSpeed(), bean.getTotalCount(), 2);
        } else {
            return 0;
        }
    }

    /**
     * 总体统计
     *
     * @return
     */
    @RequestMapping(value = "/overallStat")
    public ResultBean overallStat(@Valid ExerciseOverallStatSH sf) {
        ChartHeatmapData chartData = new ChartHeatmapData();
        chartData.setTitle("锻炼统计");
        DateGroupType dateGroupType = sf.getDateGroupType();
        int[] minMax = ChartUtil.getMinMax(dateGroupType,sf.getStartDate(),sf.getEndDate());
        int min = minMax[0];
        int max = minMax[1];
        List<String> xdata = ChartUtil.getStringXdataList(dateGroupType,min, max);
        chartData.setXdata(xdata);
        //Y轴
        List<Sport> sportTypeList = exerciseService.getSportList(sf.getUserId());
        Map<String,OverallYIndex> yMap = new HashMap<>();
        int stn = sportTypeList.size();
        for(int i=0;i<stn;i++){
            Sport st = sportTypeList.get(i);
            yMap.put(st.getSportId().toString(),new OverallYIndex(st.getSportId().toString(),st.getSportName(),st.getUnit(),i));
            chartData.addYData(st.getSportName());
        }
        List<ExerciseOverallStat> list = exerciseService.statOverallSportExercise(sf);
        GroupType valueType = sf.getValueType();
        ChartHeatmapSerieData serieData = new ChartHeatmapSerieData(valueType.getName());
        int vn = list.size();
        for (int i=0;i<vn;i++) {
            ExerciseOverallStat seos = list.get(i);
            int indexValue = seos.getIndexValue();
            if(dateGroupType==DateGroupType.DAY){
                indexValue = DateUtil.getDayOfYear(DateUtil.getDate(indexValue+"","yyyyMMdd"));
            }
            int xIndex = ChartUtil.getXIndex(dateGroupType,indexValue,min,xdata) ;
            OverallYIndex yi = yMap.get(seos.getSportId().toString());
            int yIndex = yi.getIndex();
            double value =0;
            String unit ="次";
            if(valueType==GroupType.COUNT){
                value = seos.getTotalCount().doubleValue();
            }else if(valueType==GroupType.DURATION){
                value = NumberUtil.getValue(seos.getTotalDuration().doubleValue()/60.0,1);
                unit ="小时";
            }else if(valueType==GroupType.VALUE){
                value = seos.getTotalValue().doubleValue();
                unit = yi.getUnit();
            }
            chartData.updateMinMaxValue(value);
            serieData.addData(new Object[]{xIndex,yIndex,value,unit});
        }
        chartData.addSerieData(serieData);
        return callback(chartData);
    }


}
