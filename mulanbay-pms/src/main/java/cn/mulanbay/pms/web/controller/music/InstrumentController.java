package cn.mulanbay.pms.web.controller.music;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Instrument;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.music.instrument.InstrumentForm;
import cn.mulanbay.pms.web.bean.req.music.instrument.InstrumentSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 乐器管理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/instrument")
public class InstrumentController extends BaseController {

    private static Class<Instrument> beanClass = Instrument.class;

    /**
     * 获取乐器树
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(InstrumentSH sf) {

        try {
            sf.setPage(PageRequest.NO_PAGE);
            PageResult<Instrument> pr = getResult(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            List<Instrument> gtList = pr.getBeanList();
            for (Instrument gt : gtList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt.getInstrumentId());
                tb.setText(gt.getInstrumentName());
                list.add(tb);
            }
            return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取乐器树异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(InstrumentSH sf) {
        PageResult<Instrument> qr = getResult(sf);
        return callbackDataGrid(qr);
    }

    private PageResult<Instrument> getResult(InstrumentSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("orderIndex", Sort.ASC);
        pr.addSort(s);
        PageResult<Instrument> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid InstrumentForm form) {
        Instrument bean = new Instrument();
        BeanCopy.copy(form, bean);
        baseService.saveObject(bean);
        return callback(bean);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "instrumentId") Long instrumentId) {
        Instrument bean = baseService.getObject(beanClass,instrumentId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid InstrumentForm form) {
        Instrument bean = baseService.getObject(beanClass,form.getInstrumentId());
        BeanCopy.copy(form, bean);
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
        baseService.deleteObjects(beanClass, NumberUtil.stringToLongArray(deleteRequest.getIds()));
        return callback(null);
    }

}
