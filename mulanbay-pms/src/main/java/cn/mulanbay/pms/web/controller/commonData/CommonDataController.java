package cn.mulanbay.pms.web.controller.commonData;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.handler.RewardHandler;
import cn.mulanbay.pms.persistent.domain.CommonData;
import cn.mulanbay.pms.persistent.domain.CommonDataType;
import cn.mulanbay.pms.persistent.dto.commonData.CommonDataAnalyseStat;
import cn.mulanbay.pms.persistent.dto.commonData.CommonDataDateStat;
import cn.mulanbay.pms.persistent.dto.commonData.CommonDataStat;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.ValueType;
import cn.mulanbay.pms.persistent.service.CommonDataService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.commonData.*;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.bean.res.commonData.CommonDataStatVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 通用记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/commonData")
public class CommonDataController extends BaseController {

    private static Class<CommonData> beanClass = CommonData.class;

    /**
     * 分类分组的时长
     * 例：365天则说明统计最近一年内的分组信息
     */
    @Value("${mulanbay.commonData.categoryDays}")
    int categoryDays;

    @Autowired
    CommonDataService commonDataService;

    @Autowired
    RewardHandler rewardHandler;

    /**
     * 获取任务列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(CommonDataSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("occurTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<CommonData> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 获取类型树
     *
     * @return
     */
    @RequestMapping(value = "/nameTree")
    public ResultBean nameTree(CommonDataNameTreeSH ts) {
        try {
            Date endDate = new Date();
            Date startDate = DateUtil.getDate(-categoryDays,endDate);
            ts.setStartDate(startDate);
            ts.setEndDate(endDate);
            ts.setPage(PageRequest.NO_PAGE);
            List<TreeBean> list = new ArrayList<TreeBean>();
            List<String> gtList = commonDataService.getNameList(ts);
            for (String s : gtList) {
                TreeBean tb = new TreeBean();
                tb.setId(s);
                tb.setText(s);
                list.add(tb);
            }
            return callback(TreeBeanUtil.addRoot(list, ts.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取类型树异常",
                    e);
        }
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid CommonDataForm form) {
        CommonDataType type = baseService.getObject(CommonDataType.class,form.getTypeId());
        CommonData bean = new CommonData();
        BeanCopy.copy(form, bean);
        bean.setType(type);
        bean.setCreatedTime(new Date());
        baseService.saveObject(bean);
        //增加积分
        int rp = type.getRewardPoint();
        if (rp != 0) {
            //这里修改为通用类型ID
            rewardHandler.reward(form.getUserId(), type.getRewardPoint(), bean.getDataId(),
                    BussSource.COMMON_DATA, "通用记录操作奖励", null);
        }
        return callback(bean);
    }


    /**
     * 查询
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "dataId") Long dataId) {
        CommonData bean = baseService.getObject(beanClass,dataId);
        return callback(bean);
    }

    /**
     * 查询最新
     *
     * @return
     */
    @RequestMapping(value = "/getNearest", method = RequestMethod.GET)
    public ResultBean getNearest(@Valid CommonDataNearestSH ls) {
        CommonData bean = commonDataService.getNearest(ls);
        return callback(bean);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(@Valid CommonDataStatSH ls) {
        CommonDataStat stat = commonDataService.getCommonDataStat(ls);
        CommonDataStatVo vo = new CommonDataStatVo();
        vo.setMinDate(stat.getMinDate());
        vo.setMaxDate(stat.getMaxDate());
        vo.setTotalCount(stat.getTotalCount());
        vo.setTotalValue(stat.getTotalValue());
        CommonDataType type = baseService.getObject(CommonDataType.class,ls.getTypeId());
        vo.setUnit(type.getUnit());
        return callback(vo);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid CommonDataForm form) {
        CommonDataType type = baseService.getObject(CommonDataType.class,form.getTypeId());
        CommonData bean = baseService.getObject(beanClass,form.getDataId());
        BeanCopy.copy(form, bean);
        bean.setType(type);
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
     * 分析
     *
     * @return
     */
    @RequestMapping(value = "/analyse")
    public ResultBean analyse(@Valid CommonDataAnalyseSH sf) {
        CommonDataType type = baseService.getObject(CommonDataType.class,sf.getTypeId());
        List<CommonDataAnalyseStat> list = commonDataService.getCommonDataAnalyseStat(sf);
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("["+type.getTypeName()+"]记录分析");
        chartPieData.setUnit(sf.getValueType().getName());
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("类型");
        ValueType valueType = sf.getValueType();
        if(valueType==ValueType.TIMES){
            serieData.setUnit("次");
        }else if(valueType==ValueType.MINUTE){
            serieData.setUnit(type.getUnit());
        }
        for (CommonDataAnalyseStat bean : list) {
            chartPieData.getXdata().add(bean.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(bean.getName());
            if(valueType==ValueType.TIMES){
                dataDetail.setValue(bean.getTotalCount());
            }else if(valueType==ValueType.MINUTE){
                dataDetail.setValue(bean.getTotalValue());
            }
            serieData.getData().add(dataDetail);
        }
        chartPieData.getDetailData().add(serieData);
        return callback(chartPieData);
    }

    /**
     * 时间线
     *
     * @return
     */
    @RequestMapping(value = "/timeline")
    public ResultBean timeline(@Valid CommonDataTimelineSH sf) {
        CommonDataType type = baseService.getObject(CommonDataType.class,sf.getTypeId());
        ChartData chartData = new ChartData();
        chartData.setTitle("["+type.getTypeName()+"]时间线分析");
        chartData.setUnit("分钟");
        //混合图形下使用
        chartData.addYAxis("时长","分钟");
        chartData.addYAxis("天数","天");
        chartData.setLegendData(new String[]{"时长","距离上次"});
        ChartYData yData1 = new ChartYData("时长","分钟");
        ChartYData yData2 = new ChartYData("距离上次","天");
        CommonDataSH crs = new CommonDataSH();
        BeanCopy.copy(sf,crs);
        PageRequest pr = crs.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("occurTime", Sort.ASC);
        pr.addSort(sort);
        pr.setPage(PageRequest.NO_PAGE);
        List<CommonData> list = baseService.getBeanList(pr);
        Date lastDate = null;
        for (CommonData bean : list) {
            Date dt = bean.getOccurTime();
            chartData.getXdata().add(DateUtil.getFormatDate(dt, DateUtil.FormatDay1));
            yData1.getData().add(bean.getValue());
            int days =0;
            if(lastDate!=null){
                days = DateUtil.getIntervalDays(lastDate,dt);
            }
            yData2.getData().add(days);
            lastDate = dt;
        }
        chartData.getYdata().add(yData1);
        chartData.getYdata().add(yData2);
        return callback(chartData);
    }

    /**
     * 按照日期统计
     *
     * @return
     */
    @RequestMapping(value = "/dateStat")
    public ResultBean dateStat(@Valid CommonDataDateStatSH sf) {
        switch (sf.getDateGroupType()){
            case DAYCALENDAR :
                //日历
                return callback(createChartCalendarDataDateStat(sf));
            case MINUTE :
                //值的单位
                return callback(creatBarDataDateStatByValue(sf));
            case HOURMINUTE :
                return callback(createScatterChartData(sf));
            default:
                ChartData chartData = creatBarDataDateStat(sf);
                return callback(chartData);
        }
    }

    /**
     * 散点图
     * @param sf
     * @return
     */
    private ScatterChartData createScatterChartData(CommonDataDateStatSH sf){
        CommonDataSH cdsh = new CommonDataSH();
        BeanCopy.copy(sf,cdsh);
        List<Date> dateList = commonDataService.getCommonDataDateList(cdsh);
        return ChartUtil.createHMChartData(dateList,"通用记录分析","时间点");
    }

    /**
     * 按值来区分
     *
     * @param sf
     * @return
     */
    private ChartData creatBarDataDateStatByValue(CommonDataDateStatSH sf) {
        CommonDataSH crs = new CommonDataSH();
        BeanCopy.copy(sf, crs);
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        //不分页
        pr.setPage(0);
        List<CommonData> list = baseService.getBeanList(pr);
        Map<String, Integer> map = new HashMap<>();
        for (CommonData cr : list) {
            String kv = cr.getValue().toString();
            Integer v = map.get(kv);
            if (v == null) {
                map.put(kv, 1);
            } else {
                map.put(kv, v + 1);
            }
        }
        ChartData chartData = new ChartData();
        CommonDataType type = baseService.getObject(CommonDataType.class,sf.getTypeId());
        chartData.setTitle(type.getTypeName() + "统计");
        chartData.setSubTitle(this.getDateTitle(sf));
        chartData.setLegendData(new String[]{"次数"});
        ChartYData yData1 = new ChartYData("次数","次");
        //总的值
        BigDecimal totalCount = new BigDecimal(0);
        for (String key : map.keySet()) {
            chartData.getIntXData().add(Integer.valueOf(key));
            chartData.getXdata().add(key + type.getUnit());
            yData1.getData().add(map.get(key));
            totalCount = totalCount.add(new BigDecimal(map.get(key)));
        }
        chartData.getYdata().add(yData1);
        String subTitle = this.getDateTitle(sf, totalCount.longValue() + type.getUnit());
        chartData.setSubTitle(subTitle);
        chartData = ChartUtil.completeDate(chartData, sf);
        return chartData;
    }

    /**
     * 柱状图、折线图数据统计
     *
     * @param sf
     * @return
     */
    private ChartData creatBarDataDateStat(CommonDataDateStatSH sf) {
        List<CommonDataDateStat> list = commonDataService.getCommonDataDateStat(sf);
        CommonDataType type = baseService.getObject(CommonDataType.class,sf.getTypeId());
        ChartData chartData = new ChartData();
        chartData.setTitle(type.getTypeName() + "统计");
        chartData.setSubTitle(this.getDateTitle(sf));
        chartData.setLegendData(new String[]{"时长","次数"});
        //混合图形下使用
        chartData.addYAxis("时长",type.getUnit());
        chartData.addYAxis("次数","次");
        ChartYData yData1 = new ChartYData("次数","次");
        ChartYData yData2 = new ChartYData("时长",type.getUnit());
        //总的值
        BigDecimal totalCount = new BigDecimal(0);
        for (CommonDataDateStat bean : list) {
            chartData.addXData(bean, sf.getDateGroupType());
            yData1.getData().add(bean.getTotalCount());
            yData2.getData().add(bean.getTotalValue());
            totalCount = totalCount.add(new BigDecimal(bean.getTotalCount()));
        }
        chartData.getYdata().add(yData2);
        chartData.getYdata().add(yData1);
        String subTitle = this.getDateTitle(sf, totalCount.longValue() + "次");
        chartData.setSubTitle(subTitle);
        chartData = ChartUtil.completeDate(chartData, sf);
        return chartData;
    }

    /**
     * 日历类型
     *
     * @param sf
     * @return
     */
    private ChartCalendarData createChartCalendarDataDateStat(CommonDataDateStatSH sf) {
        CommonDataType type = baseService.getObject(CommonDataType.class,sf.getTypeId());
        List<CommonDataDateStat> list = commonDataService.getCommonDataDateStat(sf);
        ChartCalendarData calandarData = ChartUtil.createChartCalendarData(type.getTypeName() + "统计", "次数", type.getUnit(), sf, list);
        calandarData.setTop(3);
        return calandarData;
    }

}
