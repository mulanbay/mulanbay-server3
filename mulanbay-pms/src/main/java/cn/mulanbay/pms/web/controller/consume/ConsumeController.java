package cn.mulanbay.pms.web.controller.consume;

import cn.mulanbay.ai.nlp.processor.NLPProcessor;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.handler.ConsumeHandler;
import cn.mulanbay.pms.handler.UserHandler;
import cn.mulanbay.pms.handler.bean.consume.ConsumeMatchBean;
import cn.mulanbay.pms.persistent.domain.Consume;
import cn.mulanbay.pms.persistent.domain.ConsumeSource;
import cn.mulanbay.pms.persistent.domain.GoodsType;
import cn.mulanbay.pms.persistent.domain.UserSet;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.persistent.service.ConsumeService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.consume.consume.*;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购买记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/consume")
public class ConsumeController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ConsumeController.class);

    private static Class<Consume> beanClass = Consume.class;

    @Value("${mulanbay.consume.tag.statDays:14}")
    int tagDays;

    @Value("${mulanbay.consume.tag.num:5}")
    int tagNum;

    @Autowired
    ConsumeService consumeService;

    @Autowired
    AuthService authService;

    @Autowired
    ConsumeHandler consumeHandler;

    @Autowired
    UserHandler userHandler;

    @Autowired
    NLPProcessor nlpProcessor;

    /**
     * 关键字列表
     *
     * @return
     */
    @RequestMapping(value = "/tagTree")
    public ResultBean tagTree(ConsumeTagSH sf) {
        if(sf.getStartDate()==null&&sf.getEndDate()==null){
            Date end = new Date();
            Date start = DateUtil.getDate(-tagDays);
            sf.setStartDate(start);
            sf.setEndDate(end);
        }
        List<String> tagList = consumeService.getTagsList(sf);
        //去重
        List<String> newList = tagList.stream().distinct().collect(Collectors.toList());
        List<TreeBean> list = new ArrayList();
        for (String s : newList) {
            TreeBean tb = new TreeBean();
            tb.setId(s);
            tb.setText(s);
            list.add(tb);
        }
        return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(ConsumeSH sf) {
        PageRequest pr = sf.buildQuery();
        if (StringUtil.isEmpty(sf.getSortField())) {
            Sort s = new Sort("buyTime", Sort.DESC);
            pr.addSort(s);
        } else {
            Sort s = new Sort(sf.getSortField(), sf.getSortType());
            pr.addSort(s);
        }
        pr.setBeanClass(beanClass);
        PageResult<Consume> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid ConsumeForm form) {
        Consume bean = new Consume();
        changeFormToBean(form, bean);
        bean.setCreatedTime(new Date());
        consumeService.saveConsume(bean);
        consumeHandler.addToCache(bean);
        return callback(null);
    }

    /**
     * 转换
     * @param form
     * @param bean
     */
    private void changeFormToBean(ConsumeForm form, Consume bean) {
        BeanCopy.copy(form, bean);
        bean.setTotalPrice(bean.getPrice().multiply(new BigDecimal(bean.getAmount()).add(bean.getShipment())));
        ConsumeSource source = baseService.getObject(ConsumeSource.class,form.getSourceId());
        bean.setSource(source);
        GoodsType goodsType = baseService.getObject(GoodsType.class,form.getGoodsTypeId());
        bean.setGoodsType(goodsType);
        //消费日期默认为购买日期
        if (bean.getConsumeTime() == null) {
            bean.setConsumeTime(bean.getBuyTime());
        }
        //设置使用时长
        if(bean.getInvalidTime()!=null){
            long usedTime = bean.getInvalidTime().getTime()-bean.getBuyTime().getTime();
            bean.setDuration(usedTime);
        }
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid ConsumeForm form) {
        Consume consume = baseService.getObject(beanClass,form.getConsumeId());
        changeFormToBean(form, consume);
        consume.setModifyTime(new Date());
        consumeService.updateConsume(consume);
        //lifeExperienceService.updateLifeExperienceConsumeByBuyRecord(buyRecord);
        return callback(null);
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "consumeId") Long consumeId) {
        Consume consume = baseService.getObject(beanClass,consumeId);
        return callback(consume);
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
     * 设置上级
     *
     * @return
     */
    @RequestMapping(value = "/setParent", method = RequestMethod.POST)
    public ResultBean setParent(@RequestBody @Valid ConsumeParentForm spr) {
        consumeService.setParent(spr.getConsumeId(),spr.getPid());
        return callback(null);
    }

    /**
     * 取消上级
     *
     * @return
     */
    @RequestMapping(value = "/deleteParent", method = RequestMethod.POST)
    public ResultBean deleteParent(@RequestBody @Valid ConsumeParentForm spr) {
        consumeService.deleteParent(spr.getConsumeId());
        return callback(null);
    }

    /**
     * 取消下级
     *
     * @return
     */
    @RequestMapping(value = "/deleteChildren", method = RequestMethod.POST)
    public ResultBean deleteChildren(@RequestBody @Valid ConsumeDeleteChildrenForm dcr) {
        consumeService.deleteChildren(dcr.getPid());
        return callback(null);
    }

    /**
     * 根据商品名智能分析出其分类及品牌等
     * 如果没有配置NLP直接返回空对象
     * @return
     */
    @RequestMapping(value = "/aiMatch", method = RequestMethod.POST)
    public ResultBean aiMatch(@RequestBody @Valid GoodsNameAiMatchForm mr) {
        ConsumeMatchBean bean = null;
        try {
            bean = consumeHandler.match(mr.getUserId(),mr.getGoodsName());
            if(bean==null){
                bean = new ConsumeMatchBean();
            }
            List<String> keywords = nlpProcessor.extractKeyword(mr.getGoodsName(),tagNum);
            bean.setTags(keywords);
            if(bean.getCompareId()==null){
                //说明没有匹配,设置默认的配置
                UserSet us = userHandler.getUserSet(mr.getUserId());
                bean.setSourceId(us.getBuyTypeId());
                bean.setPayment(us.getPayment());
            }
        } catch (Exception e) {
            logger.error("根据商品名智能分析出其分类及品牌等异常",e);
            return callback(null);
        }
        return callback(bean);
    }
}
