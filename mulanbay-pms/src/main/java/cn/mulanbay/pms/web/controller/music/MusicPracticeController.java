package cn.mulanbay.pms.web.controller.music;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.domain.Instrument;
import cn.mulanbay.pms.persistent.domain.MusicPractice;
import cn.mulanbay.pms.persistent.domain.MusicPracticeDetail;
import cn.mulanbay.pms.persistent.dto.music.*;
import cn.mulanbay.pms.persistent.enums.ChartType;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.service.MusicPracticeService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.pms.web.bean.req.music.musicPractice.*;
import cn.mulanbay.pms.web.bean.req.music.musicPracticeDetail.MusicPracticeDetailSH;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static cn.mulanbay.pms.common.Constant.ROUNDING_MODE;

/**
 * 音乐练习记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/musicPractice")
public class MusicPracticeController extends BaseController {

    private static Class<MusicPractice> beanClass = MusicPractice.class;

    @Autowired
    MusicPracticeService musicPracticeService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(MusicPracticeSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("practiceDate", Sort.DESC);
        pr.addSort(s);
        PageResult<MusicPractice> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }


    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid MusicPracticeForm form) {
        MusicPractice bean = new MusicPractice();
        BeanCopy.copy(form, bean);
        bean.setCreatedTime(new Date());
        bean.setPracticeDate(DateUtil.getDate(bean.getStartTime(), DateUtil.FormatDay1));
        Instrument instrument = baseService.getObject(Instrument.class, form.getInstrumentId());
        bean.setInstrument(instrument);
        baseService.saveObject(bean);
        return callback(bean);
    }

    /**
     * 以某天的模板创建
     *
     * @return
     */
    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public ResultBean copy(@RequestBody @Valid MusicExerciseCopyForm form) {
        Date temEndTime = DateUtil.tillMiddleNight(form.getTemplateDate());
        MusicPracticeSH sf = new MusicPracticeSH();
        sf.setStartDate(form.getTemplateDate());
        sf.setEndDate(temEndTime);
        sf.setUserId(form.getUserId());
        sf.setInstrumentId(form.getInstrumentId());
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("practiceDate", Sort.ASC);
        pr.addSort(s);
        List<MusicPractice> list = baseService.getBeanList(pr);
        if (list.isEmpty()) {
            return callbackErrorInfo("无法找到[" + DateUtil.getFormatDate(form.getTemplateDate(), DateUtil.FormatDay1) + "]的音乐练习记录");
        } else {
            Date ed = form.getBeginTime();
            for (MusicPractice se : list) {
                MusicPractice nn = new MusicPractice();
                BeanCopy.copy(se, nn);
                nn.setPracticeId(null);
                nn.setPracticeDate(DateUtil.getDate(ed, DateUtil.FormatDay1));
                nn.setRemark("以模板新增,模版日期:" + DateUtil.getFormatDate(form.getTemplateDate(), DateUtil.FormatDay1));
                nn.setStartTime(ed);
                ed = new Date(ed.getTime() + nn.getMinutes() * 60 * 1000);
                nn.setEndTime(ed);
                //查找曲子练习记录
                List<MusicPracticeDetail> tuneList = musicPracticeService.getDetailList(se.getPracticeId());
                List<MusicPracticeDetail> newTuneList = new ArrayList<>();
                for (MusicPracticeDetail mpt : tuneList) {
                    MusicPracticeDetail ntn = new MusicPracticeDetail();
                    BeanCopy.copy(mpt, ntn);
                    ntn.setDetailId(null);
                    ntn.setRemark("以模板新增");
                    newTuneList.add(ntn);
                }
                musicPracticeService.addMusicPractice(nn, newTuneList);
            }
        }
        return callback(null);
    }


    /**
     * 查询详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "practiceId") Long practiceId) {
        MusicPractice bean = baseService.getObject(beanClass, practiceId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid MusicPracticeForm form) {
        MusicPractice bean = baseService.getObject(beanClass, form.getPracticeId());
        BeanCopy.copy(form, bean);
        bean.setPracticeDate(DateUtil.getDate(bean.getStartTime(), DateUtil.FormatDay1));
        Instrument instrument = baseService.getObject(Instrument.class, form.getInstrumentId());
        bean.setInstrument(instrument);
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
        String[] ids = deleteRequest.getIds().split(",");
        for (String id : ids) {
            MusicPractice bean = baseService.getObject(beanClass, Long.valueOf(id));
            musicPracticeService.deleteMusicPractice(bean);
        }
        return callback(null);
    }

    /**
     * 到目前为止的统计
     *
     * @return
     */
    @RequestMapping(value = "/tillNowStat", method = RequestMethod.GET)
    public ResultBean tillNowStat(@RequestParam(name = "practiceId") Long practiceId) {
        MusicPractice bean = baseService.getObject(beanClass, practiceId);
        MusicPracticeSummaryStat stat = musicPracticeService.getTillNowStat(bean.getPracticeDate(), bean.getUserId(), bean.getInstrument().getInstrumentId());
        return callback(stat);
    }

    /**
     * 总的概要统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(MusicPracticeStatSH sf) {
        // 获取乐器的分析信息
        List<InstrumentStat> list = musicPracticeService.getInstrumentStat(sf);
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("音乐练习统计");
        chartPieData.setSubTitle(ChartUtil.getDateTitle(sf));
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("音乐练习统计(小时)");
        ChartPieSerieData serieDataCount = new ChartPieSerieData();
        serieDataCount.setName("音乐练习统计(次)");
        for (InstrumentStat mp : list) {
            chartPieData.getXdata().add(mp.getInstrumentName());
            ChartPieSerieDetailData cp = new ChartPieSerieDetailData();
            cp.setName(mp.getInstrumentName());
            cp.setValue(minutesToHours(mp.getTotalMinutes()));
            serieData.getData().add(cp);

            ChartPieSerieDetailData cpCount = new ChartPieSerieDetailData();
            cpCount.setName(mp.getInstrumentName());
            cpCount.setValue(mp.getTotalCount());
            serieDataCount.getData().add(cpCount);
        }
        chartPieData.getDetailData().add(serieDataCount);
        chartPieData.getDetailData().add(serieData);
        return callback(chartPieData);
    }

    private double minutesToHours(BigDecimal minutes) {
        return DateUtil.minutesToHours(minutes);
    }

    /**
     * 按照日期统计
     *
     * @return
     */
    @RequestMapping(value = "/dateStat")
    public ResultBean dateStat(MusicPracticeDateStatSH sf) {
        switch (sf.getDateGroupType()) {
            case DAYCALENDAR:
                //日历
                return callback(createChartCalendarData(sf));
            case HOURMINUTE:
                return callback(createScatterChartData(sf));
            default:
                return callback(createDateStatChartData(sf));
        }
    }

    private ScatterChartData createScatterChartData(MusicPracticeDateStatSH sf){
        //散点图
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        List<Date> dateList = musicPracticeService.getPracticeDateList(sf);
        return ChartUtil.createHMChartData(dateList, "音乐练习分析", "练习时间点");
    }
    /**
     * 按时间属性统计
     *
     * @param sf
     * @return
     */
    private ChartData createDateStatChartData(MusicPracticeDateStatSH sf) {
        List<MusicPracticeDateStat> list = musicPracticeService.getDateStat(sf);
        ChartData chartData = new ChartData();
        chartData.setTitle(getChartTitle(sf.getInstrumentId()));
        chartData.setLegendData(new String[]{"总时长", "次数"});
        //混合图形下使用
        chartData.addYAxis("时长", "小时");
        chartData.addYAxis("次数", "次");
        ChartYData yData1 = new ChartYData("次数", "次");
        ChartYData yData2 = new ChartYData("总时长", "小时");
        ChartYData yData3 = new ChartYData("平均每天", "小时");
        ChartYData yData4 = new ChartYData("平均每次", "小时");
        //总的值
        BigDecimal totalCount = new BigDecimal(0);
        BigDecimal totalValue = new BigDecimal(0);
        int year = DateUtil.getYear(sf.getEndDate() == null ? new Date() : sf.getEndDate());
        for (MusicPracticeDateStat bean : list) {
            chartData.getIntXData().add(bean.getDateIndexValue());
            if (sf.getDateGroupType() == DateGroupType.MONTH) {
                chartData.getXdata().add(bean.getDateIndexValue() + "月份");
                int days = DateUtil.getDayOfMonth(year, bean.getDateIndexValue() - 1);
                yData3.getData().add(minutesToHours(bean.getTotalMinutes().divide(new BigDecimal(days), ROUNDING_MODE)));
            } else if (sf.getDateGroupType() == DateGroupType.YEAR) {
                chartData.getXdata().add(bean.getDateIndexValue() + "年");
            } else if (sf.getDateGroupType() == DateGroupType.WEEK) {
                chartData.getXdata().add("第" + bean.getDateIndexValue() + "周");
                yData3.getData().add(minutesToHours(bean.getTotalMinutes().divide(new BigDecimal(Constant.WEEK_DAY), ROUNDING_MODE)));
            } else {
                chartData.getXdata().add(bean.getDateIndexValue().toString());
            }
            yData1.getData().add(bean.getTotalCount());
            yData2.getData().add(minutesToHours(bean.getTotalMinutes()));
            yData4.getData().add(minutesToHours(bean.getTotalMinutes().divide(new BigDecimal(bean.getTotalCount()), ROUNDING_MODE)));
            totalCount = totalCount.add(new BigDecimal(bean.getTotalCount()));
            totalValue = totalValue.add(bean.getTotalMinutes());
        }
        chartData.getYdata().add(yData2);
        if (sf.getDateGroupType() == DateGroupType.WEEK || sf.getDateGroupType() == DateGroupType.MONTH || sf.getDateGroupType() == DateGroupType.YEAR) {
            //如果是周，计算每天锻炼值
            chartData.setLegendData(new String[]{"总时长", "平均每天", "平均每次", "次数"});
            chartData.getYdata().add(yData3);
            chartData.getYdata().add(yData4);
        }
        //次数放最后
        chartData.getYdata().add(yData1);
        String totalString = totalCount.longValue() + "(次)," + minutesToHours(totalValue) + "(小时)";
        chartData.setSubTitle(ChartUtil.getDateTitle(sf,totalString));
        chartData = ChartUtil.completeDate(chartData, sf);
        return chartData;
    }

    private ChartCalendarData createChartCalendarData(MusicPracticeDateStatSH sf) {
        List<MusicPracticeDateStat> list = musicPracticeService.getDateStat(sf);
        ChartCalendarData calendarData = ChartUtil.createChartCalendarData("音乐练习统计", "练习时间", "分钟", sf, list);
        if (!StringUtil.isEmpty(sf.getTune())) {
            MusicPracticeDetailSH mpts = new MusicPracticeDetailSH();
            BeanCopy.copy(sf, mpts);
            PageRequest pr = mpts.buildQuery();
            pr.setBeanClass(MusicPracticeDetail.class);
            calendarData.setGraphName("["+sf.getTune()+"]追踪");
            List<MusicPracticeDetail> dd = baseService.getBeanList(pr);
            //添加监控走势数据
            for (MusicPracticeDetail tt : dd) {
                calendarData.addGraph(tt.getPractice().getPracticeDate(), tt.getTimes());
            }
        } else {
            calendarData.setTop(3);
        }
        return calendarData;
    }

    /**
     * 按照日期统计
     *
     * @return
     */
    @RequestMapping(value = "/yoyStat")
    public ResultBean yoyStat(@Valid MusicPracticeYoyStatSH sf) {
        if (sf.getDateGroupType() == DateGroupType.DAY) {
            return callback(createChartCalendarMultiData(sf));
        } else {
            return callback(createYoyChartData(sf));
        }
    }

    /**
     * 基于日历的热点图
     *
     * @param sf
     * @return
     */
    private ChartCalendarMultiData createChartCalendarMultiData(MusicPracticeYoyStatSH sf) {
        ChartCalendarMultiData data = new ChartCalendarMultiData();
        data.setTitle("音乐练习同期对比");
        if (sf.getGroupType() == GroupType.COUNT) {
            data.setUnit("次");
        } else {
            data.setUnit("分钟");
        }
        for (int i = 0; i < sf.getYears().size(); i++) {
            MusicPracticeDateStatSH dateSearch = generateSearch(sf.getYears().get(i), sf);
            List<MusicPracticeDateStat> list = musicPracticeService.getDateStat(dateSearch);
            for (MusicPracticeDateStat bean : list) {
                String dateString = DateUtil.getFormatDate(bean.getDateIndexValue().toString(), "yyyyMMdd", "yyyy-MM-dd");
                if (sf.getGroupType() == GroupType.COUNT) {
                    data.addData(sf.getYears().get(i), dateString, bean.getTotalCount());
                } else {
                    data.addData(sf.getYears().get(i), dateString, bean.getTotalMinutes());
                }
            }
        }
        return data;
    }

    private MusicPracticeDateStatSH generateSearch(int year, MusicPracticeYoyStatSH sf) {
        MusicPracticeDateStatSH dateSearch = new MusicPracticeDateStatSH();
        dateSearch.setDateGroupType(sf.getDateGroupType());
        dateSearch.setStartDate(DateUtil.getDate(year + "-01-01", DateUtil.FormatDay1));
        dateSearch.setEndDate(DateUtil.getDate(year + "-12-31", DateUtil.FormatDay1));
        dateSearch.setUserId(sf.getUserId());
        return dateSearch;
    }


    /**
     * 同期对比数据
     *
     * @param sf
     * @return
     */
    private ChartData createYoyChartData(MusicPracticeYoyStatSH sf) {
        ChartData chartData = ChartUtil.initYoyCharData(sf, musicPracticeService.getInstrumentName(sf.getInstrumentId()) + "练习统计同期对比", null);
        chartData.setUnit(sf.getGroupType().getUnit());
        String[] legendData = new String[sf.getYears().size()];
        for (int i = 0; i < sf.getYears().size(); i++) {
            legendData[i] = sf.getYears().get(i).toString();
            //数据,为了代码复用及统一，统计还是按照日期的统计
            MusicPracticeDateStatSH dateSearch = new MusicPracticeDateStatSH();
            dateSearch.setDateGroupType(sf.getDateGroupType());
            dateSearch.setStartDate(DateUtil.getDate(sf.getYears().get(i) + "-01-01", DateUtil.FormatDay1));
            dateSearch.setEndDate(DateUtil.getDate(sf.getYears().get(i) + "-12-31", DateUtil.FormatDay1));
            dateSearch.setUserId(sf.getUserId());
            dateSearch.setInstrumentId(sf.getInstrumentId());
            ChartYData yData = new ChartYData();
            yData.setName(sf.getYears().get(i).toString());
            if (sf.getGroupType() == GroupType.COUNT) {
                yData.setUnit("次");
            } else {
                yData.setUnit("小时");
            }
            List<MusicPracticeDateStat> list = musicPracticeService.getDateStat(dateSearch);
            //临时内容，作为补全用
            ChartData temp = new ChartData();
            for (MusicPracticeDateStat bean : list) {
                temp.addXData(bean, sf.getDateGroupType());
                if (sf.getGroupType() == GroupType.COUNT) {
                    yData.getData().add(bean.getTotalCount());
                } else {
                    yData.getData().add(minutesToHours(bean.getTotalMinutes()));
                }
            }
            //临时内容，作为补全用
            temp.getYdata().add(yData);
            dateSearch.setCompleteDate(true);
            temp = ChartUtil.completeDate(temp, dateSearch);
            //设置到最终的结果集中
            chartData.getYdata().add(temp.getYdata().get(0));
        }
        chartData.setLegendData(legendData);
        return chartData;
    }

    /**
     * 按照时间统计（查看主要在哪个小时内练习，或者练习分钟数）
     *
     * @return
     */
    @RequestMapping(value = "/timeStat")
    public ResultBean timeStat(MusicPracticeTimeStatSH sf) {
        List<MusicPracticeTimeStat> list = musicPracticeService.getTimeStat(sf);
        if (sf.getChartType() == ChartType.PIE) {
            return createTimeStatPieData(list, sf);
        } else {
            return createTimeStatBarData(list, sf);
        }
    }

    /**
     * 获取时间统计的饼图数据
     *
     * @param list
     * @param sf
     * @return
     */
    private ResultBean createTimeStatPieData(List<MusicPracticeTimeStat> list, MusicPracticeTimeStatSH sf) {
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle(this.getChartTitle(sf.getInstrumentId()));
        chartPieData.setUnit("次");
        ChartPieSerieData serieData = new ChartPieSerieData();
        if (sf.getDateGroupType() == DateGroupType.MINUTE) {
            serieData.setName("练习时长");
        } else if (sf.getDateGroupType() == DateGroupType.HOUR) {
            serieData.setName("时间点");
        } else if (sf.getDateGroupType() == DateGroupType.DAY) {
            serieData.setName("天");
        } else if (sf.getDateGroupType() == DateGroupType.WEEK) {
            serieData.setName("周");
        }
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        for (MusicPracticeTimeStat bean : list) {
            String name = getTimeStatName(bean.getDateIndexValue(), sf.getDateGroupType());
            chartPieData.getXdata().add(name);
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(name);
            dataDetail.setValue(bean.getTotalCount());
            serieData.getData().add(dataDetail);
            totalValue = totalValue.add(new BigDecimal(bean.getTotalCount()));
        }
        String subTitle = ChartUtil.getDateTitle(sf);
        chartPieData.setSubTitle(subTitle + ",总计:" + totalValue.longValue() + "次");
        chartPieData.getDetailData().add(serieData);
        return callback(chartPieData);
    }

    /**
     * 获取时间统计的柱状图数据
     *
     * @param list
     * @param sf
     * @return
     */
    private ResultBean createTimeStatBarData(List<MusicPracticeTimeStat> list, MusicPracticeTimeStatSH sf) {
        ChartData chartData = new ChartData();
        chartData.setTitle(this.getChartTitle(sf.getInstrumentId()));
        chartData.setUnit("次");
        chartData.setLegendData(new String[]{"次数"});
        ChartYData yData1 = new ChartYData();
        yData1.setName("次数");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        int year = DateUtil.getYear(sf.getEndDate() == null ? new Date() : sf.getEndDate());
        for (MusicPracticeTimeStat bean : list) {
            chartData.getIntXData().add(bean.getDateIndexValue());
            chartData.getXdata().add(getTimeStatName(bean.getDateIndexValue(), sf.getDateGroupType()));
            yData1.getData().add(bean.getTotalCount());
            totalValue = totalValue.add(new BigDecimal(bean.getTotalCount()));
        }
        chartData.getYdata().add(yData1);
        chartData = ChartUtil.completeDate(chartData, sf);
        String subTitle = ChartUtil.getDateTitle(sf);
        chartData.setSubTitle(subTitle + ",总计:" + totalValue.longValue() + "次");
        return callback(chartData);
    }

    /**
     * 获取时间统计的名称表示
     *
     * @param dateGroupType
     * @return
     */
    private String getTimeStatName(Integer indexValue, DateGroupType dateGroupType) {
        return ChartUtil.getStringXData(dateGroupType, indexValue);
    }

    /**
     * 获取统计图表的表头
     *
     * @param musicInstrumentId
     * @return
     */
    private String getChartTitle(Long musicInstrumentId) {
        return musicPracticeService.getInstrumentName(musicInstrumentId) + "练习统计";
    }

    /**
     * 比对，采用散点图
     *
     * @return
     */
    @RequestMapping(value = "/compareStat")
    public ResultBean compareStat(@Valid MusicPracticeCompareStatSH sf) {
        ScatterChartData chartData = new ScatterChartData();
        chartData.setTitle("乐器练习比对");
        chartData.setxUnit(sf.getXgroupType().getName());
        chartData.setyUnit(sf.getYgroupType().getName());
        for (Long id : sf.getInstrumentIds()) {
            Instrument mi = baseService.getObject(Instrument.class, id);
            chartData.addLegent(mi.getInstrumentName());
            List<MusicPracticeCompareStat> list = musicPracticeService.getCompareStat(sf, id);
            ScatterChartDetailData detailData = new ScatterChartDetailData();
            detailData.setName(mi.getInstrumentName());
            double totalX = 0;
            int n = 0;
            for (MusicPracticeCompareStat stat : list) {
                detailData.addData(new Object[]{stat.getCXValue(), stat.getCYValue()});
                totalX += stat.getCXValue();
                n++;
            }
            detailData.setxAxisAverage(totalX / n);
            chartData.addSeriesData(detailData);
        }
        return callback(chartData);
    }

    /**
     * 总体统计
     *
     * @return
     */
    @RequestMapping(value = "/overallStat")
    public ResultBean overallStat(@Valid MusicPracticeOverallStatSH sf) {
        ChartHeatmapData chartData = new ChartHeatmapData();
        chartData.setTitle("音乐练习统计");
        DateGroupType dateGroupType = sf.getDateGroupType();
        int[] minMax = ChartUtil.getMinMax(dateGroupType, sf.getStartDate(), sf.getEndDate());
        int min = minMax[0];
        int max = minMax[1];
        List<String> xdata = ChartUtil.getStringXdataList(dateGroupType, min, max);
        chartData.setXdata(xdata);
        //Y轴
        List<Instrument> miLIst = musicPracticeService.getInstrumentList(sf.getUserId());
        Map<String, OverallYIndex> yMap = new HashMap<>();
        int stn = miLIst.size();
        for (int i = 0; i < stn; i++) {
            Instrument st = miLIst.get(i);
            yMap.put(st.getInstrumentId().toString(), new OverallYIndex(st.getInstrumentId().toString(), st.getInstrumentName(), "分钟", i));
            chartData.addYData(st.getInstrumentName());
        }
        List<MusicPracticeOverallStat> list = musicPracticeService.getOverallStat(sf);
        GroupType valueType = sf.getValueType();
        ChartHeatmapSerieData serieData = new ChartHeatmapSerieData(valueType.getName());
        int vn = list.size();
        for (int i = 0; i < vn; i++) {
            MusicPracticeOverallStat seos = list.get(i);
            int indexValue = seos.getIndexValue();
            if (dateGroupType == DateGroupType.DAY) {
                indexValue = DateUtil.getDayOfYear(DateUtil.getDate(indexValue + "", "yyyyMMdd"));
            }
            int xIndex = ChartUtil.getXIndex(dateGroupType, indexValue, min, xdata);
            OverallYIndex yi = yMap.get(seos.getInstrumentId().toString());
            int yIndex = yi.getIndex();
            double value = 0;
            String unit = "次";
            if (valueType == GroupType.COUNT) {
                value = seos.getTotalCount().doubleValue();
            } else if (valueType == GroupType.DURATION) {
                value = NumberUtil.getValue(seos.getTotalMinutes().divide(new BigDecimal(60.0), RoundingMode.HALF_UP), 1);
                unit = "小时";
            }
            chartData.updateMinMaxValue(value);
            serieData.addData(new Object[]{xIndex, yIndex, value, unit});
        }
        chartData.addSerieData(serieData);
        return callback(chartData);
    }


}
