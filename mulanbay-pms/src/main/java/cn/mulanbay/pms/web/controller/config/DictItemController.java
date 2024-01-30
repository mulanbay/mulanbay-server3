package cn.mulanbay.pms.web.controller.config;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.DictGroup;
import cn.mulanbay.pms.persistent.domain.DictItem;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.config.dictItem.DictItemForm;
import cn.mulanbay.pms.web.bean.req.config.dictItem.DictItemSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典项
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/dictItem")
public class DictItemController extends BaseController {

    private static Class<DictItem> beanClass = DictItem.class;

    /**
     * 获取数据字典项树
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(DictItemSH sf) {
        try {
            sf.setStatus(CommonStatus.ENABLE);
            sf.setPage(PageRequest.NO_PAGE);
            PageRequest pr = sf.buildQuery();
            pr.setBeanClass(beanClass);
            Sort sort = new Sort("orderIndex", Sort.ASC);
            pr.addSort(sort);
            List<DictItem> gtList = baseService.getBeanList(pr);
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (DictItem gt : gtList) {
                TreeBean tb = new TreeBean();
                String code = gt.getCode();
                code = (code == null ? gt.getItemName() : code);
                tb.setId(code);
                tb.setText(gt.getItemName());
                list.add(tb);
            }
            return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取数据字典项树异常",
                    e);
        }
    }


    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(DictItemSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("orderIndex", Sort.ASC);
        pr.addSort(sort);
        PageResult<DictItem> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid DictItemForm formRequest) {
        DictItem bean = new DictItem();
        BeanCopy.copy(formRequest, bean);
        DictGroup group = baseService.getObject(DictGroup.class, formRequest.getGroupId());
        bean.setGroup(group);
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "itemId") Long itemId) {
        DictItem bean = baseService.getObject(beanClass, itemId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid DictItemForm formRequest) {
        DictItem bean = baseService.getObject(beanClass, formRequest.getItemId());
        BeanCopy.copy(formRequest, bean);
        DictGroup group = baseService.getObject(DictGroup.class, formRequest.getGroupId());
        bean.setGroup(group);
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
        baseService.deleteObjects(beanClass, NumberUtil.stringArrayToLongArray(deleteRequest.getIds().split(",")));
        return callback(null);
    }

}
