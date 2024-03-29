package cn.mulanbay.pms.web.controller.report;

import cn.mulanbay.ai.ml.processor.bean.PlanReportER;
import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.ReportHandler;
import cn.mulanbay.pms.handler.UserScoreHandler;
import cn.mulanbay.pms.persistent.domain.PlanReport;
import cn.mulanbay.pms.persistent.domain.PlanTemplate;
import cn.mulanbay.pms.persistent.domain.UserPlan;
import cn.mulanbay.pms.persistent.enums.PlanType;
import cn.mulanbay.pms.persistent.service.PlanService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.report.plan.PlanReportSH;
import cn.mulanbay.pms.web.bean.req.report.plan.UserPlanForm;
import cn.mulanbay.pms.web.bean.req.report.plan.UserPlanSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.report.UserPlanVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static cn.mulanbay.persistent.dao.BaseHibernateDao.NO_PAGE;

/**
 * 用户计划
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/userPlan")
public class UserPlanController extends BaseController {


    private static Class<UserPlan> beanClass = UserPlan.class;

    @Autowired
    PlanService planService;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    UserScoreHandler userScoreHandler;

    @Autowired
    ReportHandler reportHandler;

    /**
     * 用户计划树
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(UserPlanSH sf) {
        try {
            sf.setPage(NO_PAGE);
            PageRequest pr = sf.buildQuery();
            pr.setBeanClass(beanClass);
            Sort s = new Sort("planType", Sort.ASC);
            pr.addSort(s);
            List<UserPlan> unList = baseService.getBeanList(pr);
            List<TreeBean> result = new ArrayList<>();
            if(StringUtil.isEmpty(unList)){
                return callback(result);
            }
            PlanType current = unList.get(0).getPlanType();
            TreeBean typeTreeBean = new TreeBean();
            typeTreeBean.setId(current.name());
            typeTreeBean.setText(current.getName());
            int n = unList.size();
            for (int i = 0; i < n; i++) {
                UserPlan pc = unList.get(i);
                TreeBean tb = new TreeBean();
                tb.setId(pc.getPlanId());
                tb.setText(pc.getTitle());
                PlanType m = pc.getPlanType();
                if (m == current) {
                    typeTreeBean.addChild(tb);
                }else{
                    current = m;
                    result.add(typeTreeBean);
                    typeTreeBean = new TreeBean();
                    typeTreeBean.setId(current.name());
                    typeTreeBean.setText(current.getName());
                    typeTreeBean.addChild(tb);
                }
                if (i == n - 1) {
                    //最后一个
                    result.add(typeTreeBean);
                }
            }
            return callback(result);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取用户计划树异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(UserPlanSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("orderIndex", Sort.ASC);
        pr.addSort(s);
        PageResult<UserPlan> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid UserPlanForm form) {
        UserPlan bean = new UserPlan();
        BeanCopy.copy(form, bean);
        PlanTemplate template = planService.getPlanTemplate(form.getTemplateId(), form.getLevel());
        if (template == null) {
            return callbackErrorCode(PmsCode.USER_ENTITY_NOT_ALLOWED);
        }
        bean.setTemplate(template);
        planService.saveOrUpdateUsePlan(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "planId") Long planId) {
        UserPlan bean = baseService.getObject(beanClass,planId);
        return callback(bean);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(@RequestParam(name = "planId") Long planId,@RequestParam(name = "predict") Boolean predict) {
        UserPlan userPlan = baseService.getObject(beanClass,planId);
        UserPlanVo vo = this.getStatVo(userPlan,predict);
        return callback(vo);
    }

    private UserPlanVo getStatVo(UserPlan plan,boolean predict){
        UserPlanVo vo = new UserPlanVo();
        BeanCopy.copy(plan, vo);
        //设置PlanReport
        PlanReport report = planService.statPlanReport(plan);
        vo.setPlanReport(report);
        if(predict){
            PlanReportER pv = reportHandler.predictPlanReport(report);
            if(pv!=null){
                vo.setPredictCount(pv.getCountRate()*report.getPlanCountValue());
                vo.setPredictValue(pv.getValueRate()*report.getPlanValue());
            }
        }
        return vo;
    }

    /**
     * 统计数据
     *
     * @return
     */
    @RequestMapping(value = "/statList", method = RequestMethod.GET)
    public ResultBean statList(UserPlanSH sf) {
        Boolean realtime = sf.getRealtime();
        if(realtime){
            return callbackDataGrid(this.realtimeStatList(sf));
        }else{
            return callbackDataGrid(this.historyStatList(sf));
        }
    }

    /**
     * 实时
     * @param sf
     * @return
     */
    private PageResult<UserPlanVo> realtimeStatList(UserPlanSH sf){
        PageRequest pr = sf.buildQuery();
        Sort s = new Sort("orderIndex", Sort.ASC);
        pr.addSort(s);
        pr.setBeanClass(beanClass);
        PageResult<UserPlan> unResult = baseService.getBeanResult(pr);
        List<UserPlanVo> list = new ArrayList<>();
        Boolean predict = sf.getPredict();
        for(UserPlan userPlan: unResult.getBeanList()){
            UserPlanVo vo = this.getStatVo(userPlan,predict);
            list.add(vo);
        }
        PageResult<UserPlanVo> res = new PageResult<>(sf.getPage(), sf.getPageSize());
        res.setBeanList(list);
        res.setMaxRow(unResult.getMaxRow());
        return res;
    }

    /**
     * 历史
     * @param sf
     * @return
     */
    private PageResult<UserPlanVo> historyStatList(UserPlanSH sf){
        PlanReportSH ursh = new PlanReportSH();
        BeanCopy.copy(sf,ursh);
        PageRequest pr = ursh.buildQuery();
        Sort s = new Sort("bussDay", Sort.DESC);
        pr.addSort(s);
        pr.setBeanClass(PlanReport.class);
        PageResult<PlanReport> unResult = baseService.getBeanResult(pr);
        List<UserPlanVo> list = new ArrayList<>();
        Boolean predict = sf.getPredict();
        for(PlanReport report: unResult.getBeanList()){
            UserPlanVo vo = new UserPlanVo();
            BeanCopy.copy(report.getPlan(), vo);
            //设置PlanReport
            vo.setPlanReport(report);
            if(predict){
                PlanReportER pv = reportHandler.predictPlanReport(report);
                if(pv!=null){
                    vo.setPredictCount(pv.getCountRate()*report.getPlanCountValue());
                    vo.setPredictValue(pv.getValueRate()*report.getPlanValue());
                }
            }
            list.add(vo);
        }
        PageResult<UserPlanVo> res = new PageResult<>(sf.getPage(), sf.getPageSize());
        res.setBeanList(list);
        res.setMaxRow(unResult.getMaxRow());
        return res;
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid UserPlanForm form) {
        UserPlan bean = baseService.getObject(beanClass,form.getPlanId());
        BeanCopy.copy(form, bean);
        PlanTemplate template = planService.getPlanTemplate(form.getTemplateId(), form.getLevel());
        if (template == null) {
            return callbackErrorCode(PmsCode.USER_ENTITY_NOT_ALLOWED);
        }
        bean.setTemplate(template);
        planService.saveOrUpdateUsePlan(bean);
        return callback(null);
    }


    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        String[] ss = deleteRequest.getIds().split(",");
        for(String s :ss){
            planService.deleteUsePlan(Long.valueOf(s));
        }
        return callback(null);
    }

}
