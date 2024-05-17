package cn.mulanbay.pms.web.controller.health;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.BodyAbnormal;
import cn.mulanbay.pms.persistent.dto.body.BodyAbnormalDateStat;
import cn.mulanbay.pms.persistent.dto.body.BodyAbnormalStat;
import cn.mulanbay.pms.persistent.dto.body.BodyInfoAvgStat;
import cn.mulanbay.pms.persistent.dto.health.TreatAnalyseDetailStat;
import cn.mulanbay.pms.persistent.enums.BodyAbnormalGroupType;
import cn.mulanbay.pms.persistent.enums.ChartType;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.service.BodyService;
import cn.mulanbay.pms.persistent.service.TreatService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.health.body.*;
import cn.mulanbay.pms.web.bean.req.health.treat.TreatAnalyseDetailStatSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.body.BodyAbnormalAnalyseVo;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 身体不适记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/bodyAbnormal")
public class BodyAbnormalController extends BaseController {

    private static Class<BodyAbnormal> beanClass = BodyAbnormal.class;

    @Autowired
    BodyService bodyService;

    @Autowired
    TreatService treatService;

    /**
     * 获取症状或者器官的各种分类归类
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(BodyAbnormalCateSH sf) {

        try {
            List<String> categoryList = bodyService.getAbnormalCateList(sf);
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
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取症状或者器官的各种分类归类异常",
                    e);
        }
    }

    /**
     * 获取列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(BodyAbnormalSH sf) {
        return callbackDataGrid(getResult(sf));
    }

    private PageResult<BodyAbnormal> getResult(BodyAbnormalSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("occurDate", Sort.DESC);
        pr.addSort(sort);
        PageResult<BodyAbnormal> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid BodyAbnormalForm form) {
        BodyAbnormal bean = new BodyAbnormal();
        BeanCopy.copy(form, bean);
        calAndSetLastDays(bean);
        bean.setCreatedTime(new Date());
        baseService.saveObject(bean);
        return callback(bean);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "id") Long id) {
        BodyAbnormal bean = baseService.getObject(beanClass,id);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid BodyAbnormalForm form) {
        BodyAbnormal bean = baseService.getObject(beanClass,form.getId());
        BeanCopy.copy(form, bean);
        calAndSetLastDays(bean);
        baseService.updateObject(bean);
        return callback(bean);
    }

    /**
     * 计算持续时间
     * @param bean
     */
    private void calAndSetLastDays(BodyAbnormal bean) {
        if(bean.getDays()==null){
            int days = DateUtil.getIntervalDays(bean.getOccurDate(), bean.getFinishDate());
            bean.setDays(days + 1);
        }
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
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(@Valid BodyAbnormalStatSH sf) {
        //通过年份设置开始结束日期
        List<BodyAbnormalStat> list = bodyService.getAbnormalStat(sf);
        BaseChartData data = null;
        if (!list.isEmpty()) {
            if (sf.getChartType() == ChartType.BAR) {
                data = this.createStatBarData(list);
            } else {
                data = this.createStatPieData(list, sf);
            }
        }
        return callback(data);
    }

    /**
     * 封装日统计的饼状图数据
     *
     * @param list
     * @return
     */
    private ChartPieData createStatPieData(List<BodyAbnormalStat> list, BodyAbnormalStatSH sf) {
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("身体不适统计");
        chartPieData.setUnit("次");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("次数");
        //总的值
        BigDecimal totalCount = new BigDecimal(0);
        for (BodyAbnormalStat bean : list) {
            chartPieData.getXdata().add(bean.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(bean.getName());
            dataDetail.setValue(bean.getTotalCount());
            serieData.getData().add(dataDetail);
            totalCount = totalCount.add(new BigDecimal(bean.getTotalCount()));
        }
        chartPieData.getDetailData().add(serieData);
        String subTitle = ChartUtil.getDateTitle(sf, totalCount.longValue() + "次");
        chartPieData.setSubTitle(subTitle);
        return chartPieData;
    }

    /**
     * 统计柱状图数据
     *
     * @param list
     * @return
     */
    private ChartData createStatBarData(List<BodyAbnormalStat> list) {
        ChartData chartData = new ChartData();
        chartData.setTitle("身体不适统计");
        chartData.setUnit("次");
        chartData.setLegendData(new String[]{"次数"});
        ChartYData yData = new ChartYData("次数","次");
        //总的值
        BigDecimal totalCount = new BigDecimal(0);
        for (BodyAbnormalStat bean : list) {
            chartData.getXdata().add(bean.getName());
            yData.getData().add(bean.getTotalCount());
            totalCount = totalCount.add(new BigDecimal(bean.getTotalCount()));
        }
        chartData.getYdata().add(yData);
        String subTitle = totalCount.longValue() + "次";
        chartData.setSubTitle(subTitle);
        return chartData;
    }

    /**
     * 按照日期统计
     *
     * @return
     */
    @RequestMapping(value = "/dateStat")
    public ResultBean dateStat(@Valid BodyAbnormalDateStatSH sf) {
        if (sf.getDateGroupType() == DateGroupType.DAYCALENDAR) {
            return callback(createChartCalendarDataDateStat(sf));
        }else{
            return callback(createDateStatChartData(sf));
        }

    }

    private ChartData createDateStatChartData(BodyAbnormalDateStatSH sf){
        List<BodyAbnormalDateStat> list = bodyService.getAbnormalDateStat(sf);
        ChartData chartData = new ChartData();
        chartData.setTitle("身体不适统计");
        chartData.setSubTitle(ChartUtil.getDateTitle(sf));
        chartData.setLegendData(new String[]{"持续天数","次数"});
        //混合图形下使用
        chartData.addYAxis("持续天数","天");
        chartData.addYAxis("次数","次");
        ChartYData yData1 = new ChartYData("次数","次");
        ChartYData yData2 = new ChartYData("持续天数","天");
        //总的值
        BigDecimal totalCount = new BigDecimal(0);
        for (BodyAbnormalDateStat bean : list) {
            chartData.addXData(bean, sf.getDateGroupType());
            yData1.getData().add(bean.getTotalCount());
            yData2.getData().add(bean.getTotalDays());
            totalCount = totalCount.add(new BigDecimal(bean.getTotalCount()));
        }
        chartData.getYdata().add(yData2);
        chartData.getYdata().add(yData1);
        String subTitle = ChartUtil.getDateTitle(sf, totalCount.longValue() + "次");
        chartData.setSubTitle(subTitle);
        chartData = ChartUtil.completeDate(chartData, sf);
        return chartData;
    }

    private ChartCalendarData createChartCalendarDataDateStat(BodyAbnormalDateStatSH sf) {
        List<BodyAbnormalDateStat> list = bodyService.getAbnormalDateStat(sf);
        ChartCalendarData calendarData = ChartUtil.createChartCalendarData("身体不适统计", "持续天数", "天", sf, list);
        if (!StringUtil.isEmpty(sf.getDisease())) {
            BodyAbnormalByDiseaseSH bards = new BodyAbnormalByDiseaseSH();
            BeanCopy.copy(sf, bards);
            PageRequest pr = bards.buildQuery();
            pr.setBeanClass(beanClass);
            List<BodyAbnormal> dd = baseService.getBeanList(pr);
            calendarData.setGraphName("["+sf.getDisease()+"]追踪");
            for (BodyAbnormal bar : dd) {
                calendarData.addGraph(bar.getOccurDate(), bar.getDays());
            }
        } else {
            calendarData.setTop(3);
        }
        return calendarData;
    }

    /**
     * 分析
     *
     * @return
     */
    @RequestMapping(value = "/analyse", method = RequestMethod.GET)
    public ResultBean analyse(@Valid BodyAbnormalStatSH sf) {
        //通过年份设置开始结束日期
        List<BodyAbnormalAnalyseVo> responseList = new ArrayList<>();
        List<BodyAbnormalStat> list = bodyService.getAbnormalStat(sf);
        BodyInfoDateStatSH bbidss = new BodyInfoDateStatSH();
        bbidss.setStartDate(sf.getStartDate());
        bbidss.setEndDate(sf.getEndDate());
        bbidss.setUserId(sf.getUserId());
        TreatAnalyseDetailStatSH tradss = new TreatAnalyseDetailStatSH();
        BeanCopy.copy(sf, tradss);
        BodyInfoAvgStat statAvg = bodyService.getInfoAvgStat(bbidss);
        long id = 1;
        for (BodyAbnormalStat stat : list) {
            BodyAbnormalAnalyseVo vo = new BodyAbnormalAnalyseVo();
            BeanCopy.copy(stat, vo);
            if (sf.getGroupField() == BodyAbnormalGroupType.ORGAN) {
                //根据不同类型选择不同的查询方式
                tradss.setOrgan(stat.getName());
            } else {
                tradss.setDisease(stat.getName());
            }
            TreatAnalyseDetailStat ss = treatService.getAnalyseDetailStat(tradss);
            vo.setTreatStat(ss);
            //获取个人基本信息
            vo.setAvgHeight(statAvg.getAvgHeight());
            vo.setAvgWeight(statAvg.getAvgWeight());
            vo.setId(id);
            responseList.add(vo);
            id++;
        }
        PageResult<BodyAbnormalAnalyseVo> result = new PageResult();
        result.setBeanList(responseList);
        result.setMaxRow(responseList.size());
        return callbackDataGrid(result);
    }

}
