package cn.mulanbay.pms.web.controller.consume;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.ConsumeSource;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.consume.consumeSource.ConsumeSourceForm;
import cn.mulanbay.pms.web.bean.req.consume.consumeSource.ConsumeSourceSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 购买类型（消费来源）
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/consumeSource")
public class ConsumeSourceController extends BaseController {

    private static Class<ConsumeSource> beanClass = ConsumeSource.class;

    /**
     * 来源树
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(ConsumeSourceSH sf) {
        try {
            sf.setStatus(CommonStatus.ENABLE);
            PageResult<ConsumeSource> pr = getResult(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            List<ConsumeSource> gtList = pr.getBeanList();
            for (ConsumeSource gt : gtList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt.getSourceId());
                tb.setText(gt.getSourceName());
                list.add(tb);
            }
            return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取购买来源树异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(ConsumeSourceSH sf) {
        return callbackDataGrid(getResult(sf));
    }

    private PageResult<ConsumeSource> getResult(ConsumeSourceSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("orderIndex", Sort.ASC);
        pr.addSort(sort);
        PageResult<ConsumeSource> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid ConsumeSourceForm form) {
        ConsumeSource bean = new ConsumeSource();
        BeanCopy.copyProperties(form,bean);
        bean.setCreatedTime(new Date());
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "sourceId") Long sourceId) {
        ConsumeSource bean = baseService.getObject(beanClass,sourceId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid ConsumeSourceForm form) {
        ConsumeSource bean = baseService.getObject(beanClass,form.getSourceId());
        BeanCopy.copyProperties(form, bean);
        bean.setModifyTime(new Date());
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
