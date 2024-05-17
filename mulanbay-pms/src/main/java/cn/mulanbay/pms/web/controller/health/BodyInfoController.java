package cn.mulanbay.pms.web.controller.health;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.BodyInfo;
import cn.mulanbay.pms.persistent.dto.body.BodyInfoDateStat;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.service.BodyService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.pms.web.bean.req.health.body.BodyInfoDateStatSH;
import cn.mulanbay.pms.web.bean.req.health.body.BodyInfoForm;
import cn.mulanbay.pms.web.bean.req.health.body.BodyInfoSH;
import cn.mulanbay.pms.web.bean.req.health.body.BodyInfoYoyStatSH;
import cn.mulanbay.pms.web.bean.res.chart.ChartData;
import cn.mulanbay.pms.web.bean.res.chart.ChartYData;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 身体基本情况
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/bodyInfo")
public class BodyInfoController extends BaseController {

    private static Class<BodyInfo> beanClass = BodyInfo.class;

    @Autowired
    BodyService bodyService;

    /**
     * 获取列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(BodyInfoSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("recordTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<BodyInfo> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid BodyInfoForm form) {
        BodyInfo bean = new BodyInfo();
        BeanCopy.copy(form, bean);
        bean.setBmi(caleBmi(form));
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "id") Long id) {
        BodyInfo bean = baseService.getObject(beanClass,id);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid BodyInfoForm form) {
        BodyInfo bean = baseService.getObject(beanClass,form.getId());
        BeanCopy.copy(form, bean);
        bean.setBmi(caleBmi(form));
        baseService.updateObject(bean);
        return callback(null);
    }

    /**
     * 计算BMI=体重（千克数） / 身高的平方（米数）
     *
     * @param form
     * @return
     */
    private double caleBmi(BodyInfoForm form) {
        double h = form.getHeight() / 100.0;
        double w = form.getWeight();
        double b = w / (h * h);
        BigDecimal bmi = new BigDecimal(b);
        return bmi.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
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
     * 基于日期的统计
     * 界面上使用echarts展示图表，后端返回的是核心模块的数据，不再使用Echarts的第三方jar包封装（比较麻烦）
     *
     * @return
     */
    @RequestMapping(value = "/dateStat", method = RequestMethod.GET)
    public ResultBean dateStat(@Valid BodyInfoDateStatSH sf) {
        List<BodyInfoDateStat> list = bodyService.getInfoDateStat(sf);
        ChartData chartData = new ChartData();
        chartData.setTitle("身体基本情况统计");
        chartData.setLegendData(new String[]{"身高", "体重", "BMI"});
        //混合图形下使用
        chartData.addYAxis("身高/体重","");
        chartData.addYAxis("BMI指数","");
        ChartYData yData1 = new ChartYData("身高","cm");
        ChartYData yData2 = new ChartYData("体重","kg");
        ChartYData yData3 = new ChartYData("BMI","kg/m2");
        for (BodyInfoDateStat bean : list) {
            chartData.addXData(bean, sf.getDateGroupType());
            yData1.getData().add(NumberUtil.getAvg(bean.getTotalHeight().doubleValue(), bean.getTotalCount().intValue(), 0));
            yData2.getData().add(NumberUtil.getAvg(bean.getTotalWeight().doubleValue(), bean.getTotalCount().intValue(), 1));
            yData3.getData().add(NumberUtil.getAvg(bean.getTotalBmi().doubleValue(), bean.getTotalCount().intValue(), 1));
        }
        chartData.getYdata().add(yData1);
        chartData.getYdata().add(yData2);
        chartData.getYdata().add(yData3);
        chartData = ChartUtil.completeDate(chartData, sf);
        return callback(chartData);
    }

    /**
     * 按照日期统计同期对比
     *
     * @return
     */
    @RequestMapping(value = "/yoyStat")
    public ResultBean yoyStat(@Valid BodyInfoYoyStatSH sf) {
        GroupType groupType = sf.getGroupType();
        String unit = null;
        if (groupType == GroupType.COUNT) {
            unit= "次";
        } else if (groupType == GroupType.WEIGHT) {
            unit= "kg";
        } else if (groupType == GroupType.HEIGHT) {
            unit= "cm";
        } else if (groupType == GroupType.BMI) {
            unit= "kg/m2";
        }
        ChartData chartData = ChartUtil.initYoyCharData(sf, "身体基本情况同期对比", null);
        String[] legendData = new String[sf.getYears().size()];
        for (int i = 0; i < sf.getYears().size(); i++) {
            legendData[i] = sf.getYears().get(i).toString();
            //数据,为了代码复用及统一，统计还是按照日期的统计
            BodyInfoDateStatSH dateSearch = new BodyInfoDateStatSH();
            dateSearch.setDateGroupType(sf.getDateGroupType());
            dateSearch.setStartDate(DateUtil.getDate(sf.getYears().get(i) + "-01-01", DateUtil.FormatDay1));
            dateSearch.setEndDate(DateUtil.getDate(sf.getYears().get(i) + "-12-31", DateUtil.FormatDay1));
            dateSearch.setUserId(sf.getUserId());
            ChartYData yData = new ChartYData(sf.getYears().get(i).toString(),unit);
            List<BodyInfoDateStat> list = bodyService.getInfoDateStat(dateSearch);
            //临时内容，作为补全用
            ChartData temp = new ChartData();
            for (BodyInfoDateStat bean : list) {
                temp.getIntXData().add(bean.getDateIndexValue());
                if (sf.getDateGroupType() == DateGroupType.MONTH) {
                    temp.getXdata().add(bean.getDateIndexValue() + "月份");
                } else if (sf.getDateGroupType() == DateGroupType.WEEK) {
                    temp.getXdata().add("第" + bean.getDateIndexValue() + "周");
                } else {
                    temp.getXdata().add(bean.getDateIndexValue().toString());
                }
                if (groupType == GroupType.COUNT) {
                    yData.getData().add(bean.getTotalCount());
                } else if (groupType == GroupType.WEIGHT) {
                    yData.getData().add(NumberUtil.getAvg(bean.getTotalWeight().doubleValue(), bean.getTotalCount().intValue(), 1));
                } else if (groupType == GroupType.HEIGHT) {
                    yData.getData().add(NumberUtil.getAvg(bean.getTotalHeight().doubleValue(), bean.getTotalCount().intValue(), 1));
                } else if (groupType == GroupType.BMI) {
                    yData.getData().add(NumberUtil.getAvg(bean.getTotalBmi().doubleValue(), bean.getTotalCount().intValue(), 1));
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
        chartData.setUnit(sf.getGroupType().getUnit());

        return callback(chartData);
    }


}
