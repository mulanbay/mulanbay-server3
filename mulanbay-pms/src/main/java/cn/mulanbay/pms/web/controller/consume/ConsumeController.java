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
import cn.mulanbay.pms.persistent.dto.consume.ConsumeCascadeDTO;
import cn.mulanbay.pms.persistent.dto.consume.ConsumeChildrenCostStat;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.persistent.service.ConsumeService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.consume.consume.*;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.chart.ChartTreeData;
import cn.mulanbay.pms.web.bean.res.chart.ChartTreeDetailData;
import cn.mulanbay.pms.web.bean.res.consume.consume.ConsumeCostStatVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static cn.mulanbay.pms.common.Constant.ROUNDING_MODE;
import static cn.mulanbay.pms.common.Constant.SCALE;

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
        String traceId = form.getTraceId();
        if(StringUtil.isNotEmpty(traceId)){
            consumeHandler.addMatchLog(traceId,bean.getConsumeId(),bean.getUserId());
        }
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
            }else{
                //匹配到的才追踪记录
                String traceId = StringUtil.genUUID();
                bean.setTraceId(traceId);
                consumeHandler.traceMatch(traceId,bean);
            }
            List<String> keywords = nlpProcessor.extractKeyword(mr.getGoodsName(),tagNum);
            String tags= keywords.stream().map(String::valueOf).collect(Collectors.joining(","));
            bean.setTags(tags);
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

    /**
     * 成本统计
     *
     * @return
     */
    @RequestMapping(value = "/costStat")
    public ResultBean costStat(ConsumeCostStatForm form) {
        Consume consume = baseService.getObject(beanClass,form.getConsumeId());
        ConsumeCostStatVo vo = new ConsumeCostStatVo();
        BeanCopy.copyProperties(consume,vo);
        Date expDate = vo.getInvalidTime();
        if(expDate==null){
            expDate = new Date();
        }else{
            long expMillSecs = expDate.getTime()-vo.getBuyTime().getTime();
            vo.setExpMillSecs(expMillSecs);
        }
        long usedMillSecs = expDate.getTime()-vo.getBuyTime().getTime();
        vo.setUsedMillSecs(usedMillSecs);
        long usedDays = usedMillSecs / (24*3600*1000);
        if(usedDays<=0){
            usedDays=1;
        }
        //计算下一级
        boolean deepCost = form.getDeepCost();
        ConsumeChildrenCostStat cc = null;
        if(deepCost==true){
            cc = consumeService.getChildrenTotalDeepCost(form.getConsumeId());
        }else{
            cc = consumeService.getChildrenTotalCost(form.getConsumeId());
        }
        Long childrens = cc.getTotalCount()==null ? null: cc.getTotalCount().longValue();
        vo.setChildrens(childrens);
        if(cc.getSoldPrice()!=null){
            vo.setChildrenSoldPrice(cc.getSoldPrice());
        }
        BigDecimal ctp = cc.getTotalPrice()==null ? new BigDecimal(0) : cc.getTotalPrice();
        vo.setChildrenPrice(ctp);
        //总成本=商品价格+下一级商品成本
        BigDecimal totalCost = ctp.add(vo.getTotalPrice());
        vo.setTotalCost(totalCost);
        //计算每天花费
        BigDecimal vs = vo.getSoldPrice()==null ? new BigDecimal(0) : vo.getSoldPrice();
        BigDecimal cts = cc.getSoldPrice()==null ? new BigDecimal(0) : cc.getSoldPrice();
        // (买入价格-出售价格)/使用天数
        BigDecimal costPerDay = (vo.getTotalPrice().subtract(vs)).divide(new BigDecimal(usedDays),SCALE, ROUNDING_MODE);
        // (买入价格-出售价格+下一级商品成本-下一级商品出售价格)/使用天数
        BigDecimal totalCostPerDay = (vo.getTotalPrice().subtract(vs).add(ctp).subtract(cts)).divide(new BigDecimal(usedDays),SCALE, ROUNDING_MODE);
        vo.setCostPerDay(costPerDay);
        vo.setTotalCostPerDay(totalCostPerDay);
        if(vo.getSoldPrice()!=null){
            //折旧率
            // 买入价格/出售价格
            BigDecimal depRate = vo.getSoldPrice().multiply(new BigDecimal(10)).divide(vo.getTotalPrice(),SCALE, ROUNDING_MODE);
            // 买入价格/(总成本-下一级商品出售价格)
            BigDecimal totalDepRate = vo.getSoldPrice().multiply(new BigDecimal(10)).divide(totalCost.subtract(cts),SCALE, ROUNDING_MODE);
            vo.setDepRate(depRate);
            vo.setTotalDepRate(totalDepRate);
        }
        return callback(vo);
    }


    /**
     * 子集树形统计
     *
     * @return
     */
    @RequestMapping(value = "/treeStat", method = RequestMethod.GET)
    public ResultBean treeStat(@RequestParam(name = "consumeId") Long consumeId) {
        Long rootId = consumeId;
        Consume consume = baseService.getObject(beanClass,rootId);
        List<ConsumeCascadeDTO> children = consumeService.getChildrenDeepList(rootId);
        // 转换为Map
        Map<Long,ConsumeCascadeDTO> map = new HashMap<>();
        // 添加根节点信息
        ConsumeCascadeDTO root = new ConsumeCascadeDTO();
        root.setGoodsName(consume.getGoodsName());
        map.put(rootId,root);
        BigDecimal totalCost = consume.getTotalPrice();
        for (ConsumeCascadeDTO child : children){
            map.put(child.getConsumeId().longValue(),child);
            totalCost = totalCost.add(child.getTotalPrice());
        }
        ChartTreeDetailData rootData = new ChartTreeDetailData(consume.getTotalPrice().doubleValue(), consume.getGoodsName(),false);
        ChartTreeDetailData data = this.generateTree(rootData,map,children);
        ChartTreeData treeData = new ChartTreeData();
        treeData.setData(data);
        treeData.setUnit("元");
        treeData.setTitle("商品关系图");
        treeData.setSubTitle("商品总价:"+NumberUtil.getValue(consume.getTotalPrice(),2)+"元,总成本:"+NumberUtil.getValue(totalCost.doubleValue(),2)+"元");
        return callback(treeData);
    }

    /**
     * 构建树
     * @param root
     * @param map
     * @param list
     * @return
     */
    private ChartTreeDetailData generateTree(ChartTreeDetailData root,Map<Long,ConsumeCascadeDTO> map,List<ConsumeCascadeDTO> list){
        for (ConsumeCascadeDTO child : list) {
            ConsumeCascadeDTO parent = map.get(child.getPid().longValue());
            String parentName = parent.getGoodsName();
            if (root.getName().equals(parentName)) {
                root.addChild(NumberUtil.getValue(child.getTotalPrice().doubleValue(),2), child.getGoodsName(),false);
            }
        }
        if (root.getChildren() != null) {
            for (ChartTreeDetailData cc : root.getChildren()) {
                generateTree(cc,map, list);
            }
        }
        return root;
    }


}
