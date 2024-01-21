package cn.mulanbay.pms.web.controller.config;

import cn.mulanbay.ai.nlp.processor.NLPProcessor;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.handler.ConsumeHandler;
import cn.mulanbay.pms.handler.bean.consume.GoodsLifetimeMatchBean;
import cn.mulanbay.pms.persistent.domain.GoodsLifetime;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.config.goodsLifetime.GoodsLifetimeCompareAndMatchForm;
import cn.mulanbay.pms.web.bean.req.config.goodsLifetime.GoodsLifetimeForm;
import cn.mulanbay.pms.web.bean.req.config.goodsLifetime.GoodsLifetimeGetAndMatchForm;
import cn.mulanbay.pms.web.bean.req.config.goodsLifetime.GoodsLifetimeSH;
import cn.mulanbay.pms.web.bean.req.consume.consume.GoodsNameAiMatchForm;
import cn.mulanbay.pms.web.bean.res.config.goodsLifetime.GoodsLifetimeGetAndMatchVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 商品寿命配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/goodsLifetime")
public class GoodsLifetimeController extends BaseController {

    private static Class<GoodsLifetime> beanClass = GoodsLifetime.class;

    @Autowired
    NLPProcessor nlpProcessor;

    @Autowired
    ConsumeHandler consumeHandler;

    /**
     * 获取列表
     * @param sf
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(GoodsLifetimeSH sf) {
        return callbackDataGrid(getResult(sf));
    }

    private PageResult<GoodsLifetime> getResult(GoodsLifetimeSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<GoodsLifetime> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid GoodsLifetimeForm form) {
        GoodsLifetime bean = new GoodsLifetime();
        BeanCopy.copy(form, bean);
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
    public ResultBean get(@RequestParam(name = "lifetimeId") Long lifetimeId) {
        GoodsLifetime bean = baseService.getObject(beanClass,lifetimeId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid GoodsLifetimeForm form) {
        GoodsLifetime bean = baseService.getObject(beanClass,form.getLifetimeId());
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

    /**
     * 商品寿命匹配
     *
     * @return
     */
    @RequestMapping(value = "/aiMatch", method = RequestMethod.POST)
    public ResultBean aiMatch(@RequestBody @Valid GoodsNameAiMatchForm mr) {
        GoodsLifetimeMatchBean bean = consumeHandler.matchLifetime(mr.getGoodsName());
        if(bean==null){
            return callback(null);
        }
        return callback(bean);
    }

    /**
     * 商品寿命匹配
     *
     * @return
     */
    @RequestMapping(value = "/getAndMath", method = RequestMethod.POST)
    public ResultBean getAndMath(@RequestBody @Valid GoodsLifetimeGetAndMatchForm mr) {
        GoodsLifetimeSH sf = new GoodsLifetimeSH();
        sf.setPage(mr.getPage());
        sf.setPageSize(mr.getPageSize());
        sf.setNeedTotal(false);
        PageResult<GoodsLifetime> pr = this.getResult(sf);
        GoodsLifetimeMatchBean match = consumeHandler.matchLifetime(mr.getGoodsName(),pr.getBeanList(),false);
        GoodsLifetimeGetAndMatchVo vo = new GoodsLifetimeGetAndMatchVo();
        vo.setConfigs(pr);
        vo.setMatch(match);
        return callback(vo);
    }

    /**
     * 商品寿命匹配
     *
     * @return
     */
    @RequestMapping(value = "/compareAndMath", method = RequestMethod.POST)
    public ResultBean compareAndMath(@RequestBody @Valid GoodsLifetimeCompareAndMatchForm mr) {
        GoodsLifetime bean = baseService.getObject(beanClass,mr.getLifetimeId());
        float m = nlpProcessor.sentenceSimilarity(bean.getTags(),mr.getGoodsName());
        GoodsLifetimeMatchBean match = new GoodsLifetimeMatchBean();
        BeanCopy.copyProperties(bean,match);
        match.setMatch(m);
        return callback(match);
    }
}
