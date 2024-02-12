package cn.mulanbay.pms.web.controller.health;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.domain.TreatOperation;
import cn.mulanbay.pms.persistent.domain.TreatTest;
import cn.mulanbay.pms.persistent.enums.ChartType;
import cn.mulanbay.pms.persistent.enums.TreatTestResult;
import cn.mulanbay.pms.persistent.service.TreatService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.health.test.*;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 看病的检测结果
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/treatTest")
public class TreatTestController extends BaseController {

    private static Class<TreatTest> beanClass = TreatTest.class;

    /**
     * 分类分组的时长
     * 例：365天则说明统计最近一年内的分组信息
     */
    @Value("${mulanbay.health.categoryDays}")
    int categoryDays;

    @Autowired
    TreatService treatService;

    /**
     * 获取检测的分类列表
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(TreatTestGroupSH sf) {
        try {
            Date endDate = new Date();
            Date startDate = DateUtil.getDate(-categoryDays,endDate);
            sf.setStartDate(startDate);
            sf.setEndDate(endDate);
            List<String> categoryList = treatService.getTestCateList(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            int i = 0;
            for (String gt : categoryList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt);
                tb.setText(gt);
                list.add(tb);
                i++;
            }
            return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取检测的分类列表异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(TreatTestSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("createdTime", sf.getSort());
        pr.addSort(s);
        PageResult<TreatTest> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid TreatTestForm form) {
        TreatTest bean = new TreatTest();
        BeanCopy.copy(form, bean);
        TreatOperation operation = baseService.getObject(TreatOperation.class,form.getOperationId());
        bean.setOperation(operation);
        if (bean.getResult() == null) {
            if ((bean.getMinValue() == null || bean.getMaxValue() == null) && StringUtil.isEmpty(bean.getReferScope())) {
                return callbackErrorInfo("没有参考范围值时，必须手动设置分析结果");
            } else {
                bean.setResult(getResult(bean));
            }
        }
        checkTest(bean);
        baseService.saveObject(bean);
        return callback(bean);
    }

    /**
     * 计算检查结果
     *
     * @param bean
     * @return
     */
    private TreatTestResult getResult(TreatTest bean) {
        String v = bean.getValue();
        boolean isNum = NumberUtil.isNumber(v);
        if (isNum) {
            double b = Double.valueOf(v);
            if (b < bean.getMinValue()) {
                return TreatTestResult.LOWER;
            } else if (b > bean.getMaxValue()) {
                return TreatTestResult.HIGHER;
            } else {
                return TreatTestResult.NORMAL;
            }
        } else {
            String rc = bean.getReferScope();
            if (StringUtil.isNotEmpty(rc)) {
                if (v.equals(rc)) {
                    return TreatTestResult.NORMAL;
                } else {
                    return TreatTestResult.HIGHER;
                }
            }
            if ("正常".equals(v) || "阴性".equals(v)) {
                return TreatTestResult.NORMAL;
            } else {
                return TreatTestResult.HIGHER;
            }
        }
    }

    private void checkTest(TreatTest test){
        long n = treatService.getTestNameCount(test.getOperation().getOperationId(),test.getUserId(),test.getName(),test.getTestId());
        if(n>0){
            throw new ApplicationException(PmsCode.TREAT_TEST_NAME_DUPLICATE);
        }
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "testId") Long testId) {
        TreatTest bean = baseService.getObject(beanClass, testId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid TreatTestForm form) {
        TreatTest bean = baseService.getObject(beanClass, form.getTestId());
        BeanCopy.copy(form, bean);
        TreatOperation operation = baseService.getObject(TreatOperation.class,form.getOperationId());
        bean.setOperation(operation);
        checkTest(bean);
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
     * 获取最近一次的检查
     *
     * @return
     */
    @RequestMapping(value = "/lastTest", method = RequestMethod.GET)
    public ResultBean lastTest(@Valid TreatTestLastSH getRequest) {
        TreatTest bean = treatService.getLastTest(getRequest.getName(), getRequest.getUserId());
        return callback(bean);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(@Valid TreatTestStatSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setPage(-1);
        pr.setBeanClass(beanClass);
        Sort s = new Sort("testTime", Sort.ASC);
        pr.addSort(s);
        PageResult<TreatTest> qr = baseService.getBeanResult(pr);
        List<TreatTest> list = qr.getBeanList();
        if (list.isEmpty()) {
            return callback(null);
        } else {
            String vt = list.get(0).getValue();
            Map res = new HashMap();
            if (NumberUtil.isNumber(vt)) {
                //柱状图
                res.put("chartType", ChartType.LINE);
                res.put("chartData", createStatBarData(list, sf.getName()));
                TreatTest t1 = list.get(0);
                res.put("minValue",t1.getMinValue());
                res.put("maxValue",t1.getMaxValue());
            } else {
                //饼图
                res.put("chartType", ChartType.PIE);
                res.put("chartData", createStatPieData(list, sf.getName()));
            }
            return callback(res);
        }
    }

    /**
     * 封装检查报告的柱状图数据
     *
     * @param list
     * @return
     */
    private ChartData createStatBarData(List<TreatTest> list, String name) {
        TreatTest t1 = list.get(0);
        ChartData chartData = new ChartData();
        chartData.setTitle("[" + name + "]的检查报告");
        chartData.setLegendData(new String[]{"值"});
        ChartYData yData1 = new ChartYData("值",t1.getUnit());
        for (TreatTest bean : list) {
            chartData.getXdata().add(DateUtil.getFormatDate(bean.getTestTime(), DateUtil.FormatDay1));
            yData1.getData().add(bean.getValue());
        }
        chartData.getYdata().add(yData1);
        if (t1.getMinValue() != null && t1.getMaxValue() != null) {
            String subTitle = "参考值范围:" + t1.getMinValue() + "~" + t1.getMaxValue()+",单位:"+t1.getUnit();
            chartData.setSubTitle(subTitle);
        }
        return chartData;
    }

    /**
     * 封装检查报告的饼状图数据
     *
     * @param list
     * @return
     */
    private ChartPieData createStatPieData(List<TreatTest> list, String name) {
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("[" + name + "]的检查报告");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("次");
        Map<String, Integer> md = new HashMap<>();
        for (TreatTest bean : list) {
            Integer n = md.get(bean.getValue());
            if (n == null) {
                md.put(bean.getValue(), 1);
            } else {
                md.put(bean.getValue(), n + 1);
            }
        }
        for (String key : md.keySet()) {
            chartPieData.getXdata().add(key);
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(key);
            dataDetail.setValue(md.get(key));
            serieData.getData().add(dataDetail);
        }
        chartPieData.getDetailData().add(serieData);
        return chartPieData;
    }
}
