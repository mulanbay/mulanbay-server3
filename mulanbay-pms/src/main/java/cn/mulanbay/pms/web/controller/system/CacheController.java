package cn.mulanbay.pms.web.controller.system;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.web.bean.req.main.UserCommonForm;
import cn.mulanbay.pms.web.bean.req.system.cache.DeleteCacheForm;
import cn.mulanbay.pms.web.bean.res.chart.ChartPieData;
import cn.mulanbay.pms.web.bean.res.chart.ChartPieSerieData;
import cn.mulanbay.pms.web.bean.res.chart.ChartPieSerieDetailData;
import cn.mulanbay.pms.web.bean.res.system.cache.CacheVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 缓存
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/cache")
public class CacheController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    /**
     * 前缀
     */
    @Value("${mulanbay.persistent.page.cache.keyPrefix:pageSearch}")
    private String pageCacheKeyPrefix;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    private RedisTemplate redisTemplate;

    private static List<CacheVo> caches;

    @PostConstruct
    public void init(){
        logger.info("init CacheVo");
        caches = new ArrayList<CacheVo>();
        caches.add(new CacheVo(cacheHandler.getFullKey(CacheKey.getKey(CacheKey.LOGIN_TOKEN_KEY, "*")), "用户信息"));
        caches.add(new CacheVo(cacheHandler.getFullKey(CacheKey.getKey(CacheKey.USER_LOGIN_FAIL, "*")), "用户登录失败"));
        caches.add(new CacheVo(cacheHandler.getFullKey(CacheKey.getKey(CacheKey.USER_CODE_LIMIT, "*","*")), "系统代码限流"));
        //caches.add(new CacheVo(cacheHandler.getFullKey(CacheKey.SYS_FUNC), "功能点"));
        caches.add(new CacheVo(cacheHandler.getFullKey(CacheKey.ROLE_FUNC), "角色功能"));
        caches.add(new CacheVo(cacheHandler.getFullKey(CacheKey.getKey(CacheKey.SYS_CODE_COUNTS, "*")), "系统代码计数"));
        caches.add(new CacheVo(cacheHandler.getFullKey(pageCacheKeyPrefix+":*"), "查询列表缓存"));
    }

    /**
     * 缓存信息
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResultBean info() throws Exception {
        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info());
        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info("commandstats"));
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) connection -> connection.dbSize());

        Map<String, Object> result = new HashMap<>(3);
        result.put("info", info);
        result.put("dbSize", dbSize);

        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("命令统计");
        chartPieData.setUnit("个");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("命令");
        commandStats.stringPropertyNames().forEach(key -> {
            Map<String, String> data = new HashMap<>(2);
            String property = commandStats.getProperty(key);
            String name = StringUtils.removeStart(key, "cmdstat_");
            String value = StringUtils.substringBetween(property, "calls=", ",usec");
            chartPieData.getXdata().add(name);
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(name);
            dataDetail.setValue(value);
            serieData.getData().add(dataDetail);
        });
        chartPieData.getDetailData().add(serieData);
        result.put("commandStats", chartPieData);
        return callback(result);
    }

    /**
     * 名称列表
     *
     * @return
     */
    @RequestMapping(value = "/names", method = RequestMethod.GET)
    public ResultBean names() {
        return callback(caches);
    }

    @RequestMapping(value = "/getCacheKeys", method = RequestMethod.GET)
    public ResultBean getCacheKeys(@RequestParam(name = "cacheName") String cacheName) {
        Set<String> cacheKeys = cacheHandler.keys(cacheName);
        return callback(cacheKeys);
    }

    @RequestMapping(value = "/getCacheValue", method = RequestMethod.GET)
    public ResultBean getCacheValue(@RequestParam(name = "cacheName") String cacheName, @RequestParam(name = "cacheKey") String cacheKey) {
        Object cacheValue = null;
        if (cacheKey.contains(CacheKey.SYS_FUNC) || cacheKey.contains(CacheKey.ROLE_FUNC)) {
            cacheValue = redisTemplate.opsForHash().values(cacheKey);
        }else {
            cacheValue = redisTemplate.opsForValue().get(cacheKey);
        }
        CacheVo sysCache = new CacheVo(cacheName, cacheKey, cacheValue);
        return callback(sysCache);
    }

    /**
     * 删除缓存
     *
     * @return
     */
    @RequestMapping(value = "/deleteCacheKey", method = RequestMethod.POST)
    public ResultBean deleteCacheKey(@RequestBody @Valid DeleteCacheForm dcf) {
        Boolean b = redisTemplate.delete(dcf.getCacheKey());
        return callback(b);
    }

    /**
     * 删除
     * @param dcf
     * @return
     */
    @RequestMapping(value = "/deleteCacheName", method = RequestMethod.POST)
    public ResultBean deleteCacheName(@RequestBody @Valid DeleteCacheForm dcf) {
        Collection<String> cacheKeys = cacheHandler.keys(dcf.getCacheName());
        Long n = redisTemplate.delete(cacheKeys);
        return callback(n);
    }

    /**
     * 清空所有
     * @return
     */
    @RequestMapping(value = "/deleteAll", method = RequestMethod.POST)
    public ResultBean deleteAll() {
        Collection<String> cacheKeys = cacheHandler.keys("*");
        Long n = redisTemplate.delete(cacheKeys);
        return callback(n);
    }

    /**
     * 我的所有key
     * @return
     */
    @RequestMapping(value = "/myList", method = RequestMethod.GET)
    public ResultBean myList(UserCommonForm form) {
        Set<String> cacheKeys = this.getMyKeys(form.getUserId());
        List<CacheVo> list = new ArrayList<>();
        for(String key :cacheKeys){
            Object v = redisTemplate.opsForValue().get(key);
            CacheVo vo = new CacheVo();
            vo.setCacheKey(key);
            vo.setCacheValue(v);
            Long expire = redisTemplate.getExpire(key);
            vo.setExpire(expire);
            list.add(vo);
        }
        return callback(list);
    }

    /**
     * 清空我的所有key
     * @return
     */
    @RequestMapping(value = "/clearMe", method = RequestMethod.POST)
    public ResultBean clearMe(UserCommonForm form) {
        Set<String> cacheKeys = this.getMyKeys(form.getUserId());
        Long n = redisTemplate.delete(cacheKeys);
        return callback(n);
    }

    /**
     * 我的key列表
     *
     * @param userId
     * @return
     */
    private Set<String> getMyKeys(Long userId){
        String uid = userId.toString();
        Set<String> cacheKeys = new HashSet<>();
        cacheKeys.add(CacheKey.getKey(CacheKey.USER_TODAY_CALENDAR_COUNTS,uid));
        cacheKeys.addAll(cacheHandler.keys(CacheKey.getKey(CacheKey.USER_PLAN_NOTIFY,uid,"*")));
        cacheKeys.addAll(cacheHandler.keys(CacheKey.getKey(CacheKey.USER_STAT_NOTIFY,uid,"*")));
        cacheKeys.add(CacheKey.getKey(CacheKey.REWARD_LOCK,uid));
        cacheKeys.add(CacheKey.getKey(CacheKey.USER_CURRENT_POINTS,uid));
        cacheKeys.addAll(cacheHandler.keys(CacheKey.getKey(CacheKey.USER_CONTINUE_OP,uid,"*")));
        cacheKeys.addAll(cacheHandler.keys(CacheKey.getKey(CacheKey.USER_STAT,uid,"*")));
        cacheKeys.add(CacheKey.getKey(CacheKey.USER_LOGIN_FAIL,uid));
        cacheKeys.addAll(cacheHandler.keys(CacheKey.getKey(CacheKey.USER_CODE_LIMIT,"*",uid)));
        cacheKeys.add(CacheKey.getKey(CacheKey.USER_LATEST_MESSAGE,uid));
        cacheKeys.add(CacheKey.getKey(CacheKey.USER_LATEST_SCORE,uid));
        cacheKeys.add(CacheKey.getKey(CacheKey.CONSUME_CACHE_QUEUE,uid));
        cacheKeys.add(CacheKey.getKey(CacheKey.GOODS_TYPE_MATCH_LIST,uid));
        return cacheKeys;
    }

}
