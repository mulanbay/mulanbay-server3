package cn.mulanbay.pms.web.controller.consume;

import cn.mulanbay.ai.nlp.processor.NLPProcessor;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.NullType;
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
import cn.mulanbay.pms.persistent.dto.consume.*;
import cn.mulanbay.pms.persistent.enums.ChartType;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.persistent.service.ConsumeService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.pms.web.bean.req.consume.consume.*;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.chart.*;
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
    @RequestMapping(value = "/tagsTree")
    public ResultBean tagsTree(ConsumeTagSH sf) {
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
        treeData.setSubTitle("商品总价:"+NumberUtil.getValue(consume.getTotalPrice(),SCALE)+"元,总成本:"+NumberUtil.getValue(totalCost.doubleValue(),2)+"元");
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
            ConsumeCascadeDTO parent = map.get(child.getPid());
            String parentName = parent.getGoodsName();
            if (root.getName().equals(parentName)) {
                root.addChild(NumberUtil.getValue(child.getTotalPrice().doubleValue(),SCALE), child.getGoodsName(),false);
            }
        }
        if (root.getChildren() != null) {
            for (ChartTreeDetailData cc : root.getChildren()) {
                generateTree(cc,map, list);
            }
        }
        return root;
    }

    /**
     * 商品使用寿命
     *
     * @return
     */
    @RequestMapping(value = "/useTimeList")
    public ResultBean useTimeList(ConsumeUseTimeListSH sf) {
        sf.setInvalidTimeType(NullType.NOT_NULL);
        PageRequest req = sf.buildQuery();
        req.setBeanClass(beanClass);
        if (StringUtil.isEmpty(sf.getSortField())) {
            req.addSort(new Sort("invalidTime", Sort.DESC));
        } else {
            req.addSort(new Sort(sf.getSortField(), sf.getSortType()));
        }
        PageResult<Consume> res = baseService.getBeanResult(req);
        return callbackDataGrid(res);
    }

    /**
     * 商品使用寿命
     *
     * @return
     */
    @RequestMapping(value = "/useTimeStat")
    public ResultBean useTimeStat(ConsumeUseTimeStatSH sf) {
        sf.setInvalidTmeType(NullType.NOT_NULL);
        List<ConsumeUseTimeStat> list = consumeService.getUseTimeStat(sf);
        Collections.sort(list, (o1, o2) -> {
            //按照平均使用时间排序
            Long t1 = o1.getTotalDuration().longValue()/o1.getTotalCount();
            Long t2 = o2.getTotalDuration().longValue()/o2.getTotalCount();
            return t2.compareTo(t1);
        });

        ChartData chartData = new ChartData();
        chartData.setTitle("商品使用时间分析");
        //混合图形下使用
        chartData.addYAxis("天数","天");
        chartData.addYAxis("次数","次");
        chartData.setLegendData(new String[]{"平均寿命","次数"});
        ChartYData yData1 = new ChartYData("平均寿命","天");
        ChartYData yData2 = new ChartYData("次数","次");
        for (ConsumeUseTimeStat bean : list) {
            chartData.getXdata().add(bean.getName().toString());
            BigDecimal days = bean.getTotalDuration().divide(new BigDecimal(bean.getTotalCount()),SCALE, ROUNDING_MODE);
            days = days.divide(new BigDecimal(24*3600*1000L),SCALE, ROUNDING_MODE);
            yData1.getData().add(days);
            yData2.getData().add(bean.getTotalCount());
        }
        chartData.getYdata().add(yData1);
        chartData.getYdata().add(yData2);
        return callback(chartData);
    }

    /**
     * 统计分析
     *
     * @return
     */
    @RequestMapping(value = "/analyseStat")
    public ResultBean analyseStat(ConsumeAnalyseStatSH sf) {
        if (sf.getChartType() == ChartType.BAR) {
            List<ConsumeRealTimeStat> list = consumeService.getAnalyseStat(sf);
            return callback(this.createAnalyseStatBarData(list, sf));
        } else if (sf.getChartType() == ChartType.PIE) {
            List<ConsumeRealTimeStat> list = consumeService.getAnalyseStat(sf);
            return callback(this.createAnalyseStatPieData(list, sf));
        } else if (sf.getChartType() == ChartType.TREE_MAP) {
            //只有按照商品子类型的才能
            if (!ConsumeService.GroupField.GOODS_TYPE.equals(sf.getGroupField())) {
                return callbackErrorInfo("只有按照商品类型分组的才支持该分析图型");
            }
            List<ConsumeRealTimeTreeStat> list = consumeService.getAnalyseTreeStat(sf);
            return callback(this.createAnalyseStatTreeMapData(list, sf));
        } else {
            return callbackErrorInfo("不支持的图表类型");
        }
    }

    /**
     * 封装消费记录分析的树形图数据
     *
     * @param list
     * @param sf
     * @return
     */
    private ChartTreeMapData createAnalyseStatTreeMapData(List<ConsumeRealTimeTreeStat> list, ConsumeAnalyseStatSH sf) {
        ChartTreeMapData chartData = new ChartTreeMapData();
        chartData.setTitle("消费分析");
        chartData.setName("消费");
        if (sf.getType() == GroupType.COUNT) {
            chartData.setUnit("次");
        } else {
            chartData.setUnit("元");
        }
        BigDecimal totalValue = new BigDecimal(0);
        Map<Long, ChartTreeMapDetailData> dataMap = new HashMap<>();
        for (ConsumeRealTimeTreeStat bean : list) {
            totalValue = totalValue.add(bean.getValue());
            ChartTreeMapDetailData mdd = dataMap.get(bean.getParentGoodsTypeId());
            //只有两层结构
            if (mdd == null) {
                mdd = new ChartTreeMapDetailData(NumberUtil.getValue(bean.getValue(),SCALE), bean.getGoodsName(), bean.getGoodsName());
                dataMap.put(bean.getParentGoodsTypeId(), mdd);
            }
            ChartTreeMapDetailData child = new ChartTreeMapDetailData(NumberUtil.getValue(bean.getValue(),SCALE),
                    bean.getGoodsName(), bean.getParentGoodsTypeName() + "/" + bean.getGoodsName());
            mdd.addChild(child);
        }
        chartData.setData(new ArrayList<>(dataMap.values()));
        String subTitle = this.getDateTitle(sf)+",总计"+getSubTitlePostfix(sf.getType(), totalValue);
        chartData.setSubTitle(subTitle);
        return chartData;

    }

    /**
     * 封装消费分析的饼状图数据
     *
     * @param list
     * @param sf
     * @return
     */
    private ChartPieData createAnalyseStatPieData(List<ConsumeRealTimeStat> list, ConsumeAnalyseStatSH sf) {
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("消费分析");
        chartPieData.setUnit(sf.getType().getUnit());
        ChartPieSerieData seriesData = new ChartPieSerieData();
        seriesData.setName(sf.getType().getName());
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        for (ConsumeRealTimeStat bean : list) {
            chartPieData.getXdata().add(bean.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(bean.getName());
            dataDetail.setValue(bean.getValue());
            seriesData.getData().add(dataDetail);
            totalValue = totalValue.add(bean.getValue());
        }
        String subTitle = this.getDateTitle(sf)+",总计"+getSubTitlePostfix(sf.getType(), totalValue);
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(seriesData);
        return chartPieData;
    }


    /**
     * 封装消费记录分析的柱状图数据
     *
     * @param list
     * @param sf
     * @return
     */
    private ChartData createAnalyseStatBarData(List<ConsumeRealTimeStat> list, ConsumeAnalyseStatSH sf) {
        ChartData chartData = new ChartData();
        chartData.setTitle("消费分析");
        chartData.setUnit(sf.getType().getUnit());
        chartData.setLegendData(new String[]{sf.getType().getName()});
        ChartYData yData = new ChartYData();
        yData.setName(sf.getType().getName());
        BigDecimal totalValue = new BigDecimal(0);
        for (ConsumeRealTimeStat bean : list) {
            chartData.getXdata().add(bean.getName());
            yData.getData().add(bean.getValue());
            totalValue = totalValue.add(bean.getValue());
        }
        String subTitle = this.getDateTitle(sf)+",总计"+getSubTitlePostfix(sf.getType(),totalValue);
        chartData.setSubTitle(subTitle);
        chartData.getYdata().add(yData);
        return chartData;

    }


    /**
     * 获取子标题后缀
     *
     * @param groupType
     * @param totalValue
     * @return
     */
    private String getSubTitlePostfix(GroupType groupType, BigDecimal totalValue) {
        if (groupType == GroupType.COUNT) {
            return NumberUtil.getValue(totalValue,0) + "次";
        } else {
            return NumberUtil.getValue(totalValue,SCALE) + "元";
        }
    }

    /**
     * 按照日期统计
     *
     * @return
     */
    @RequestMapping(value = "/dateStat")
    public ResultBean dateStat(ConsumeDateStatSH sf) {
        switch (sf.getDateGroupType()){
            case DAYCALENDAR :
                //日历
                List<ConsumeDateStat> list = consumeService.getDateStat(sf);
                return callback(ChartUtil.createChartCalendarData("消费统计", "次数", "次", sf, list));
            case HOURMINUTE :
                //散点图
                PageRequest pr = sf.buildQuery();
                pr.setBeanClass(beanClass);
                List<Date> dateList = consumeService.getDateList(sf);
                return callback(ChartUtil.createHMChartData(dateList,"消费分析","消费时间点"));
            default:
                break;
        }
        ChartData chartData = new ChartData();
        chartData.setTitle("消费统计");
        chartData.setSubTitle(this.getDateTitle(sf));
        chartData.setLegendData(new String[]{"消费","次数"});
        //混合图形下使用(最后一组数据默认为次数)
        chartData.addYAxis("消费","元");
        chartData.addYAxis("次数","次");
        ChartYData yData1 = new ChartYData("次数","次");
        ChartYData yData2 = new ChartYData("消费","元");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        //总的值
        BigDecimal totalCount = new BigDecimal(0);
        List<ConsumeDateStat> list = consumeService.getDateStat(sf);
        for (ConsumeDateStat bean : list) {
            chartData.addXData(bean, sf.getDateGroupType());
            yData1.getData().add(bean.getTotalCount());
            yData2.getData().add(bean.getTotalPrice());
            totalCount = totalCount.add(new BigDecimal(bean.getTotalCount()));
            totalValue = totalValue.add(bean.getTotalPrice());
        }
        chartData.getYdata().add(yData2);
        chartData.getYdata().add(yData1);
        String subTitle = this.getDateTitle(sf)+",总计"+totalCount.longValue() + "次，" + totalValue.doubleValue() + "元";
        chartData.setSubTitle(subTitle);
        chartData = ChartUtil.completeDate(chartData, sf);
        return callback(chartData);
    }

    /**
     * 同期比对统计
     *
     * @return
     */
    @RequestMapping(value = "/yoyStat")
    public ResultBean yoyStat(@Valid ConsumeYoyStatSH sf) {
        if (sf.getDateGroupType() == DateGroupType.DAY) {
            return callback(createChartCalendarMultiData(sf));
        }
        String unit = sf.getGroupType().getUnit();
        ChartData chartData = initYoyCharData(sf, "消费统计同期对比", null);
        chartData.setUnit(unit);
        String[] legendData = new String[sf.getYears().size()];
        for (int i = 0; i < sf.getYears().size(); i++) {
            legendData[i] = sf.getYears().get(i).toString();
            //数据,为了代码复用及统一，统计还是按照日期的统计
            ConsumeDateStatSH dateSearch = generateSearch(sf.getYears().get(i), sf);
            ChartYData yData = new ChartYData();
            yData.setName(sf.getYears().get(i).toString());
            yData.setUnit(unit);
            List<ConsumeDateStat> list = consumeService.getDateStat(dateSearch);
            //临时内容，作为补全用
            ChartData temp = new ChartData();
            for (ConsumeDateStat bean : list) {
                temp.addXData(bean, sf.getDateGroupType());
                if (sf.getGroupType() == GroupType.COUNT) {
                    yData.getData().add(bean.getTotalCount());
                } else {
                    yData.getData().add(bean.getTotalPrice());
                }
            }
            //临时内容，作为补全用
            temp.getYdata().add(yData);
            dateSearch.setCompleteDate(true);
            temp = ChartUtil.completeDate(temp, dateSearch);
            //设置到最终的结果集中
            chartData.getYdata().add(temp.getYdata().get(0));
        }
        chartData.setLegendData(legendData);

        return callback(chartData);
    }

    private ConsumeDateStatSH generateSearch(int year, ConsumeYoyStatSH sf) {
        ConsumeDateStatSH dateSearch = new ConsumeDateStatSH();
        dateSearch.setDateGroupType(sf.getDateGroupType());
        dateSearch.setStartDate(DateUtil.getDate(year + "-01-01", DateUtil.FormatDay1));
        dateSearch.setEndDate(DateUtil.getDate(year + "-12-31", DateUtil.FormatDay1));
        dateSearch.setUserId(sf.getUserId());
        dateSearch.setStartTotalPrice(sf.getStartTotalPrice());
        dateSearch.setEndTotalPrice(sf.getEndTotalPrice());
        dateSearch.setGroupType(GroupType.TOTALPRICE);
        dateSearch.setSourceId(sf.getSourceId());
        dateSearch.setGoodsTypeId(sf.getGoodsTypeId());
        dateSearch.setConsumeType(sf.getConsumeType());
        dateSearch.setSecondhand(sf.getSecondhand());
        return dateSearch;
    }

    /**
     * 基于日历的热点图
     *
     * @param sf
     * @return
     */
    private ChartCalendarMultiData createChartCalendarMultiData(ConsumeYoyStatSH sf) {
        ChartCalendarMultiData data = new ChartCalendarMultiData();
        data.setTitle("消费统计同期对比");
        if (sf.getGroupType() == GroupType.COUNT) {
            data.setUnit("次");
        } else {
            data.setUnit("元");
        }
        for (int i = 0; i < sf.getYears().size(); i++) {
            ConsumeDateStatSH dateSearch = generateSearch(sf.getYears().get(i), sf);
            List<ConsumeDateStat> list = consumeService.getDateStat(dateSearch);
            for (ConsumeDateStat bean : list) {
                String dateString = DateUtil.getFormatDateString(bean.getDateIndexValue().toString(), "yyyyMMdd", "yyyy-MM-dd");
                if (sf.getGroupType() == GroupType.COUNT) {
                    data.addData(sf.getYears().get(i), dateString, bean.getTotalCount());
                } else {
                    data.addData(sf.getYears().get(i), dateString, bean.getTotalPrice());
                }
            }
        }
        return data;
    }

    /**
     * 根据标签统计
     *
     * @return
     */
    @RequestMapping(value = "/tagsStat")
    public ResultBean tagsStat(@Valid ConsumeTagsStatSH sf) {
        List<ConsumeTagsStat> list = consumeService.getTagsStat(sf);
        ChartData chartData = new ChartData();
        chartData.setTitle("标签统计");
        chartData.setSubTitle(this.getDateTitle(sf));
        chartData.setLegendData(new String[]{"消费","次数"});
        //混合图形下使用
        chartData.addYAxis("消费","元");
        chartData.addYAxis("次数","次");
        ChartYData yData1 = new ChartYData("次数","次");
        ChartYData yData2 = new ChartYData("消费","元");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        //总的值
        BigDecimal totalCount = new BigDecimal(0);
        for (ConsumeTagsStat bean : list) {
            chartData.getXdata().add(bean.getTags());
            yData1.getData().add(bean.getTotalCount());
            yData2.getData().add(bean.getTotalPrice());
            totalCount = totalCount.add(new BigDecimal(bean.getTotalCount().longValue()));
            totalValue = totalValue.add(bean.getTotalPrice());
        }
        chartData.getYdata().add(yData2);
        chartData.getYdata().add(yData1);
        String subTitle = this.getDateTitle(sf)+",总计"+totalCount.longValue() + "次，" + totalValue.doubleValue() + "元";
        chartData.setSubTitle(subTitle);
        chartData = ChartUtil.completeDate(chartData, sf);
        return callback(chartData);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/tagsDetailStat", method = RequestMethod.GET)
    public ResultBean tagsDetailStat(ConsumeAnalyseStatSH basf) {
        basf.setType(GroupType.TOTALPRICE);
        List<ConsumeRealTimeStat> list = consumeService.getAnalyseStat(basf);
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("[" + basf.getTags() + "]的消费分析");
        chartPieData.setUnit("元");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("费用");
        //总的值
        BigDecimal totalValue = new BigDecimal((0));
        for (ConsumeRealTimeStat bean : list) {
            chartPieData.getXdata().add(bean.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(bean.getName());
            dataDetail.setValue(bean.getValue());
            serieData.getData().add(dataDetail);
            totalValue.add(bean.getValue());
        }
        String subTitle = "花费总金额:" + NumberUtil.getValue( totalValue,SCALE) + "元";
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(serieData);
        return callback(chartPieData);
    }

    /**
     * 商品词云统计
     *
     * @return
     */
    @RequestMapping(value = "/wordCloudStat", method = RequestMethod.GET)
    public ResultBean wordCloudStat(@Valid ConsumeWordCloudSH sf) {
        List<String> list = consumeService.getWordCloudStat(sf);
        Map<String,Integer> statData = new HashMap<>();
        String field = sf.getField();
        for (String d : list) {
            if("goodsName".equals(field)||"sku".equals(field)){
                //先分词
                List<String> keywords = nlpProcessor.extractKeyword(d,tagNum);
                for(String s : keywords){
                    //忽略分词后为词长度为1的
                    if(sf.getIgnoreShort()!=null&&sf.getIgnoreShort()){
                        if(s.length()<2){
                            continue;
                        }
                    }
                    Integer n = statData.get(s);
                    if(n==null){
                        statData.put(s,1);
                    }else{
                        statData.put(s,n+1);
                    }
                }
            }else if("shopName".equals(field)||"brand".equals(field)){
                Integer n = statData.get(d);
                if(n==null){
                    statData.put(d,1);
                }else{
                    statData.put(d,n+1);
                }
            }else if("tags".equals(field)){
                String[] keywords = d.split(",");
                for(String s : keywords){
                    Integer n = statData.get(s);
                    if(n==null){
                        statData.put(s,1);
                    }else{
                        statData.put(s,n+1);
                    }
                }
            }

        }

        ChartWorldCloudData chartData = new ChartWorldCloudData();
        for(String key : statData.keySet()){
            ChartNameValueVo dd = new ChartNameValueVo();
            dd.setName(key);
            dd.setValue(statData.get(key));
            chartData.addData(dd);
        }
        chartData.setTitle("消费词云");
        return callback(chartData);
    }

}
