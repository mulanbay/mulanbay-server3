package cn.mulanbay.pms.web.controller.health;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.domain.TreatDrug;
import cn.mulanbay.pms.persistent.domain.TreatDrugDetail;
import cn.mulanbay.pms.persistent.dto.health.TreatDrugDetailStat;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.UserBehaviorType;
import cn.mulanbay.pms.persistent.service.TreatService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.health.drugDetail.*;
import cn.mulanbay.pms.web.bean.res.chart.ChartCalendarData;
import cn.mulanbay.pms.web.bean.res.chart.ChartCalendarPieData;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用药详细记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/treatDrugDetail")
public class TreatDrugDetailController extends BaseController {

    private static Class<TreatDrugDetail> beanClass = TreatDrugDetail.class;

    @Value("${mulanbay.health.drug.detailMinInv}")
    int minInterval;

    @Autowired
    TreatService treatService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(TreatDrugDetailSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("occurTime", Sort.DESC);
        pr.addSort(s);
        PageResult<TreatDrugDetail> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid TreatDrugDetailForm form) {
        TreatDrugDetail bean = new TreatDrugDetail();
        BeanCopy.copy(form, bean);
        TreatDrug treatDrug = baseService.getObject(TreatDrug.class,form.getDrugId());
        bean.setDrug(treatDrug);
        treatService.saveOrUpdateTreatDrugDetail(bean);
        return callback(bean);
    }

    /**
     * 以某天的模板创建
     *
     * @return
     */
    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public ResultBean copy(@RequestBody @Valid TreatDrugDetailCopyForm form) {
        Date temEndTime = DateUtil.tillMiddleNight(form.getTemplateDate());
        TreatDrugDetailSH sf = new TreatDrugDetailSH();
        sf.setStartDate(form.getTemplateDate());
        sf.setEndDate(temEndTime);
        sf.setUserId(form.getUserId());
        sf.setDrugId(form.getDrugId());
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("occurTime", Sort.ASC);
        pr.addSort(s);
        List<TreatDrugDetail> list = baseService.getBeanList(pr);
        if (list.isEmpty()) {
            return callbackErrorInfo("无法找到[" + DateUtil.getFormatDate(form.getTemplateDate(), DateUtil.FormatDay1) + "]的用药记录");
        } else {
            //现在和模板时间相差的天数
            int days = DateUtil.getIntervalDays(form.getBeginDate(), form.getEndDate());
            List<TreatDrugDetail> newList = new ArrayList<>();
            for(int i=0;i<days;i++){
                Date date = DateUtil.getDate(i,form.getBeginDate());
                for (TreatDrugDetail se : list) {
                    TreatDrugDetail nn = new TreatDrugDetail();
                    BeanCopy.copy(se, nn);
                    nn.setDetailId(null);
                    //拼接时分秒
                    String d = DateUtil.getFormatDate(date,DateUtil.FormatDay1);
                    String hm = DateUtil.getFormatDate(se.getOccurTime(),"HH:mm:ss");
                    nn.setOccurTime(DateUtil.getDate(d+" "+hm,DateUtil.Format24Datetime));
                    nn.setRemark("以模板新增");
                    newList.add(nn);
                }
            }
            treatService.saveDrugDetailList(newList);
        }
        return callback(null);
    }

    private void checkTreatDrugDetail(TreatDrugDetail bean) {
        Date startTime = new Date(bean.getOccurTime().getTime()-60*1000L*minInterval);
        Date endTime = new Date(bean.getOccurTime().getTime()+60*1000L*minInterval);
        long n = treatService.getDetailCount(startTime,endTime,bean.getUserId(),bean.getDrug().getDrugId(),bean.getDetailId());
        if (n>0) {
            String msg = "在此用药时间段的"+minInterval+"分钟间隔内，一共还有"+n+"个用药记录，重复输入.";
            throw new ApplicationException(PmsCode.TREAT_DRUG_DETAIL_OCCUR_TIME_INCORRECT,msg);
        }
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@Valid @RequestParam(name = "detailId") Long detailId) {
        TreatDrugDetail bean = baseService.getObject(beanClass,detailId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid TreatDrugDetailForm form) {
        TreatDrugDetail bean = new TreatDrugDetail();
        BeanCopy.copy(form, bean);
        TreatDrug treatDrug = baseService.getObject(TreatDrug.class,form.getDrugId());
        bean.setDrug(treatDrug);
        checkTreatDrugDetail(bean);
        treatService.saveOrUpdateTreatDrugDetail(bean);
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
     * 时间点
     *
     * @return
     */
    @RequestMapping(value = "/timeStat", method = RequestMethod.GET)
    public ResultBean timeStat(TreatDrugDetailTimeStatSH sf){
        List<Date> dateList = treatService.getDrugDetailDateList(sf.getDrugId(),sf.getStartDate(),sf.getEndDate(),sf.getUserId(),sf.isMergeSameName());
        return callback(ChartUtil.createHMChartData(dateList,"用药分析","用药时间点"));
    }

    /**
     * 日历统计
     *
     * @return
     */
    @RequestMapping(value = "/calendarStat", method = RequestMethod.GET)
    public ResultBean calendarStat(TreatDrugDetailCalendarStatSH sf) {
        TreatDrug treatDrug = baseService.getObject(TreatDrug.class, sf.getDrugId());
        TreatDrugDetailSH search = new TreatDrugDetailSH();
        if (sf.getDateGroupType() == null || sf.getDateGroupType() == DateGroupType.YEAR) {
            Date startDate = DateUtil.getDate(sf.getYear() + "-01-01 00:00:00", DateUtil.Format24Datetime);
            Date endDate = DateUtil.getDate(sf.getYear() + "-12-31 23:59:59", DateUtil.Format24Datetime);
            search.setStartDate(startDate);
            search.setEndDate(endDate);
        } else {
            Date date = DateUtil.getDate(sf.getYear() + "-" + sf.getMonth() + "-01", DateUtil.FormatDay1);
            Date startDate = DateUtil.getMonthFirst(date);
            String ed = DateUtil.getFormatDate(DateUtil.getMonthLast(date), DateUtil.FormatDay1);
            Date endDate = DateUtil.getDate(ed + " 23:59:59", DateUtil.Format24Datetime);
            search.setStartDate(startDate);
            search.setEndDate(endDate);
        }
        search.setUserId(sf.getUserId());

        List<TreatDrugDetail> list = treatService.getDrugDetailCalendarStatList(search,sf.getDrugId(),sf.isMergeSameName());
        if (sf.getDateGroupType() == null || sf.getDateGroupType() == DateGroupType.YEAR) {
            return callback(this.createYearCalendarData(list, sf, treatDrug));
        } else {
            return callback(this.createMonthCalendarData(list, sf.getYear(), sf.getMonth(), treatDrug));
        }
    }

    private ChartCalendarPieData createMonthCalendarData(List<TreatDrugDetail> list, int year, String month, TreatDrug treatDrug) {
        ChartCalendarPieData pieData = new ChartCalendarPieData(UserBehaviorType.HEALTH);
        pieData.setTitle(year + "-" + month + "用药分析");
        pieData.setStartDate(DateUtil.getDate(year + "-" + month + "-01", DateUtil.FormatDay1));

        for (TreatDrugDetail stat : list) {
            String name = "";
            if (treatDrug.getPerDay() > 1) {
                name = "吃药";
            } else {
                int perTimes = treatDrug.getPerTimes();
                if (perTimes == 1) {
                    name = "吃药";
                } else {
                    //获取小时数
                    int occurHours = Integer.valueOf(DateUtil.getFormatDate(stat.getOccurTime(), "HH"));
                    if (perTimes == 2) {
                        if (occurHours < 12) {
                            name = "早";
                        } else {
                            name = "晚";
                        }
                    } else if (perTimes == 3) {
                        if (occurHours <= 9) {
                            name = "早";
                        } else if (occurHours < 15) {
                            name = "中";
                        } else {
                            name = "晚";
                        }
                    } else {
                        name = "吃药点" + occurHours / 4;
                    }
                }

            }
            pieData.addData(DateUtil.getFormatDate(stat.getOccurTime(), DateUtil.FormatDay1), name, 1, false, 1, true);
        }
        return pieData;
    }

    /**
     * 获取年的统计
     *
     * @param list
     * @param sf
     * @param treatDrug
     * @return
     */
    private ChartCalendarData createYearCalendarData(List<TreatDrugDetail> list, TreatDrugDetailCalendarStatSH sf, TreatDrug treatDrug) {
        ChartCalendarData chartData = new ChartCalendarData();
        if (treatDrug.getPerDay() == 1) {
            //只有每天用的才有用
            chartData.setCustomData(treatDrug.getPerTimes());
        }
        chartData.setTitle(sf.getYear() + "年用药分析");
        chartData.setCount(list.size());
        chartData.setYear(sf.getYear());
        chartData.setLegendData("用药", 3);
        chartData.setUnit("次");
        chartData.setCount(list.size());
        for (TreatDrugDetail stat : list) {
            //这时value就是天数值
            chartData.addSerieData(stat.getOccurTime(), 1);
        }
        return chartData;
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(TreatDrugDetailStatSH sf) {
        List<TreatDrugDetailStat> list = treatService.getDrugDetailStat(sf);
        return callback(list);
    }
}
