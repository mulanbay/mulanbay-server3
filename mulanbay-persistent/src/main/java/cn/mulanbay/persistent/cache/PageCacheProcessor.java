package cn.mulanbay.persistent.cache;

import cn.mulanbay.common.util.DigestUtil;
import cn.mulanbay.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 分页缓存
 * @author fenghong
 * @date 2024/5/20
 */
@Component
public class PageCacheProcessor {

    /**
     * 前缀
     */
    @Value("${mulanbay.persistent.page.cache.keyPrefix:pageSearch}")
    private String keyPrefix;

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
     * 缓存失效时间
     */
    @Value("${mulanbay.persistent.page.cache.expireSeconds:10}")
    private int expireSeconds;

    @Autowired
    CacheProcessor cacheProcessor;

    /**
     * 是否启用缓存
     * @return
     */
    public boolean enableCache(){
        return listCache || totalCache;
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
        if(StringUtil.isEmpty(cacheBeans)) {
            return false;
        }
        String beanName = this.getBeanClassName(beanClass);
        if("*".equals(cacheBeans)) {
            return true;
        }
        String[] ss = cacheBeans.split(",");
        for(String s : ss){
            if(s.equals(beanName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取列表缓存
     * @param key
     * @param beanClass
     * @return
     * @param <T>
     */
    public <T> List<T> getCacheList(String key,Class<T> beanClass){
        List<T> list = cacheProcessor.get(key,List.class);
        return list;
    }

    /**
     * 缓存结果
     * @param key
     * @param list
     */
    public void cacheList(String key,List list){
        cacheProcessor.set(key,list,expireSeconds);
    }

    /**
     * 获取总数缓存
     * @param key
     * @return
     */
    public Long getCacheTotal(String key){
        return cacheProcessor.get(key,Long.class);
    }

    /**
     * 缓存结果
     * @param key
     * @param total
     */
    public void cacheTotal(String key,Long total){
        cacheProcessor.set(key,total,expireSeconds);
    }

    /**
     * 创建缓存KEY
     * @param hql
     * @param page
     * @param pageSize
     * @param clazz
     * @return
     */
    public String createListCacheKey(String hql, int page,int pageSize, Class clazz,Object... iObjects){
        String beanName = this.getBeanClassName(clazz);
        return keyPrefix +":" +beanName+":"+this.getParaKey(hql, iObjects)+":"+page+":"+pageSize;
    }

    /**
     * 创建缓存KEY
     * @param hql
     * @param clazz
     * @return
     */
    public String createTotalCacheKey(String hql, Class clazz,Object... iObjects){
        String beanName = this.getBeanClassName(clazz);
        return keyPrefix +":" +beanName+":"+this.getParaKey(hql, iObjects)+":t";
    }

    /**
     * 获取bean名称
     * @param beanClass
     * @return
     */
    private String getBeanClassName(Class beanClass){
        return beanClass.getSimpleName();
    }

    /**
     * 获取参数key,会产生hash碰撞
     * @param hql
     * @param iObjects
     * @return
     */
    private String getParaKey(String hql, Object... iObjects){
        StringBuffer sb = new StringBuffer();
        sb.append(hql);
        if(iObjects!=null){
            int n = iObjects.length;
            for(int i=0;i<n;i++){
                Object o = iObjects[i];
                if(o==null){
                    sb.append(","+i+"=null");
                }else{
                    sb.append(","+i+"="+o.toString());
                }
            }
        }
        return DigestUtil.sha1(sb.toString()).toLowerCase();
    }
}
