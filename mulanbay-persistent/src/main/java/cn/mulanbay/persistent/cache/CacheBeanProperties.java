package cn.mulanbay.persistent.cache;

import cn.mulanbay.common.util.StringUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存Bean配置
 *
 * @author fenghong
 * @date 2024/8/13
 */
@Component
public class CacheBeanProperties {

    private static final Logger logger = LoggerFactory.getLogger(CacheBeanProperties.class);

    /**
     * 列表数据数据是否缓存
     */
    @Value("${mulanbay.persistent.page.cache.list:false}")
    private boolean listCache;

    /**
     * 总页数是否缓存
     */
    @Value("${mulanbay.persistent.page.cache.total:false}")
    private boolean totalCache;

    /**
     * 需要缓存的bean
     */
    @Value("${mulanbay.persistent.page.cache.beans:}")
    private String cacheBeans;

    /**
     * 缓存bean
     */
    private Map<String,Integer> cacheBeanMap;

    @PostConstruct
    public void init(){
        if(!listCache&&!totalCache){
            logger.info("列表数据和记录总数都设置为不缓存");
            return;
        }
        cacheBeanMap = new HashMap<>();
        if(StringUtil.isEmpty(cacheBeans)){
            logger.warn("未配置缓存Bean");
            return;
        }
        int v = 1;
        String[] ss = cacheBeans.split(",");
        for(String s : ss){
            cacheBeanMap.put(s,v);
        }
        logger.info("缓存Bean:"+cacheBeans);
    }

    /**
     * 判断列表数据是否要缓存
     * @param beanClass
     * @return
     */
    public boolean isListCache(Class beanClass){
        if(!listCache) {
            return false;
        }
        return this.isBeanCache(beanClass);
    }

    /**
     * 判断总记录值是否要缓存
     * @param beanClass
     * @return
     */
    public boolean isTotalCache(Class beanClass){
        if(!totalCache) {
            return false;
        }
        return this.isBeanCache(beanClass);
    }

    /**
     * 判断bean是否要缓存
     * @param beanClass
     * @return
     */
    private boolean isBeanCache(Class beanClass){
        if(cacheBeanMap.get("*")!=null){
            return true;
        }
        String beanName = beanClass.getSimpleName();
        if(cacheBeanMap.get(beanName)!=null){
            return true;
        }
        return false;
    }
}
