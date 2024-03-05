package cn.mulanbay.pms.web.controller.report;

import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.StringCoderUtil;
import cn.mulanbay.pms.persistent.domain.ChartTemplate;
import cn.mulanbay.pms.persistent.domain.StatBindConfig;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.StatBussType;
import cn.mulanbay.pms.persistent.service.ChartService;
import cn.mulanbay.pms.persistent.service.StatBindConfigService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.report.ReportTreeSH;
import cn.mulanbay.pms.web.bean.req.report.ReportUserTreeSH;
import cn.mulanbay.pms.web.bean.req.report.chart.ChartTemplateForm;
import cn.mulanbay.pms.web.bean.req.report.chart.ChartTemplateSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 图表配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/chartTemplate")
public class ChartTemplateController extends BaseController {

    private static Class<ChartTemplate> beanClass = ChartTemplate.class;

    @Autowired
    StatBindConfigService statBindConfigService;

    @Autowired
    ChartService chartService;

    /**
     * 图表配置模板选项列表(用户使用，需要判断用户级别)
     *
     * @return
     */
    @RequestMapping(value = "/userTree")
    public ResultBean userTree(ReportUserTreeSH sf) {
        List<TreeBean> list = this.createChartTemplateTree(sf.getLevel(),sf.getBussType());
        return callback(list);
    }


    /**
     * 为锻炼管理界面的下拉菜单使用
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(ReportTreeSH sf) {
        List<TreeBean> list = this.createChartTemplateTree(null,sf.getBussType());
        return callback(list);
    }

    private List<TreeBean> createChartTemplateTree(Integer maxLevel, BussType bussType) {
        ChartTemplateSH sh = new ChartTemplateSH();
        sh.setBussType(bussType);
        sh.setMaxLevel(maxLevel);
        PageRequest pr = sh.buildQuery();
        pr.setBeanClass(beanClass);
        pr.setPage(PageRequest.NO_PAGE);
        Sort s = new Sort("bussType", Sort.ASC);
        Sort s2 = new Sort("orderIndex", Sort.ASC);
        pr.addSort(s);
        pr.addSort(s2);
        List<ChartTemplate> gtList = baseService.getBeanList(pr);
        List<TreeBean> result = new ArrayList<>();
        BussType current = gtList.get(0).getBussType();
        TreeBean typeTreeBean = new TreeBean();
        typeTreeBean.setId(current.name());
        typeTreeBean.setText(current.getName());
        int n = gtList.size();
        for (int i = 0; i < n; i++) {
            ChartTemplate pc = gtList.get(i);
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
                typeTreeBean.setId(current.name());
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
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(ChartTemplateSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("orderIndex", Sort.ASC);
        pr.addSort(s);
        PageResult<ChartTemplate> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid ChartTemplateForm form) {
        ChartTemplate bean = new ChartTemplate();
        BeanCopy.copy(form, bean);
        String para = StringCoderUtil.decodeJson(form.getPara());
        bean.setPara(para);
        List<StatBindConfig> configList = new ArrayList<>();
        Long fromTemplateId = form.getFromTemplateId();
        if(fromTemplateId!=null&&form.getCopyItems()!=null&&form.getCopyItems()==true){
            //加载配置项
            List<StatBindConfig> copyConfigList = statBindConfigService.getConfigList(fromTemplateId, StatBussType.CHART);
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
        chartService.saveChartTemplate(bean,configList);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "templateId") Long templateId) {
        ChartTemplate bean = baseService.getObject(beanClass, templateId);
        return callback(bean);
    }
    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid ChartTemplateForm form) {
        ChartTemplate bean = baseService.getObject(beanClass, form.getTemplateId());
        BeanCopy.copy(form, bean);
        String para = StringCoderUtil.decodeJson(form.getPara());
        bean.setPara(para);
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
        String[] ss = deleteRequest.getIds().split(",");
        for(String s :ss){
            chartService.deleteChartTemplate(Long.valueOf(s));
        }
        return callback(null);
    }

}
