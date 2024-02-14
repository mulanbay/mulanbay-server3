package cn.mulanbay.pms.web.controller.health;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Sleep;
import cn.mulanbay.pms.persistent.dto.sleep.SleepAnalyseStat;
import cn.mulanbay.pms.persistent.enums.ChartType;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.SleepStatType;
import cn.mulanbay.pms.persistent.service.SleepService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.health.sleep.SleepAnalyseStatSH;
import cn.mulanbay.pms.web.bean.req.health.sleep.SleepForm;
import cn.mulanbay.pms.web.bean.req.health.sleep.SleepSH;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.bean.res.health.SleepAnalyseStatVo;
import cn.mulanbay.pms.web.bean.res.health.SleepPieChartStatVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 睡眠
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/sleep")
public class SleepController extends BaseController {

    private static Class<Sleep> beanClass = Sleep.class;

    @Autowired
    SleepService sleepService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(SleepSH sf) {
        return callbackDataGrid(getResult(sf));
    }

    private PageResult<Sleep> getResult(SleepSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("sleepDate", Sort.DESC);
        pr.addSort(sort);
        PageResult<Sleep> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid SleepForm form) {
        Sleep bean = new Sleep();
        BeanCopy.copy(form, bean);
        Date sleepDate = calSleepDate(bean.getSleepTime());
        checkSleepDate(sleepDate);
        bean.setSleepDate(sleepDate);
        if (bean.getSleepTime() != null && bean.getGetUpTime() != null) {
            long n = bean.getGetUpTime().getTime() - bean.getSleepTime().getTime();
            bean.setDuration((int) (n / (1000 * 60)));
        }
        bean.setCreatedTime(new Date());
        baseService.saveObject(bean);
        return callback(bean);
    }

    private void checkSleepDate(Date sleepDate) {
        Sleep sleep = sleepService.getSleep(sleepDate);
        if (sleep != null) {
            throw new ApplicationException(ErrorCode.DO_BUSS_ERROR,
                    "睡眠日[" + DateUtil.getFormatDate(sleepDate, DateUtil.FormatDay1) + "]已经存在");
        }
    }

    /**
     * 计算睡眠日
     *
     * @param date
     */
    private Date calSleepDate(Date date) {
        if (date == null) {
            return null;
        } else {
            String hour = DateUtil.getFormatDate(date, "HH");
            int n = Integer.valueOf(hour);
            if (n >= 12) {
                //当天
                return DateUtil.getDate(0, date);
            } else {
                //昨天
                return DateUtil.getDate(-1, date);
            }
        }
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "sleepId") Long sleepId) {
        Sleep bean = baseService.getObject(beanClass,sleepId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid SleepForm form) {
        Sleep bean = baseService.getObject(beanClass,form.getSleepId());
        BeanCopy.copy(form, bean);
        Date sleepDate = calSleepDate(bean.getSleepTime());
        bean.setSleepDate(sleepDate);
        if (bean.getSleepTime() != null && bean.getGetUpTime() != null) {
            long n = bean.getGetUpTime().getTime() - bean.getSleepTime().getTime();
            bean.setDuration((int) (n / (1000 * 60)));
        }
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
     * 比对，采用散点图
     *
     * @return
     */
    @RequestMapping(value = "/analyseStat")
    public ResultBean analyseStat(@Valid SleepAnalyseStatSH sf) {
        List<SleepAnalyseStatVo> statVoList = new ArrayList<>();
        List<SleepAnalyseStat> list = sleepService.getAnalyseSta(sf);
        DateGroupType dateGroupType = sf.getGroupType();
        for (SleepAnalyseStat stat : list) {
            if(stat.getValue()==null){
                continue;
            }
            double x = stat.getGroupValue();
            SleepAnalyseStatVo vo = new SleepAnalyseStatVo();
            if(dateGroupType == DateGroupType.DAYCALENDAR){
                Date dd = DateUtil.getDate(stat.getGroup().toString(),"yyyyMMdd");
                x = DateUtil.getDayOfYear(dd);
            }
            vo.setGroupValue(x);
            if (sf.getValueType() == SleepStatType.DURATION) {
                double hours = stat.getVE() / 60;
                BigDecimal b = new BigDecimal(hours);
                double v = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                vo.setValue(v);
            } else {
                vo.setValue(stat.getVE());
            }
            statVoList.add(vo);
        }
        if(sf.getChartType()== ChartType.SCATTER){
            return callback(this.createAnalyseStatScatterData(statVoList,sf));
        }else{
            return callback(this.createAnalyseStatPieData(statVoList,sf));
        }
    }

    /**
     * 散点图
     * @param statVoList
     * @param sf
     * @return
     */
    private ScatterChartData createAnalyseStatScatterData(List<SleepAnalyseStatVo> statVoList, SleepAnalyseStatSH sf){
        ScatterChartData chartData = new ScatterChartData();
        chartData.setTitle("睡眠分析");
        chartData.setxUnit(sf.getGroupType().getName());
        chartData.setyUnit(sf.getValueType().getUnit());
        chartData.addLegent(sf.getValueType().getName());
        ScatterChartDetailData detailData = new ScatterChartDetailData();
        detailData.setName(sf.getValueType().getName());
        double totalX = 0;
        int n = 0;
        for (SleepAnalyseStatVo stat : statVoList) {
            detailData.addData(new Object[]{stat.getGroupValue(), stat.getValue()});
            totalX += stat.getGroupValue();
            n++;
        }
        detailData.setxAxisAverage(totalX / n);
        chartData.addSeriesData(detailData);
        return chartData;
    }

    /**
     * 封装饼状图数据
     *
     * @param statVoList
     * @param sf
     * @return
     */
    private ChartPieData createAnalyseStatPieData(List<SleepAnalyseStatVo> statVoList, SleepAnalyseStatSH sf) {
        SleepStatType statType = sf.getValueType();
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("睡眠分析");
        chartPieData.setUnit("次");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName(statType.getName());
        //Step 1:统计数据
        Map<String, SleepPieChartStatVo> statMap =new HashMap<>();
        for(SleepAnalyseStatVo vo : statVoList){
            int v = (int) Math.round(vo.getValue());
            String key = v+"-"+(v+1);
            SleepPieChartStatVo pv = statMap.get(key);
            if(pv==null){
                pv = new SleepPieChartStatVo();
                pv.setCount(1);
                if(statType==SleepStatType.SLEEP_TIME||statType==SleepStatType.GETUP_TIME){
                    if(v<10){
                        pv.setName("0"+v+":00~"+"0"+v+":59");
                    }else{
                        pv.setName(v+":00~"+v+":59");
                    }
                }else if(statType == SleepStatType.DURATION){
                    pv.setName("["+v+"~"+(v+1)+")小时");
                }else if(statType == SleepStatType.QUALITY){
                    pv.setName("["+v+"~"+(v+1)+")分");
                }else if(statType == SleepStatType.WAKEUP_COUNT){
                    pv.setName("["+v+"~"+(v+1)+")次");
                }
                statMap.put(key,pv);
            }else{
                pv.setCount(pv.getCount()+1);
            }
        }
        //Step 2:封装图表对象
        for (SleepPieChartStatVo bean : statMap.values()) {
            chartPieData.getXdata().add(bean.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(bean.getName());
            dataDetail.setValue(bean.getCount());
            serieData.getData().add(dataDetail);
        }
        chartPieData.getDetailData().add(serieData);
        return chartPieData;
    }

}
