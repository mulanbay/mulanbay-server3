package cn.mulanbay.pms.web.controller.report;

import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.PlanReport;
import cn.mulanbay.pms.persistent.service.PlanService;
import cn.mulanbay.pms.web.bean.req.report.plan.PlanReportSH;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户计划报告
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("planReport")
public class PlanReportController extends BaseController {

    @Autowired
    PlanService planService;

    private static Class<PlanReport> beanClass = PlanReport.class;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(PlanReportSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("bussDay", Sort.DESC);
        pr.addSort(s);
        PageResult<PlanReport> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

}
