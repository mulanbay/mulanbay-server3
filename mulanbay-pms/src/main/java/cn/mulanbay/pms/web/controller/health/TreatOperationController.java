package cn.mulanbay.pms.web.controller.health;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Treat;
import cn.mulanbay.pms.persistent.domain.TreatOperation;
import cn.mulanbay.pms.persistent.dto.health.TreatOperationStat;
import cn.mulanbay.pms.persistent.service.TreatService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.health.operation.*;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 手术
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/treatOperation")
public class TreatOperationController extends BaseController {

    private static Class<TreatOperation> beanClass = TreatOperation.class;

    @Autowired
    TreatService treatService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(TreatOperationSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("treatDate", Sort.DESC);
        pr.addSort(s);
        PageResult<TreatOperation> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 获取看病用药的手术分类列表
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(TreatOperationGroupSH sf) {
        try {
            List<String> categoryList = treatService.getOperationCateList(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (String gt : categoryList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt);
                tb.setText(gt);
                list.add(tb);
            }
            return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取看病手术的疾病分类列表异常",
                    e);
        }
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid TreatOperationForm form) {
        TreatOperation bean = new TreatOperation();
        BeanCopy.copy(form, bean);
        Treat treat = baseService.getObject(Treat.class,form.getTreatId());
        bean.setTreat(treat);
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "operationId") Long operationId) {
        TreatOperation bean = baseService.getObject(beanClass, operationId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid TreatOperationForm form) {
        TreatOperation bean = baseService.getObject(beanClass, form.getOperationId());
        BeanCopy.copy(form, bean);
        Treat treat = baseService.getObject(Treat.class,form.getTreatId());
        bean.setTreat(treat);
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
            treatService.deleteOperation(Long.valueOf(s));
        }
        return callback(null);
    }

    /**
     * 总的概要统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(TreatOperationStatSH sf) {
        List<TreatOperationStat> data = treatService.getOperationStat(sf);
        TreatOperationStat total = new TreatOperationStat();
        BigInteger n = BigInteger.ZERO;
        BigDecimal totalFee = BigDecimal.ZERO;
        for (TreatOperationStat bean : data) {
            n = n.add(bean.getTotalCount());
            totalFee = totalFee.add(bean.getTotalFee());
        }
        total.setName("小计");
        total.setTotalCount(n);
        total.setTotalFee(totalFee);
        data.add(total);
        return callback(data);
    }

    /**
     * 获取最近一次的手术
     *
     * @return
     */
    @RequestMapping(value = "/lastOperation", method = RequestMethod.GET)
    public ResultBean lastOperation(@Valid TreatOperationLastSH getRequest) {
        TreatOperation bean = treatService.getLastOperation(getRequest.getOperationName(), getRequest.getUserId());
        return callback(bean);
    }


}
