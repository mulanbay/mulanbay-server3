package cn.mulanbay.pms.web.controller.life;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Company;
import cn.mulanbay.pms.persistent.domain.Experience;
import cn.mulanbay.pms.persistent.domain.ExperienceSum;
import cn.mulanbay.pms.persistent.enums.ExperienceType;
import cn.mulanbay.pms.persistent.service.CompanyService;
import cn.mulanbay.pms.persistent.service.ExperienceService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.life.sum.ExperienceSumAnalyseSH;
import cn.mulanbay.pms.web.bean.req.life.sum.ExperienceSumForm;
import cn.mulanbay.pms.web.bean.req.life.sum.ExperienceSumReviseForm;
import cn.mulanbay.pms.web.bean.req.life.sum.ExperienceSumSH;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人生经历汇总
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/experienceSum")
public class ExperienceSumController extends BaseController {

    private static Class<ExperienceSum> beanClass = ExperienceSum.class;

    @Autowired
    ExperienceService experienceService;

    @Autowired
    CompanyService companyService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(ExperienceSumSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("year", Sort.DESC);
        pr.addSort(sort);
        PageResult<ExperienceSum> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid ExperienceSumForm form) {
        ExperienceSum bean = new ExperienceSum();
        BeanCopy.copy(form, bean);
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "sumId") Long sumId) {
        ExperienceSum bean = baseService.getObject(beanClass,sumId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid ExperienceSumForm form) {
        ExperienceSum bean = baseService.getObject(beanClass,form.getSumId());
        BeanCopy.copy(form, bean);
        baseService.updateObject(bean);
        return callback(null);
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
     * 统计分析
     *
     * @return
     */
    @RequestMapping(value = "/analyse")
    public ResultBean analyse(ExperienceSumAnalyseSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("year", Sort.ASC);
        pr.addSort(sort);
        PageResult<ExperienceSum> qr = baseService.getBeanResult(pr);
        String title = "人生经历汇总分析";
        ChartShadowData chartShadowData = new ChartShadowData();
        chartShadowData.setUnit("天");
        chartShadowData.setTitle(title);
        chartShadowData.addLegend("学习");
        chartShadowData.addLegend("旅行");
        chartShadowData.addLegend("工作");
        chartShadowData.addLegend("休息");
        ChartShadowSerieData studySerie = new ChartShadowSerieData("学习", "天", "总量");
        ChartShadowSerieData travelSerie = new ChartShadowSerieData("旅行", "天", "总量");
        ChartShadowSerieData workSerie = new ChartShadowSerieData("工作", "天", "总量");
        ChartShadowSerieData restSerie = new ChartShadowSerieData("休息", "天", "总量");
        int totalDays = 0;
        int totalStudyDays = 0;
        int totalWorkDays = 0;
        int totalTravelDays = 0;
        int totalRestDays = 0;
        for (ExperienceSum bean : qr.getBeanList()) {
            chartShadowData.addYaxis(String.valueOf(bean.getYear()));
            studySerie.addData(String.valueOf(bean.getStudyDays()));
            travelSerie.addData(String.valueOf(bean.getTravelDays()));
            workSerie.addData(String.valueOf(bean.getWorkDays()));
            int restDays = bean.getTotalDays() - bean.getStudyDays() - bean.getTravelDays() - bean.getWorkDays();
            restSerie.addData(String.valueOf(restDays));
            totalDays += bean.getTotalDays();
            totalStudyDays += bean.getStudyDays();
            totalTravelDays += bean.getTravelDays();
            totalWorkDays += bean.getWorkDays();
            totalRestDays += restDays;
        }
        chartShadowData.addSerie(studySerie);
        chartShadowData.addSerie(travelSerie);
        chartShadowData.addSerie(workSerie);
        chartShadowData.addSerie(restSerie);

        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("总时间统计");
        chartPieData.setUnit("天");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("天数");
        //学习
        chartPieData.getXdata().add(String.valueOf(totalStudyDays));
        ChartPieSerieDetailData studyDataDetail = new ChartPieSerieDetailData();
        studyDataDetail.setName("学习");
        chartPieData.getXdata().add("学习");
        studyDataDetail.setValue(totalStudyDays);
        serieData.getData().add(studyDataDetail);

        //工作
        chartPieData.getXdata().add(String.valueOf(totalWorkDays));
        ChartPieSerieDetailData workDataDetail = new ChartPieSerieDetailData();
        workDataDetail.setName("工作");
        chartPieData.getXdata().add("工作");
        workDataDetail.setValue(totalWorkDays);
        serieData.getData().add(workDataDetail);

        //旅行
        chartPieData.getXdata().add(String.valueOf(totalTravelDays));
        ChartPieSerieDetailData travelDataDetail = new ChartPieSerieDetailData();
        travelDataDetail.setName("旅行");
        chartPieData.getXdata().add("旅行");
        travelDataDetail.setValue(totalTravelDays);
        serieData.getData().add(travelDataDetail);

        //休息
        chartPieData.getXdata().add(String.valueOf(totalRestDays));
        ChartPieSerieDetailData restDataDetail = new ChartPieSerieDetailData();
        restDataDetail.setName("休息");
        chartPieData.getXdata().add("休息");
        restDataDetail.setValue(totalRestDays);
        serieData.getData().add(restDataDetail);

        chartPieData.setSubTitle("共:" + totalDays + "天");
        chartPieData.getDetailData().add(serieData);

        Map<String, BaseChartData> res = new HashMap<>();
        res.put("chartShadowData", chartShadowData);
        res.put("chartPieData", chartPieData);
        return callback(res);
    }

    /**
     * 修正
     *
     * @return
     */
    @RequestMapping(value = "/revise", method = RequestMethod.POST)
    public ResultBean revise(@RequestBody @Valid ExperienceSumReviseForm form) {
        ExperienceSum bean = baseService.getObject(beanClass,form.getSumId());
        int totalDays = DateUtil.getDays(bean.getYear());
        bean.setTotalDays(totalDays);
        List<Company> list = companyService.selectCompanyList(bean.getYear(), bean.getUserId());
        int nc = 0;
        //无效公司记录
        int unc = 0;
        Map res = new HashMap();
        int totalWorkDays = 0;
        for (Company c : list) {
            int ey = DateUtil.getYear(c.getEntryDate());
            int qy = DateUtil.getYear(c.getQuitDate());
            nc++;
            if (ey < bean.getYear() && qy > bean.getYear()) {
                //第一种情况：入职和离职都在该年外
                totalWorkDays += totalDays;
            } else if (ey == bean.getYear() && qy == bean.getYear()) {
                //第二种情况：入职和离职都在该年内
                totalWorkDays += DateUtil.getIntervalDays(c.getEntryDate(), c.getQuitDate());
            } else if (ey < bean.getYear() && qy >= bean.getYear()) {
                //第三种情况：入职在年外，离职在该年内
                Date date = DateUtil.getYearFirst(bean.getYear());
                totalWorkDays += DateUtil.getIntervalDays(date, c.getQuitDate());
            } else if (ey <= bean.getYear() && qy > bean.getYear()) {
                //第四种情况：入职在年内，离职在该年外
                Date date = DateUtil.getYearLast(bean.getYear());
                totalWorkDays += DateUtil.getIntervalDays(c.getEntryDate(), date);
            } else {
                unc++;
            }
        }
        bean.setWorkDays(totalWorkDays);
        res.put("nc", nc);
        res.put("unc", unc);
        List<Experience> leList = experienceService.selectExperienceList(bean.getYear(), bean.getUserId());
        int totalTravelDays = 0;
        int totalStudyDays = 0;
        int nle = 0;
        int unle = 0;
        for (Experience le : leList) {
            int ey = DateUtil.getYear(le.getStartDate());
            int qy = DateUtil.getYear(le.getEndDate());
            nle++;
            int v = 0;
            if (ey < bean.getYear() && qy > bean.getYear()) {
                //第一种情况：入职和离职都在该年外
                v += totalDays;
            } else if (ey == bean.getYear() && qy == bean.getYear()) {
                //第二种情况：入职和离职都在该年内
                v += DateUtil.getIntervalDays(le.getStartDate(), le.getEndDate());
            } else if (ey < bean.getYear() && qy >= bean.getYear()) {
                //第三种情况：入职在年外，离职在该年内
                Date date = DateUtil.getYearFirst(bean.getYear());
                v += DateUtil.getIntervalDays(date, le.getEndDate());
            } else if (ey <= bean.getYear() && qy > bean.getYear()) {
                //第四种情况：入职在年内，离职在该年外
                Date date = DateUtil.getYearLast(bean.getYear());
                v += DateUtil.getIntervalDays(le.getStartDate(), date);
            } else {
                unle++;
            }
            //第一种情况：入职和离职都在该年外
            if (le.getType() == ExperienceType.TRAVEL) {
                totalTravelDays += v;
            } else if (le.getType() == ExperienceType.STUDY) {
                totalStudyDays += v;
            }
        }
        res.put("nle", nle);
        res.put("unle", unle);
        res.put("year", bean.getYear());
        bean.setTravelDays(totalTravelDays);
        bean.setStudyDays(totalStudyDays);
        baseService.updateObject(bean);
        return callback(res);
    }


}
