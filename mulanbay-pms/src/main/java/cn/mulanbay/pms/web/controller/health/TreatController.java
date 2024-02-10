package cn.mulanbay.pms.web.controller.health;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.NullType;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import cn.mulanbay.pms.handler.UserHandler;
import cn.mulanbay.pms.persistent.domain.Treat;
import cn.mulanbay.pms.persistent.domain.TreatDrug;
import cn.mulanbay.pms.persistent.domain.TreatOperation;
import cn.mulanbay.pms.persistent.domain.UserSet;
import cn.mulanbay.pms.persistent.dto.health.*;
import cn.mulanbay.pms.persistent.enums.ChartType;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.service.TreatService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.pms.web.bean.req.health.drug.TreatDrugSH;
import cn.mulanbay.pms.web.bean.req.health.operation.TreatOperationSH;
import cn.mulanbay.pms.web.bean.req.health.treat.TreatForm;
import cn.mulanbay.pms.web.bean.req.health.treat.*;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.bean.res.health.TreatSummaryStatVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

import static cn.mulanbay.pms.common.Constant.SCALE;

/**
 * 看病记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/treat")
public class TreatController extends BaseController {

    private static Class<Treat> beanClass = Treat.class;

    /**
     * 分类分组的时长
     * 例：365天则说明统计最近一年内的分组信息
     */
    @Value("${mulanbay.health.categoryDays}")
    int categoryDays;

    @Autowired
    TreatService treatService;

    @Autowired
    SystemConfigHandler systemConfigHandler;

    @Autowired
    UserHandler userHandler;

    /**
     * 获取看病或者器官的各种分类归类
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(TreatGroupSH sf) {
        try {
            Date endDate = new Date();
            Date startDate = DateUtil.getDate(-categoryDays,endDate);
            sf.setStartDate(startDate);
            sf.setEndDate(endDate);
            List<String> categoryList = treatService.getTreatCateList(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (String cc : categoryList) {
                //标签的话有多个
                String[] ss = cc.split(",");
                for(String s : ss){
                    TreeBean tb = new TreeBean();
                    tb.setId(s);
                    tb.setText(s);
                    list.add(tb);
                }
            }
            return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取看病的各种分类归类异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(TreatSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("treatTime", Sort.DESC);
        pr.addSort(s);
        PageResult<Treat> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid TreatForm form) {
        UserSet us = userHandler.getUserSet(form.getUserId());
        this.checkConsumeSync(form.getSyncToConsume(), us);
        Treat bean = new Treat();
        BeanCopy.copy(form, bean);
        treatService.saveTreat(bean, form.getSyncToConsume(), us);
        return callback(bean);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "treatId") Long treatId) {
        Treat bean = baseService.getObject(beanClass, treatId);
        return callback(bean);
    }

    /**
     * 检查同步配置
     * @param syncToConsume
     * @param us
     */
    private void checkConsumeSync(boolean syncToConsume,UserSet us){
        if(syncToConsume){
            if(us.getTreatSourceId()==null||us.getTreatGoodsTypeId()==null){
                throw new ApplicationException(PmsCode.TREAT_SYNC_UN_CONFIG);
            }
        }
    }
    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid TreatForm form) {
        UserSet us = userHandler.getUserSet(form.getUserId());
        this.checkConsumeSync(form.getSyncToConsume(), us);
        Treat bean = baseService.getObject(beanClass, form.getTreatId());
        BeanCopy.copy(form, bean);
        treatService.saveTreat(bean, form.getSyncToConsume(),us);
        return callback(bean);
    }

    /**
     * 复制
     *
     * @return
     */
    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public ResultBean copy(@RequestBody @Valid TreatCopyForm form) {
        UserSet us = userHandler.getUserSet(form.getUserId());
        this.checkConsumeSync(form.getSyncToConsume(), us);
        Treat bean = baseService.getObject(beanClass, form.getTreatId());
        Treat copyBean = new Treat();
        BeanCopy.copy(bean,copyBean);
        copyBean.setTreatId(null);
        copyBean.setTreatTime(form.getTreatTime());
        copyBean.setStage(form.getStage());
        copyBean.setCreatedTime(null);
        copyBean.setModifyTime(null);
        List<TreatDrug> copyDrugList = new ArrayList<>();
        if(form.getCopyDrug()){
            TreatDrugSH ds = new TreatDrugSH();
            ds.setUserId(form.getUserId());
            ds.setTreatId(form.getTreatId());
            ds.setPage(BaseHibernateDao.NO_PAGE);
            PageRequest pr = ds.buildQuery();
            pr.setBeanClass(TreatDrug.class);
            List<TreatDrug> drugList = baseService.getBeanList(pr);
            for(TreatDrug d : drugList){
                TreatDrug copy = new TreatDrug();
                BeanCopy.copy(d,copy);
                copy.setDrugId(null);
                copy.setCreatedTime(null);
                copy.setModifyTime(null);
                copyDrugList.add(copy);
            }
        }
        List<TreatOperation> copyOperationList = new ArrayList<>();
        if(form.getCopyOperation()){
            TreatOperationSH ds = new TreatOperationSH();
            ds.setUserId(form.getUserId());
            ds.setTreatId(form.getTreatId());
            ds.setPage(BaseHibernateDao.NO_PAGE);
            PageRequest pr = ds.buildQuery();
            pr.setBeanClass(TreatDrug.class);
            List<TreatOperation> operationList = baseService.getBeanList(pr);
            for(TreatOperation d : operationList){
                TreatOperation copy = new TreatOperation();
                BeanCopy.copy(d,copy);
                copy.setOperationId(null);
                copy.setCreatedTime(null);
                copy.setModifyTime(null);
                copyOperationList.add(copy);
            }
        }
        treatService.copyTreat(copyBean, form.getSyncToConsume(), us,copyDrugList,copyOperationList);
        return callback(null);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        for (String s : deleteRequest.getIds().split(",")) {
            treatService.deleteTreat(Long.valueOf(s));
        }
        return callback(null);
    }


    /**
     * 总的概要统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(TreatSH sf) {
        TreatSummaryStat data = treatService.getTreatStat(sf);
        //统计医保、个人的支付比例
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("费用统计");
        chartPieData.setSubTitle(this.getDateTitle(sf));
        chartPieData.getXdata().add("医保支付");
        chartPieData.getXdata().add("个人支付");
        chartPieData.getXdata().add("挂号费");
        chartPieData.getXdata().add("药费");
        chartPieData.getXdata().add("手术费");
        //总体统计
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("医保/个人");
        ChartPieSerieDetailData miFeeDataDetail = new ChartPieSerieDetailData();
        miFeeDataDetail.setName("医保支付");
        miFeeDataDetail.setValue(data.getTotalMiFee());
        serieData.getData().add(miFeeDataDetail);

        ChartPieSerieDetailData pdFeeDataDetail = new ChartPieSerieDetailData();
        pdFeeDataDetail.setName("个人支付");
        pdFeeDataDetail.setValue(data.getTotalPdFee());
        serieData.getData().add(pdFeeDataDetail);

        //分类统计
        ChartPieSerieData serie2Data = new ChartPieSerieData();
        serie2Data.setName("分项费用");
        ChartPieSerieDetailData rgFeeDataDetail = new ChartPieSerieDetailData();
        rgFeeDataDetail.setName("挂号费");
        rgFeeDataDetail.setValue(data.getTotalRegFee());
        serie2Data.getData().add(rgFeeDataDetail);

        ChartPieSerieDetailData drugFeeDataDetail = new ChartPieSerieDetailData();
        drugFeeDataDetail.setName("药费");
        drugFeeDataDetail.setValue(data.getTotalDrugFee());
        serie2Data.getData().add(drugFeeDataDetail);

        ChartPieSerieDetailData operationFeeDataDetail = new ChartPieSerieDetailData();
        operationFeeDataDetail.setName("手术费");
        operationFeeDataDetail.setValue(data.getTotalOperationFee());
        serie2Data.getData().add(operationFeeDataDetail);

        chartPieData.getDetailData().add(serieData);
        chartPieData.getDetailData().add(serie2Data);

        TreatSummaryStatVo vo = new TreatSummaryStatVo();
        BeanCopy.copy(data,vo);
        vo.setChartData(chartPieData);
        return callback(data);
    }

    /**
     * 统计分析
     *
     * @return
     */
    @RequestMapping(value = "/analyseStat", method = RequestMethod.GET)
    public ResultBean analyseStat(TreatAnalyseStatSH sf) {
        if ("tags".equals(sf.getGroupField())) {
            //如果是按照疾病标签统计，那么tags字段需要不为空
            //TODO tags可能有多个标签，需要拆分，大部分情况下只有一个标签
            sf.setGroupTags(NullType.NOT_NULL);
        }
        List<TreatAnalyseStat> list = treatService.getTreatAnalyseStat(sf);
        if (sf.getChartType() == ChartType.MIX_LINE_BAR) {
            return callback(this.createAnalyseStatBarData(list, sf));
        } else {
            return callback(this.createAnalyseStatPieData(list, sf));
        }

    }

    /**
     * 封装看病记录分析的饼状图数据
     *
     * @param list
     * @param sf
     * @return
     */
    private ChartPieData createAnalyseStatPieData(List<TreatAnalyseStat> list, TreatAnalyseStatSH sf) {
        String unit;
        if (sf.getGroupType() == GroupType.COUNT) {
            unit = "次";
        } else {
            unit = "元";
        }
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("看病记录分析");
        chartPieData.setUnit(unit);
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName(sf.getGroupType().getName());
        serieData.setUnit(unit);
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        //总的值
        BigDecimal totalCount = new BigDecimal(0);
        for (TreatAnalyseStat bean : list) {
            chartPieData.getXdata().add(bean.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(bean.getName());
            if (sf.getGroupType() == GroupType.COUNT) {
                dataDetail.setValue(bean.getTotalCount());
            } else {
                dataDetail.setValue(bean.getTotalFee());
            }
            totalCount = totalCount.add(new BigDecimal(bean.getTotalCount()));
            totalValue = totalValue.add(bean.getTotalFee());
            serieData.getData().add(dataDetail);
        }
        String subTitle = this.getDateTitle(sf, totalCount.longValue() + "次，" + totalValue.doubleValue() + "元");
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(serieData);
        return chartPieData;
    }

    /**
     * 封装看病记录分析的柱状图数据
     *
     * @param list
     * @param sf
     * @return
     */
    private ChartData createAnalyseStatBarData(List<TreatAnalyseStat> list, TreatAnalyseStatSH sf) {
        ChartData chartData = new ChartData();
        chartData.setTitle("看病记录分析");
        chartData.setLegendData(new String[]{"费用","次数"});
        ChartYData yData1 = new ChartYData("费用","元");
        ChartYData yData2 = new ChartYData("次数","次");
        //混合图形下使用
        chartData.addYAxis("费用","元");
        chartData.addYAxis("次数","次");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        //总的值
        BigDecimal totalCount = new BigDecimal(0);
        for (TreatAnalyseStat bean : list) {
            chartData.getXdata().add(bean.getName());
            yData1.getData().add(bean.getTotalFee());
            yData2.getData().add(bean.getTotalCount());
            totalCount = totalCount.add(new BigDecimal(bean.getTotalCount()));
            totalValue = totalValue.add(bean.getTotalFee());
        }
        chartData.getYdata().add(yData1);
        chartData.getYdata().add(yData2);
        String subTitle = this.getDateTitle(sf, totalCount.longValue() + "次，" + totalValue.doubleValue() + "元");
        chartData.setSubTitle(subTitle);
        return chartData;

    }

    /**
     * 基于日期统计
     *
     * @return
     */
    @RequestMapping(value = "/dateStat", method = RequestMethod.GET)
    public ResultBean dateStat(TreatDateStatSH sf) {
        switch (sf.getDateGroupType()){
            case DAYCALENDAR :
                //日历
                return callback(this.createChartCalendarDataDateStat(sf));
            case HOURMINUTE :
                //散点图
                return callback(createScatterChartData(sf));
            default:
                return callback(createDateStatChartData(sf));
        }
    }

    private ChartData createDateStatChartData(TreatDateStatSH sf){
        List<TreatDateStat> list = treatService.statDateTreatRecord(sf);
        ChartData chartData = new ChartData();
        chartData.setTitle("看病统计");
        chartData.setLegendData(new String[]{"费用","次数"});
        //混合图形下使用
        chartData.addYAxis("费用","元");
        chartData.addYAxis("次数","次");
        ChartYData yData1 = new ChartYData("次数","次");
        ChartYData yData2 = new ChartYData("费用","元");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        //总的值
        BigDecimal totalCount = new BigDecimal(0);
        for (TreatDateStat bean : list) {
            chartData.addXData(bean, sf.getDateGroupType());
            yData1.getData().add(bean.getTotalCount());
            yData2.getData().add(bean.getTotalFee());
            totalCount = totalCount.add(new BigDecimal(bean.getTotalCount()));
            totalValue = totalValue.add(bean.getTotalFee());
        }
        chartData.getYdata().add(yData2);
        chartData.getYdata().add(yData1);
        String subTitle = this.getDateTitle(sf, totalCount.longValue() + "次，" + totalValue.doubleValue() + "元");
        chartData.setSubTitle(subTitle);
        chartData = ChartUtil.completeDate(chartData, sf);
        return chartData;
    }

    private ScatterChartData createScatterChartData(TreatDateStatSH sf){
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        List<Date> dateList = treatService.getTreatDateList(sf);
        return ChartUtil.createHMChartData(dateList,"看病分析","看病时间点");
    }

    /**
     * 日历图
     *
     * @param sf
     * @return
     */
    private ChartCalendarData createChartCalendarDataDateStat(TreatDateStatSH sf) {
        List<TreatDateStat> list = treatService.statDateTreatRecord(sf);
        ChartCalendarData calendarData = ChartUtil.createChartCalendarData("看病统计", "次数", "次", sf, list);
        if (!StringUtil.isEmpty(sf.getDisease())) {
            List<Date> dateList = treatService.getTreatDateList(sf);
            for (Date tb : dateList) {
                calendarData.addGraph(tb, 1);
            }

        } else {
            calendarData.setTop(3);
        }
        return calendarData;
    }

    /**
     * 同期比对统计
     *
     * @return
     */
    @RequestMapping(value = "/yoyStat")
    public ResultBean yoyStat(@Valid TreatYoyStatSH sf) {
        if (sf.getDateGroupType() == DateGroupType.DAY) {
            return callback(createChartCalandarMultiData(sf));
        }
        String unit;
        if (sf.getGroupType() == GroupType.COUNT) {
            unit = "次";
        } else {
            unit = sf.getGroupType().getUnit();
        }
        ChartData chartData = initYoyCharData(sf, "看病记录同期对比", null);
        chartData.setUnit(unit);
        String[] legendData = new String[sf.getYears().size()];
        for (int i = 0; i < sf.getYears().size(); i++) {
            legendData[i] = sf.getYears().get(i).toString();
            TreatDateStatSH monthStatSearch = this.generateSearch(sf.getYears().get(i), sf);
            ChartYData yData = new ChartYData(sf.getYears().get(i).toString(),unit);
            List<TreatDateStat> list = treatService.statDateTreatRecord(monthStatSearch);
            //临时内容，作为补全用
            ChartData temp = new ChartData();
            for (TreatDateStat bean : list) {
                temp.addXData(bean, sf.getDateGroupType());
                if (sf.getGroupType() == GroupType.COUNT) {
                    yData.getData().add(bean.getTotalCount());
                } else {
                    yData.getData().add(bean.getTotalFee());
                }
            }
            //临时内容，作为补全用
            temp.getYdata().add(yData);
            monthStatSearch.setCompleteDate(true);
            temp = ChartUtil.completeDate(temp, monthStatSearch);
            //设置到最终的结果集中
            chartData.getYdata().add(temp.getYdata().get(0));
        }
        chartData.setLegendData(legendData);

        return callback(chartData);
    }

    private TreatDateStatSH generateSearch(int year, TreatYoyStatSH sf) {
        //数据,为了代码复用及统一，统计还是按照日期的统计
        TreatDateStatSH monthStatSearch = new TreatDateStatSH();
        monthStatSearch.setStartDate(DateUtil.getDate(year + "-01-01", DateUtil.FormatDay1));
        monthStatSearch.setEndDate(DateUtil.getDate(year + "-12-31", DateUtil.FormatDay1));
        monthStatSearch.setUserId(sf.getUserId());
        monthStatSearch.setFeeField(sf.getFeeField());
        monthStatSearch.setDateGroupType(sf.getDateGroupType());
        monthStatSearch.setName(sf.getName());
        monthStatSearch.setTreatType(sf.getTreatType());
        return monthStatSearch;
    }

    /**
     * 基于日历的热点图
     *
     * @param sf
     * @return
     */
    private ChartCalendarMultiData createChartCalandarMultiData(TreatYoyStatSH sf) {
        ChartCalendarMultiData data = new ChartCalendarMultiData();
        data.setTitle("看病记录同期对比");
        if (sf.getGroupType() == GroupType.COUNT) {
            data.setUnit("次");
        } else {
            data.setUnit("元");
        }
        for (int i = 0; i < sf.getYears().size(); i++) {
            TreatDateStatSH monthStatSearch = this.generateSearch(sf.getYears().get(i), sf);
            List<TreatDateStat> list = treatService.statDateTreatRecord(monthStatSearch);
            for (TreatDateStat bean : list) {
                String dateString = DateUtil.getFormatDateString(bean.getDateIndexValue().toString(), "yyyyMMdd", "yyyy-MM-dd");
                if (sf.getGroupType() == GroupType.COUNT) {
                    data.addData(sf.getYears().get(i), dateString, bean.getTotalCount());
                } else {
                    data.addData(sf.getYears().get(i), dateString, NumberUtil.getValue(bean.getTotalFee(),SCALE));
                }
            }
        }
        return data;
    }


    /**
     * 关键字列表
     *
     * @return
     */
    @RequestMapping(value = "/getTagsTree")
    public ResultBean getTagsTree(TreatTagsSH sf) {
        List<TreeBean> list = new ArrayList<TreeBean>();
        Date endDate = new Date();
        Date startDate = DateUtil.getDate(-categoryDays,endDate);
        sf.setStartDate(startDate);
        sf.setEndDate(endDate);
        List<String> tagsList = treatService.getTagsList(sf);
        //去重,不需要，实际上每次看病只会是一个病，如果是两种病，肯定也是两个科室去看，会有两条看病记录
        //Set<String> tagsSet = deleteDuplicate(tagsList);
        for (String s : tagsList) {
            TreeBean tb = new TreeBean();
            tb.setText(s);
            tb.setId(s);
            list.add(tb);
        }
        return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
    }

    /**
     * 疾病概况统计
     *
     * @return
     */
    @RequestMapping(value = "/fullStat", method = RequestMethod.GET)
    public ResultBean fullStat(TreatFullStatSH sf) {
        PageResult<TreatFullStat> res = new PageResult(sf.getPage(), sf.getPageSize());
        List<TreatFullStat> list = treatService.getFullStat(sf);
        res.setBeanList(list);
        if (!list.isEmpty()) {
            //求最大行数
            long n = treatService.getMaxRowOfFullStat(sf);
            res.setMaxRow(n);
        }
        return callbackDataGrid(res);
    }

    /**
     * 总体统计
     *
     * @return
     */
    @RequestMapping(value = "/overallStat")
    public ResultBean overallStat(@Valid TreatOverallStatSH sf) {
        ChartHeatmapData chartData = new ChartHeatmapData();
        chartData.setTitle("看病统计");
        DateGroupType dateGroupType = sf.getDateGroupType();
        int[] minMax = ChartUtil.getMinMax(dateGroupType,sf.getStartDate(),sf.getEndDate());
        int min = minMax[0];
        int max = minMax[1];
        List<String> xdata = ChartUtil.getStringXdataList(dateGroupType,min, max);
        chartData.setXdata(xdata);
        List<TreatOverallStat> list = treatService.getOverallStat(sf);
        int stn = list.size();
        //Y轴
        Map<String,OverallYIndex> yMap = new HashMap<>();
        int yxi =0;
        for(int i=0;i<stn;i++){
            TreatOverallStat tros = list.get(i);
            String key = tros.getName();
            if(yMap.get(key)!=null){
                continue;
            }
            yMap.put(key,new OverallYIndex(key,key,"次",(yxi++)));
            chartData.addYData(key);
        }
        GroupType groupType = sf.getGroupType();
        ChartHeatmapSerieData serieData = new ChartHeatmapSerieData(groupType.getName());
        for (int i=0;i<stn;i++) {
            TreatOverallStat seos = list.get(i);
            int indexValue = seos.getIndexValue();
            if(dateGroupType==DateGroupType.DAY){
                indexValue = DateUtil.getDayOfYear(DateUtil.getDate(indexValue+"","yyyyMMdd"));
            }
            int xIndex = ChartUtil.getXIndex(dateGroupType,indexValue,min,xdata) ;
            OverallYIndex yi = yMap.get(seos.getName());
            int yIndex = yi.getIndex();
            double value =0;
            String unit ="次";
            if(groupType==GroupType.COUNT){
                value = seos.getTotalCount().doubleValue();
            }else{
                value = seos.getTotalFee().doubleValue();
                unit = "元";
            }
            chartData.updateMinMaxValue(value);
            serieData.addData(new Object[]{xIndex,yIndex,value,unit});
        }
        chartData.addSerieData(serieData);
        return callback(chartData);
    }

    /**
     * 关系图
     *
     * @return
     */
    @RequestMapping(value = "/relation", method = RequestMethod.GET)
    public ResultBean relation(TreatRelationSH rs) {
        String tags = rs.getTags();
        List<TreatUnionDTO> list = treatService.getTreatList(tags,rs.getUserId(),rs.getStartDate(),rs.getEndDate());
        if(StringUtil.isNotEmpty(tags)){
            ChartGraphData data = this.createGraphData(tags,rs.getUnionAll(),list);
            return callback(data);
        }else{
            if(rs.getStartDate()==null||rs.getEndDate()==null){
                //数据过多导致图形显示复杂
                return callbackErrorInfo("请选择时间段");
            }
            ChartGraphData data = this.createGraphData(rs.getUnionAll(),list);
            return callback(data);
        }
    }

    /**
     * 指定标签
     * @param tags
     * @param unionAll
     * @param list
     * @return
     */
    private ChartGraphData createGraphData(String tags,boolean unionAll,List<TreatUnionDTO> list){
        int n = list.size();
        String root = tags;
        ChartGraphData chartGraphData = new ChartGraphData();
        chartGraphData.addItem(root,0);
        chartGraphData.setCategoryNames(new String[]{"根","医院","科室","确诊疾病","药品","手术"});
        for(int i=0;i<n;i++){
            TreatUnionDTO dto = list.get(i);
            //第一级：医院
            String hospital = dto.getHospital();
            chartGraphData.addLink(root,hospital);
            chartGraphData.addItem(hospital,1);

            //第二级：科室
            String department = dto.getDepartment();
            chartGraphData.addLink(hospital,department);
            chartGraphData.addItem(department,2);

            //第三级：确诊疾病
            String confirmDisease = dto.getConfirmDisease();
            chartGraphData.addLink(hospital,confirmDisease);
            chartGraphData.addItem(confirmDisease,3);

            //第四级：药品
            String drugName = dto.getDrugName();
            if(StringUtil.isNotEmpty(drugName)){
                chartGraphData.addLink(hospital,drugName);
                chartGraphData.addItem(drugName,4);
                if(unionAll){
                    chartGraphData.addLink(confirmDisease,drugName);
                }
            }

            //第五级：手术
            String operationName = dto.getOperationName();
            if(StringUtil.isNotEmpty(operationName)){
                chartGraphData.addLink(hospital,operationName);
                chartGraphData.addItem(operationName,5);
                if(unionAll){
                    chartGraphData.addLink(confirmDisease,operationName);
                }
            }
        }
        chartGraphData.setTitle("["+tags+"]疾病分析");
        return chartGraphData;
    }

    /**
     * 不指定标签
     * @param list
     * @return
     */
    private ChartGraphData createGraphData(boolean unionAll,List<TreatUnionDTO> list){
        String root = "我的疾病";
        int n = list.size();
        ChartGraphData chartGraphData = new ChartGraphData();
        chartGraphData.addItem(root,0);
        chartGraphData.setCategoryNames(new String[]{"根","确诊疾病","医院","科室","药品","手术"});
        for(int i=0;i<n;i++){
            TreatUnionDTO dto = list.get(i);

            //第一级：确诊疾病
            String confirmDisease = dto.getConfirmDisease();
            chartGraphData.addLink(root,confirmDisease);
            chartGraphData.addItem(confirmDisease,1);

            //第二级：医院
            String hospital = dto.getHospital();
            chartGraphData.addLink(confirmDisease,hospital);
            chartGraphData.addItem(hospital,2);

            //第三级：科室
            String department = dto.getDepartment();
            chartGraphData.addLink(hospital,department);
            chartGraphData.addItem(department,3);

            //第四级：药品
            String drugName = dto.getDrugName();
            if(StringUtil.isNotEmpty(drugName)){
                chartGraphData.addLink(hospital,drugName);
                chartGraphData.addItem(drugName,4);
                if(unionAll){
                    chartGraphData.addLink(confirmDisease,drugName);
                }
            }

            //第五级：手术
            String operationName = dto.getOperationName();
            if(StringUtil.isNotEmpty(operationName)){
                chartGraphData.addLink(hospital,operationName);
                chartGraphData.addItem(operationName,5);
                if(unionAll){
                    chartGraphData.addLink(confirmDisease,operationName);
                }
            }
        }
        chartGraphData.setTitle("我的疾病分析");
        return chartGraphData;
    }
}
