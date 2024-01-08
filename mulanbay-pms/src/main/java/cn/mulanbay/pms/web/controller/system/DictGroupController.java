package cn.mulanbay.pms.web.controller.system;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.DictGroup;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.service.DictService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.config.dictGroup.DictGroupForm;
import cn.mulanbay.pms.web.bean.req.config.dictGroup.DictGroupSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典组
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/dictGroup")
public class DictGroupController extends BaseController {

    private static Class<DictGroup> beanClass = DictGroup.class;

    @Autowired
    DictService dictService;

    /**
     * 获取数据组树
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(DictGroupSH sf) {
        try {
            sf.setStatus(CommonStatus.ENABLE);
            sf.setPage(PageRequest.NO_PAGE);
            PageRequest pr = sf.buildQuery();
            pr.setBeanClass(beanClass);
            Sort sort = new Sort("orderIndex", Sort.ASC);
            pr.addSort(sort);
            List<DictGroup> gtList = baseService.getBeanList(pr);
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (DictGroup gt : gtList) {
                TreeBean tb = new TreeBean();
                if ("code".equals(sf.getIdField())) {
                    tb.setId(gt.getCode());
                } else {
                    tb.setId(gt.getGroupId());
                }
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
    public ResultBean list(DictGroupSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("orderIndex", Sort.DESC);
        pr.addSort(sort);
        PageResult<DictGroup> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid DictGroupForm formRequest) {
        DictGroup bean = new DictGroup();
        BeanCopy.copy(formRequest, bean);
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "groupId") Long groupId) {
        DictGroup bean = baseService.getObject(beanClass, groupId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid DictGroupForm formRequest) {
        DictGroup bean = baseService.getObject(beanClass, formRequest.getGroupId());
        BeanCopy.copy(formRequest, bean);
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
        for(String s : ss){
            dictService.deleteDictGroup(Long.valueOf(s));
        }
        return callback(null);
    }

}
