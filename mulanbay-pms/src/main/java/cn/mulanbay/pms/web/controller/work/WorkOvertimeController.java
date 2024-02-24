package cn.mulanbay.pms.web.controller.work;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import cn.mulanbay.pms.persistent.domain.Company;
import cn.mulanbay.pms.persistent.domain.WorkOvertime;
import cn.mulanbay.pms.persistent.dto.work.WorkOvertimeDateStat;
import cn.mulanbay.pms.persistent.dto.work.WorkOvertimeStat;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.service.WorkService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.work.overtime.WorkOvertimeDateStatSH;
import cn.mulanbay.pms.web.bean.req.work.overtime.WorkOvertimeForm;
import cn.mulanbay.pms.web.bean.req.work.overtime.WorkOvertimeSH;
import cn.mulanbay.pms.web.bean.req.work.overtime.WorkOvertimeStatSH;
import cn.mulanbay.pms.web.bean.res.chart.ChartCalendarData;
import cn.mulanbay.pms.web.bean.res.chart.ChartData;
import cn.mulanbay.pms.web.bean.res.chart.ChartYData;
import cn.mulanbay.pms.web.bean.res.chart.ScatterChartData;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static cn.mulanbay.pms.common.Constant.*;

/**
 * 加班记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/workOvertime")
public class WorkOvertimeController extends BaseController {

    private static Class<WorkOvertime> beanClass = WorkOvertime.class;

    @Autowired
    WorkService workService;

    @Autowired
    SystemConfigHandler systemConfigHandler;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(WorkOvertimeSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("workDate", Sort.DESC);
        pr.addSort(s);
        PageResult<WorkOvertime> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid WorkOvertimeForm form) {
        WorkOvertime bean = new WorkOvertime();
        BeanCopy.copy(form, bean);
        Company company = baseService.getObject(Company.class,form.getCompanyId());
        bean.setCompany(company);
        setWorkTime(bean);
        baseService.saveObject(bean);
        return callback(bean);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "overtimeId") Long overtimeId) {
        WorkOvertime bean = baseService.getObject(beanClass,overtimeId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid WorkOvertimeForm form) {
        WorkOvertime bean = baseService.getObject(beanClass,form.getOvertimeId());
        BeanCopy.copy(form, bean);
        Company company = baseService.getObject(Company.class,form.getCompanyId());
        bean.setCompany(company);
        setWorkTime(bean);
        baseService.updateObject(bean);
        return callback(bean);
    }

    private void setWorkTime(WorkOvertime bean) {
        if (bean.getWorkDate() == null) {
            bean.setWorkDate(DateUtil.getDate(bean.getStartTime(), DateUtil.FormatDay1));
        }
        if (bean.getHours() == null) {
            long seconds = (bean.getEndTime().getTime() - bean.getStartTime().getTime()) / 1000;
            double hours = seconds / 3600.0;
            bean.setHours(hours);
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
     * 按照日期统计
     *
     * @return
     */
    @RequestMapping(value = "/dateStat")
    public ResultBean dateStat(WorkOvertimeDateStatSH sf) {
        switch (sf.getDateGroupType()){
            case DAYCALENDAR :
                return callback(createChartCalendarData(sf));
            case HOURMINUTE :
                return callback(createScatterChartData(sf));
            default:
                return callback(createChartData(sf));
        }
    }

    private ChartData createChartData(WorkOvertimeDateStatSH sf){
        ChartData chartData = new ChartData();
        chartData.setTitle("加班统计");
        chartData.setSubTitle(this.getDateTitle(sf));
        chartData.setLegendData(new String[]{ "总时长", "平均每天加班","次数"});
        //混合图形下使用
        chartData.addYAxis("时长","小时");
        chartData.addYAxis("次数","次");
        ChartYData yData1 = new ChartYData("次数","次");
        ChartYData yData2 = new ChartYData("总时长","小时");
        ChartYData yData3 = new ChartYData("平均每天加班","小时");
        double monthWorkDays = MONTH_WORK_DAY;
        double weekWorkDays = WEEK_WORK_DAY;
        double yearWorkDays = YEAR_WORK_DAY;
        List<WorkOvertimeDateStat> list = workService.getWorkOvertimeDateStat(sf);
        for (WorkOvertimeDateStat bean : list) {
            chartData.getIntXData().add(bean.getDateIndexValue());
            if (sf.getDateGroupType() == DateGroupType.MONTH) {
                chartData.getXdata().add(bean.getDateIndexValue() + "月份");
                yData3.getData().add(NumberUtil.getAvg(bean.getTotalHours(), monthWorkDays,1));
            } else if (sf.getDateGroupType() == DateGroupType.YEAR) {
                chartData.getXdata().add(bean.getDateIndexValue() + "年");
                yData3.getData().add(NumberUtil.getAvg(bean.getTotalHours(), yearWorkDays,1));
            } else if (sf.getDateGroupType() == DateGroupType.WEEK) {
                chartData.getXdata().add("第" + bean.getDateIndexValue() + "周");
                yData3.getData().add(NumberUtil.getAvg(bean.getTotalHours(), weekWorkDays,1));
            } else {
                chartData.getXdata().add(bean.getDateIndexValue().toString());
                yData3.getData().add(NumberUtil.getAvg(bean.getTotalHours(), 1.0,1));
            }
            yData1.getData().add(bean.getTotalCount());
            yData2.getData().add(bean.getTotalHours().doubleValue());
        }
        chartData.getYdata().add(yData2);
        chartData.getYdata().add(yData3);

        chartData.getYdata().add(yData1);
        chartData = ChartUtil.completeDate(chartData, sf);
        return chartData;
    }

    /**
     * 时间点
     *
     * @param sf
     * @return
     */
    private ScatterChartData createScatterChartData(WorkOvertimeDateStatSH sf){
        List<Date> dateList = workService.getWorkOvertimeDateList(sf);
        return ChartUtil.createHMChartData(dateList,"加班分析","加班时间点");
    }

    /**
     * 日历
     * @param sf
     * @return
     */
    private ChartCalendarData createChartCalendarData(WorkOvertimeDateStatSH sf){
        //日历
        List<WorkOvertimeDateStat> list = workService.getWorkOvertimeDateStat(sf);
        return ChartUtil.createChartCalendarData("加班统计", "次数", "次", sf, list);
    }


    /**
     * 总的概要统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(WorkOvertimeStatSH sf) {
        WorkOvertimeStat data = workService.getWorkOvertimeStat(sf);
        return callback(data);
    }
}
