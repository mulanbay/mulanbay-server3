package cn.mulanbay.pms.web.controller.config;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.ScoreGroup;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.service.ScoreConfigService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.config.scoreGroup.ScoreGroupCopyForm;
import cn.mulanbay.pms.web.bean.req.config.scoreGroup.ScoreGroupForm;
import cn.mulanbay.pms.web.bean.req.config.scoreGroup.ScoreGroupSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 评分配置组
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/scoreGroup")
public class ScoreGroupController extends BaseController {

    private static Class<ScoreGroup> beanClass = ScoreGroup.class;

    @Autowired
    ScoreConfigService scoreConfigService;

    /**
     * 获取数据组树
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(ScoreGroupSH sf) {
        try {
            sf.setStatus(CommonStatus.ENABLE);
            PageRequest pr = sf.buildQuery();
            pr.setBeanClass(beanClass);
            List<ScoreGroup> gtList = baseService.getBeanList(pr);
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (ScoreGroup gt : gtList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt.getGroupId());
                tb.setText(gt.getGroupName());
                list.add(tb);
            }
            return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取数据组树异常",
                    e);
        }
    }


    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(ScoreGroupSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<ScoreGroup> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid ScoreGroupForm form) {
        ScoreGroup bean = new ScoreGroup();
        BeanCopy.copy(form, bean);
        baseService.saveObject(bean);
        return callback(null);
    }

    /**
     * 复制
     *
     * @return
     */
    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public ResultBean copy(@RequestBody @Valid ScoreGroupCopyForm form) {
        scoreConfigService.copyGroup(form.getTemplateId(), form.getCode(), form.getGroupName());
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "groupId") Long groupId) {
        ScoreGroup bean = baseService.getObject(beanClass, groupId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid ScoreGroupForm form) {
        ScoreGroup bean = baseService.getObject(beanClass, form.getGroupId());
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
        String[] ss= deleteRequest.getIds().split(",");
        for(String s :ss){
            scoreConfigService.deleteGroup(Long.valueOf(s));
        }
        return callback(null);
    }

}
