package cn.mulanbay.pms.web.controller.fund;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Account;
import cn.mulanbay.pms.persistent.domain.ConsumeRefer;
import cn.mulanbay.pms.persistent.domain.Income;
import cn.mulanbay.pms.persistent.dto.fund.IncomeDateStat;
import cn.mulanbay.pms.persistent.dto.fund.IncomeTypeStat;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.IncomeType;
import cn.mulanbay.pms.persistent.service.IncomeService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.fund.income.IncomeDateStatSH;
import cn.mulanbay.pms.web.bean.req.fund.income.IncomeForm;
import cn.mulanbay.pms.web.bean.req.fund.income.IncomeSH;
import cn.mulanbay.pms.web.bean.req.fund.income.IncomeStatSH;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 收入
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/income")
public class IncomeController extends BaseController {

    private static Class<Income> beanClass = Income.class;

    @Autowired
    IncomeService incomeService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(IncomeSH sf) {
        return callbackDataGrid(this.getResult(sf));
    }

    private PageResult<Income> getResult(IncomeSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        pr.addSort(new Sort("occurTime", Sort.DESC));
        PageResult<Income> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid IncomeForm form) {
        Income income = new Income();
        BeanCopy.copy(form, income);
        if (form.getAccountId() != null) {
            Account account = baseService.getObject(Account.class,form.getAccountId());
            income.setAccount(account);
        }
        baseService.saveObject(income);
        return callback(income);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "incomeId") Long incomeId) {
        Income income = baseService.getObject(beanClass,incomeId);
        return callback(income);
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/getConsume", method = RequestMethod.GET)
    public ResultBean getConsume(@RequestParam(name = "incomeId") Long incomeId) {
        ConsumeRefer refer = incomeService.getConsumeRefer(incomeId);
        return callback(refer);
    }


    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid IncomeForm form) {
        Income income = baseService.getObject(beanClass,form.getIncomeId());
        BeanCopy.copy(form, income);
        if (form.getAccountId() != null) {
            Account account = baseService.getObject(Account.class,form.getAccountId());
            income.setAccount(account);
        }
        baseService.updateObject(income);
        return callback(income);
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
    public ResultBean dateStat(IncomeDateStatSH sf) {
        switch (sf.getDateGroupType()){
            case DAYCALENDAR :
                //日历
                List<IncomeDateStat> list = incomeService.statDateIncome(sf);
                ChartCalendarData calendarData = ChartUtil.createChartCalendarData("收入统计", "金额", "元", sf, list);
                calendarData.setTop(3);
                return callback(calendarData);
            case HOURMINUTE :
                //散点图
                PageRequest pr = sf.buildQuery();
                pr.setBeanClass(beanClass);
                List<Date> dateList = incomeService.getDateList(sf);
                return callback(ChartUtil.createHMChartData(dateList,"收入分析","收入时间点"));
            default:
                break;
        }
        List<IncomeDateStat> list = incomeService.statDateIncome(sf);
        ChartData chartData = new ChartData();
        chartData.setTitle(getChartTitle(sf.getAccountId(), sf.getUserId()));
        chartData.setLegendData(new String[]{"收入","次数"});
        //混合图形下使用
        chartData.addYAxis("收入","元");
        chartData.addYAxis("次数","次");
        ChartYData yData1 = new ChartYData("次数","次");
        ChartYData yData2 = new ChartYData("收入","元");
        //总的值
        BigDecimal totalCount = new BigDecimal(0);
        BigDecimal totalValue = new BigDecimal(0);
        int year = DateUtil.getYear(sf.getEndDate() == null ? new Date() : sf.getEndDate());
        for (IncomeDateStat bean : list) {
            chartData.getIntXData().add(bean.getDateIndexValue());
            if (sf.getDateGroupType() == DateGroupType.MONTH) {
                chartData.getXdata().add(bean.getDateIndexValue() + "月份");
                int days = DateUtil.getDayOfMonth(year, bean.getDateIndexValue() - 1);
            } else if (sf.getDateGroupType() == DateGroupType.YEAR) {
                chartData.getXdata().add(bean.getDateIndexValue() + "年");
            } else if (sf.getDateGroupType() == DateGroupType.WEEK) {
                chartData.getXdata().add("第" + bean.getDateIndexValue() + "周");
            } else {
                chartData.getXdata().add(bean.getDateIndexValue().toString());
            }
            yData1.getData().add(bean.getTotalCount());
            yData2.getData().add(bean.getTotalAmount().doubleValue());
            totalCount = totalCount.add(new BigDecimal(bean.getTotalCount()));
            totalValue = totalValue.add(bean.getTotalAmount());
        }
        chartData.getYdata().add(yData2);
        chartData.getYdata().add(yData1);
        String totalString = totalCount.longValue() + "(次)," + totalValue.doubleValue() + "(元)";
        chartData.setSubTitle(ChartUtil.getDateTitle(sf,totalString));
        chartData = ChartUtil.completeDate(chartData, sf);
        return callback(chartData);
    }

    /**
     * 获取统计图表的表头
     *
     * @param accountId
     * @return
     */
    private String getChartTitle(Long accountId, Long userId) {
        if (accountId == null) {
            return "账户统计";
        } else {
            Account account = baseService.getObject(Account.class, accountId);
            return "[" + account.getAccountName() + "]账户统计";
        }
    }

    /**
     * 总的概要统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(IncomeStatSH sf) {
        List<IncomeTypeStat> list = incomeService.statIncome(sf);
        ChartPieData chartPieData = this.createStatPieData(sf, list);
        return callback(chartPieData);
    }

    private ChartPieData createStatPieData(IncomeStatSH sf, List<IncomeTypeStat> list) {
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("收入分析");
        chartPieData.setUnit("元");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("分析");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        for (IncomeTypeStat bean : list) {
            IncomeType it = IncomeType.getIncomeType(bean.getIndexValue().intValue());
            chartPieData.getXdata().add(it.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(it.getName());
            dataDetail.setValue(bean.getTotalAmount());
            serieData.getData().add(dataDetail);
            totalValue = totalValue.add(bean.getTotalAmount());
        }
        String subTitle = ChartUtil.getDateTitle(sf,totalValue.intValue() + "元");
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(serieData);
        return chartPieData;
    }

}
