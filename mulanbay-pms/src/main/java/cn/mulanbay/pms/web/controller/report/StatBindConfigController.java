package cn.mulanbay.pms.web.controller.report;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.StatBindConfig;
import cn.mulanbay.pms.persistent.dto.report.StatBindConfigDTO;
import cn.mulanbay.pms.persistent.enums.StatValueSource;
import cn.mulanbay.pms.persistent.service.StatBindConfigService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.report.bind.StatBindConfigForm;
import cn.mulanbay.pms.web.bean.req.report.bind.StatBindConfigSH;
import cn.mulanbay.pms.web.bean.req.report.bind.StatBindConfigsSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 提醒、计划、图表等通用配置值项目
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/statBindConfig")
public class StatBindConfigController extends BaseController {

    private static Class<StatBindConfig> beanClass = StatBindConfig.class;

    @Autowired
    StatBindConfigService statBindConfigService;

    /**
     * 模版树
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(StatBindConfigSH sh) {
        PageRequest pr = sh.buildQuery();
        pr.setBeanClass(beanClass);
        pr.setPage(PageRequest.NO_PAGE);
        Sort s = new Sort("source", Sort.ASC);
        Sort s2 = new Sort("orderIndex", Sort.ASC);
        pr.addSort(s);
        pr.addSort(s2);
        List<StatBindConfig> gtList = baseService.getBeanList(pr);
        List<TreeBean> result = new ArrayList<>();
        StatValueSource current = gtList.get(0).getSource();
        TreeBean typeTreeBean = new TreeBean();
        typeTreeBean.setId(current.name());
        typeTreeBean.setText(current.getName());
        int n = gtList.size();
        for (int i = 0; i < n; i++) {
            StatBindConfig pc = gtList.get(i);
            TreeBean tb = new TreeBean();
            tb.setId(pc.getConfigId());
            tb.setText(pc.getConfigName());
            StatValueSource m = pc.getSource();
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
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(StatBindConfigSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("orderIndex", Sort.ASC);
        pr.addSort(s);
        PageResult<StatBindConfig> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid StatBindConfigForm form) {
        StatBindConfig bean = new StatBindConfig();
        BeanCopy.copy(form,bean);
        baseService.saveObject(bean);
        return callback(null);
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "configId") Long configId) {
        StatBindConfig bean = baseService.getObject(beanClass, configId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid StatBindConfigForm form) {
        StatBindConfig bean = baseService.getObject(beanClass, form.getConfigId());
        BeanCopy.copy(form,bean);
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
     * 解析配置列表
     *
     * @param sf
     * @return
     */
    @RequestMapping(value = "/parseConfigs", method = RequestMethod.GET)
    public ResultBean parseConfigs(@Valid StatBindConfigsSH sf) {
        List<StatBindConfigDTO> list = statBindConfigService.getConfigs(sf.getFid(), sf.getType(), sf.getUserId());
        return callback(list);
    }

}
