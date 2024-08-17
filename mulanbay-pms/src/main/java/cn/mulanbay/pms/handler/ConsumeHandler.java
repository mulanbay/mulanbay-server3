package cn.mulanbay.pms.handler;

import cn.mulanbay.ai.nlp.processor.NLPProcessor;
import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.queue.LimitQueue;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.handler.bean.consume.ConsumeCompareBean;
import cn.mulanbay.pms.handler.bean.consume.ConsumeMatchBean;
import cn.mulanbay.pms.handler.bean.consume.GoodsLifetimeMatchBean;
import cn.mulanbay.pms.persistent.domain.Consume;
import cn.mulanbay.pms.persistent.domain.GoodsLifetime;
import cn.mulanbay.pms.persistent.domain.GoodsType;
import cn.mulanbay.pms.persistent.domain.MatchLog;
import cn.mulanbay.pms.persistent.enums.GoodsMatchType;
import cn.mulanbay.pms.persistent.service.ConsumeService;
import cn.mulanbay.pms.persistent.service.ExperienceService;
import cn.mulanbay.pms.util.BeanCopy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消费处理类
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class ConsumeHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConsumeHandler.class);

    @Value("${mulanbay.consume.match.tag:3}")
    int tagNum;

    /**
     * 消费记录的缓存队列的过期时间
     */
    @Value("${mulanbay.consume.cacheQueue.expireDays}")
    int expireDays;

    /**
     * 消费记录的缓存队列的大小
     */
    @Value("${mulanbay.consume.cacheQueue.size}")
    int queueSize;

    /**
     * NLP匹配时的最高匹配度，高于这个数不再往下匹配
     */
    @Value("${mulanbay.consume.match.maxDegree}")
    float maxMatchDegree;

    /**
     * NLP匹配时的最低匹配度，低于这个数认为没有匹配到
     */
    @Value("${mulanbay.consume.match.minDegree}")
    float minMatchDegree;

    /**
     * 匹配追踪有效时长(秒)
     */
    @Value("${mulanbay.consume.match.traceExpires:180}")
    int traceExpires;

    /**
     * 商品类型的匹配列表过期时间(秒)
     */
    @Value("${mulanbay.consume.match.goodsTypeExpireTime}")
    int goodsTypeExpireTime;

    @Autowired
    BaseService baseService;

    @Autowired
    ConsumeService consumeService;

    @Autowired
    ExperienceService experienceService;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    ThreadPoolHandler threadPoolHandler;

    @Autowired
    NLPProcessor nlpProcessor;

    public ConsumeHandler() {
        super("消费处理类");
    }

    /**
     * 创建后业务处理
     * @param consume
     * @param traceId
     */
    public void afterCreate(Consume consume,String traceId){
        threadPoolHandler.pushThread(() -> {
            this.addToCache(consume);
            if(StringUtil.isNotEmpty(traceId)){
                this.addMatchLog(traceId,consume.getConsumeId(),consume.getUserId());
            }
        });
    }

    /**
     * 修改后业务处理
     * @param consume
     */
    public void afterEdit(Consume consume){
        threadPoolHandler.pushThread(() -> {
            experienceService.updateCostByConsume(consume);
        });
    }

    /**
     * 加入到缓存中
     * @param consume
     */
    public void addToCache(Consume consume){
        try {
            String key = CacheKey.getKey(CacheKey.CONSUME_CACHE_QUEUE,consume.getUserId().toString());
            LimitQueue<ConsumeCompareBean> queue = cacheHandler.get(key, LimitQueue.class);
            if (queue == null) {
                queue = new LimitQueue<ConsumeCompareBean>(queueSize);
            }
            queue.offer(this.convertConsume2Compare(consume));
            cacheHandler.set(key, queue, expireDays*24*3600);
        } catch (Exception e) {
            logger.error("添加消费记录到缓存中异常",e);
        }
    }

    /**
     * 转换
     *
     * @param consume
     * @return
     */
    private ConsumeCompareBean convertConsume2Compare(Consume consume){
        ConsumeCompareBean bean = new ConsumeCompareBean();
        BeanCopy.copy(consume,bean);
        bean.setGoodsTypeId(consume.getGoodsType().getTypeId());
        bean.setSourceId(consume.getSource().getSourceId());
        bean.setOriginName(consume.getGoodsName());
        bean.setCompareId(consume.getConsumeId());
        bean.setMatchType(GoodsMatchType.CONSUME);
        String tags = this.extractTags(consume.getGoodsName());
        bean.setTags(tags);
        //以关键字来匹配
        bean.setCompareName(tags);
        return bean;
    }

    /**
     * 从字符段落中选取关键字
     * @param name
     * @return
     */
    private String extractTags(String name){
        //获取关键字
        //List<String> keywords = nlpProcessor.extractKeyword(name,tagNum);
        List<String> keywords = nlpProcessor.nlpSegmentN(name);
        return keywords.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * 寻找匹配
     * 该方法针对已经与第三方系统的数据同步比较有意义
     * @param userId
     * @param goodsName
     * @return
     */
    public ConsumeMatchBean match(Long userId, String goodsName){
        //先选出待匹配的关键字
        String cmTags = this.extractTags(goodsName);
        //先从消费缓存中比对，看以前是否已经保存过类似的消费
        ConsumeMatchBean bean = this.matchConsume(userId,cmTags);
        bean.setGoodsName(goodsName);
        bean.setTags(cmTags);
        if(bean.getMatch()>=maxMatchDegree){
            return bean;
        }
        //没有或者匹配度很低，再从商品类别中比对
        List<ConsumeCompareBean> list  = this.loadGoodsTypeCompareList(userId);
        for(ConsumeCompareBean gcb : list){
            float m = nlpProcessor.sentenceSimilarity(cmTags,gcb.getCompareName());
            if(m>bean.getMatch()){
                bean = new ConsumeMatchBean();
                bean.setGoodsName(goodsName);
                bean.setTags(cmTags);
                bean.setGoodsTypeId(gcb.getGoodsTypeId());
                bean.setCompareId(gcb.getCompareId());
                bean.setMatch(m);
                bean.setMatchType(gcb.getMatchType());
                if(m>=maxMatchDegree){
                    return bean;
                }
            }
        }
        if(bean.getMatch()<minMatchDegree){
            logger.debug("匹配度过低,{}与{},匹配度:{}",goodsName,bean.getCompareName(),bean.getMatch());
            //return null;
        }
        return bean;
    }

    /**
     * 初始化消费列表
     * @param userId
     */
    private void initMatchConsumeList(Long userId){
        // 加载最近的100条
        List<Consume> consumeList = consumeService.getLastestCosumeList(userId,queueSize);
        if(StringUtil.isNotEmpty(consumeList)){
            LimitQueue<ConsumeCompareBean> queue = new LimitQueue<ConsumeCompareBean>(queueSize);
            for(Consume consume : consumeList){
                queue.offer(this.convertConsume2Compare(consume));
            }
            String key = CacheKey.getKey(CacheKey.CONSUME_CACHE_QUEUE,userId.toString());
            cacheHandler.set(key, queue, expireDays*24*3600);
        }
    }

    /**
     * 通过缓存中的匹配
     * @param userId
     * @param name
     * @return
     */
    private ConsumeMatchBean matchConsume(Long userId, String name){
        ConsumeMatchBean bean = new ConsumeMatchBean();
        String key = CacheKey.getKey(CacheKey.CONSUME_CACHE_QUEUE,userId.toString());
        LimitQueue<ConsumeCompareBean> queue = cacheHandler.get(key, LimitQueue.class);
        if (queue == null) {
            //this.initMatchConsumeList(userId);
            return bean;
        }else{
            for(ConsumeCompareBean ccb : queue.getList()){
                float m = nlpProcessor.sentenceSimilarity(name, ccb.getCompareName());
                if(m>bean.getMatch()){
                    BeanCopy.copy(ccb,bean);
                    bean.setMatch(m);
                    if(m>=maxMatchDegree){
                        logger.debug("在历史消费记录中匹配到，goodsName:"+ccb.getOriginName());
                        return bean;
                    }
                }
            }
        }
        return bean;
    }

    /**
     * 获取商品类型的匹配列表
     * @param userId
     * @return
     */
    private List<ConsumeCompareBean> loadGoodsTypeCompareList(Long userId){
        String key = CacheKey.getKey(CacheKey.GOODS_TYPE_MATCH_LIST,userId.toString());
        List<ConsumeCompareBean> list = cacheHandler.get(key,List.class);
        if(StringUtil.isEmpty(list)){
            list = new ArrayList<>();
            List<GoodsType> goodsTypeList = consumeService.getGoodsTypeList(userId);
            for(GoodsType gt : goodsTypeList){
                ConsumeCompareBean mb = new ConsumeCompareBean();
                mb.setGoodsTypeId(gt.getTypeId());
                String compareName = gt.getTypeName();
                if(StringUtil.isNotEmpty(gt.getTags())){
                    compareName+=","+gt.getTags();
                }
                mb.setCompareName(compareName);
                mb.setCompareId(gt.getTypeId());
                mb.setOriginName(gt.getTypeName());
                mb.setMatchType(GoodsMatchType.GOODS_TYPE);
                mb.setTags(gt.getTags());
                list.add(mb);
            }
            cacheHandler.set(key, list, goodsTypeExpireTime);
        }
        return list;
    }

    /**
     * 商品寿命匹配
     * @param goodsName
     * @return
     */
    public GoodsLifetimeMatchBean matchLifetime(String goodsName){
        List<GoodsLifetime> list = this.getGoodsLifetimeList();
        return this.matchLifetime(goodsName,list,true);
    }

    /**
     * 商品寿命匹配
     * @param goodsName
     * @return
     */
    public GoodsLifetimeMatchBean matchLifetime(String goodsName,List<GoodsLifetime> list,boolean skipMin){
        String cmTags = this.extractTags(goodsName);
        GoodsLifetimeMatchBean bean = new GoodsLifetimeMatchBean();
        for(GoodsLifetime lt : list){
            float m = nlpProcessor.sentenceSimilarity(cmTags,lt.getTags());
            if(m>bean.getMatch()){
                BeanCopy.copy(lt,bean);
                bean.setMatch(m);
                if(m>=maxMatchDegree){
                    logger.debug("快速寻找到商品寿命匹配:"+lt.getLifetimeName());
                    return bean;
                }
            }
        }
        if(skipMin&&bean.getMatch()<minMatchDegree){
            logger.warn("商品寿命匹配度过低:"+bean.getMatch());
            return null;
        }
        return bean;
    }

    /**
     * 获取商品寿命配置列表
     * @return
     */
    private List<GoodsLifetime> getGoodsLifetimeList(){
        String key = CacheKey.getKey(CacheKey.GOODS_LIFETIME_LIST);
        List<GoodsLifetime> list = cacheHandler.get(key,List.class);
        if(StringUtil.isEmpty(list)){
            //从数据库加载
            list = baseService.getBeanList(GoodsLifetime.class,null);
            if(list.size()>0){
                cacheHandler.set(key, list, goodsTypeExpireTime);
            }
        }
        return list;
    }

    /**
     * 追踪匹配
     * @param traceId
     * @param bean
     */
    public void traceMatch(String traceId,ConsumeMatchBean bean){
        String key = CacheKey.getKey(CacheKey.CONSUME_MATCH_TRACE,traceId);
        cacheHandler.set(key,bean,traceExpires);
    }

    /**
     * 增加匹配记录
     * @param traceId
     * @param consumeId
     * @param userId
     */
    public void addMatchLog(String traceId,Long consumeId,Long userId){
        try {
            String key = CacheKey.getKey(CacheKey.CONSUME_MATCH_TRACE,traceId);
            ConsumeMatchBean form = cacheHandler.get(key,ConsumeMatchBean.class);
            MatchLog bean = new MatchLog();
            bean.setConsumeId(consumeId);
            bean.setAiMatch(form.getMatch());
            bean.setUserId(userId);
            bean.setMatchType(form.getMatchType());
            bean.setCompareId(form.getCompareId());
            //计算实际匹配度
            Consume consume = baseService.getObject(Consume.class,consumeId);
            bean.setGoodsName(consume.getGoodsName());
            bean.setConsumeData(JsonUtil.beanToJson(consume));
            int m=0;
            int total=1;
            GoodsMatchType matchType = form.getMatchType();
            if(matchType==GoodsMatchType.CONSUME){
                Consume compareBean = baseService.getObject(Consume.class,form.getCompareId());
                if(consume.getGoodsType().getTypeId().equals(compareBean.getGoodsType().getTypeId())){
                    m++;
                }
                if(consume.getSource().getSourceId().equals(compareBean.getSource().getSourceId())){
                    m++;
                }
                if(consume.getShopName().equals(compareBean.getShopName())){
                    m++;
                }
                total=3;
                bean.setCompareData(JsonUtil.beanToJson(compareBean));
            }else{
                GoodsType compareBean = baseService.getObject(GoodsType.class,form.getCompareId());
                if(consume.getGoodsType().getTypeId().equals(compareBean.getTypeId())){
                    m++;
                }
                bean.setCompareData(JsonUtil.beanToJson(compareBean));
            }
            float acMatch = (float) NumberUtil.getPercent(m,total,4)/100;
            bean.setAcMatch(acMatch);
            baseService.saveObject(bean);
        } catch (BeansException e) {
            logger.error("增加消费记录匹配日志异常",e);
        }
    }
}
