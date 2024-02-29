package cn.mulanbay.pms.web.controller.report;

import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.PlanTemplate;
import cn.mulanbay.pms.persistent.domain.StatBindConfig;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.PlanType;
import cn.mulanbay.pms.persistent.enums.StatBussType;
import cn.mulanbay.pms.persistent.service.PlanService;
import cn.mulanbay.pms.persistent.service.StatBindConfigService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.report.ReportTreeSH;
import cn.mulanbay.pms.web.bean.req.report.ReportUserTreeSH;
import cn.mulanbay.pms.web.bean.req.report.plan.PlanTemplateForm;
import cn.mulanbay.pms.web.bean.req.report.plan.PlanTemplateSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 报表配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/planTemplate")
public class PlanTemplateController extends BaseController {

    private static Class<PlanTemplate> beanClass = PlanTemplate.class;

    @Autowired
    StatBindConfigService statBindConfigService;

    @Autowired
    PlanService planService;
    /**
     * 用户树
     *
     * @return
     */
    @RequestMapping(value = "/userTree")
    public ResultBean userTree(ReportUserTreeSH sf) {
        List<TreeBean> list = this.createPlanTemplateTree(sf.getLevel(),sf.getBussType());
        return callback(list);
    }

    /**
     * 模版树
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(ReportTreeSH sf) {
        List<TreeBean> list = this.createPlanTemplateTree(null,sf.getBussType());
        return callback(list);
    }

    private List<TreeBean> createPlanTemplateTree(Integer maxLevel, BussType bussType) {
        PlanTemplateSH sh = new PlanTemplateSH();
        sh.setBussType(bussType);
        sh.setMaxLevel(maxLevel);
        PageRequest pr = sh.buildQuery();
        pr.setBeanClass(beanClass);
        pr.setPage(PageRequest.NO_PAGE);
        Sort s = new Sort("bussType", Sort.ASC);
        Sort s2 = new Sort("orderIndex", Sort.ASC);
        pr.addSort(s);
        pr.addSort(s2);
        List<PlanTemplate> gtList = baseService.getBeanList(pr);
        List<TreeBean> result = new ArrayList<>();
        BussType current = gtList.get(0).getBussType();
        TreeBean typeTreeBean = new TreeBean();
        typeTreeBean.setId("P_" + current.name());
        typeTreeBean.setText(current.getName());
        int n = gtList.size();
        for (int i = 0; i < n; i++) {
            PlanTemplate pc = gtList.get(i);
            TreeBean tb = new TreeBean();
            tb.setId(pc.getTemplateId());
            tb.setText(pc.getTemplateName());
            BussType m = pc.getBussType();
            if (m == current) {
                typeTreeBean.addChild(tb);
            }else{
                current = m;
                result.add(typeTreeBean);
                typeTreeBean = new TreeBean();
                typeTreeBean.setId("P_" + current.name());
                typeTreeBean.setText(current.getName());
                typeTreeBean.addChild(tb);
            }
            if (i == n - 1) {
                //最后一个
                result.add(typeTreeBean);
            }
        }
        return result;
    }

    /**
     * 获取加班记录列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(PlanTemplateSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("orderIndex", Sort.ASC);
        pr.addSort(s);
        PageResult<PlanTemplate> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid PlanTemplateForm form) {
        PlanTemplate bean = new PlanTemplate();
        BeanCopy.copy(form, bean);
        List<StatBindConfig> configList = new ArrayList<>();
        Long fromTemplateId = form.getFromTemplateId();
        if(fromTemplateId!=null&&form.getCopyItems()!=null&&form.getCopyItems()==true){
            //加载配置项
            List<StatBindConfig> copyConfigList = statBindConfigService.getConfigList(fromTemplateId, StatBussType.PLAN);
            if(StringUtil.isNotEmpty(copyConfigList)){
                for(StatBindConfig svc : copyConfigList){
                    StatBindConfig config = new StatBindConfig();
                    BeanCopy.copy(svc,config);
                    config.setFid(null);
                    config.setConfigId(null);
                    configList.add(config);
                }
            }
        }
        planService.savePlanTemplate(bean,configList);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "templateId") Long templateId) {
        PlanTemplate bean = baseService.getObject(beanClass, templateId);
        return callback(bean);
    }

    /**
     * 下一个排序号
     *
     * @return
     */
    @RequestMapping(value = "/nextOrderIndex", method = RequestMethod.GET)
    public ResultBean nextOrderIndex(@RequestParam(name = "bussType") BussType bussType) {
        Short index = planService.getTemplateMaxOrderIndex(bussType);
        short next = index==null? 0:index++;
        return callback(next);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid PlanTemplateForm form) {
        PlanTemplate bean = baseService.getObject(beanClass, form.getTemplateId());
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
        String[] ss = deleteRequest.getIds().split(",");
        for(String s :ss){
            planService.deletePlanTemplate(Long.valueOf(s));
        }
        return callback(null);
    }


}
