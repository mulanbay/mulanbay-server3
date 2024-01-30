package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.business.handler.lock.DistributedLock;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.PmsCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Description: 通用的Bean缓存实现
 * @Author: fenghong
 * @Create : 2021/6/3
 */
@Component
public class CommonCacheHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(CommonCacheHandler.class);

    @Autowired
    DistributedLock distributedLock;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    BaseService baseService;

    public CommonCacheHandler() {
        super("通用bean缓存方案");
    }

    /**
     * 移除缓存
     * @param cls
     * @param id
     */
    public void removeBean(Class cls, Serializable id){
        String key = "beanCache:" + cls.getName() + ":" + id;
        cacheHandler.delete(key);
    }
    /**
     * 获取缓存Bean
     *
     * @param cls
     * @param id
     * @param <T>
     * @return
     */
    public <T> T getBean(Class<T> cls, Serializable id) {
        boolean lock = false;
        String lockKey = null;
        try {
            String key = "beanCache:" + cls.getName() + ":" + id;
            T bean = cacheHandler.get(key, cls);
            if (bean == null) {
                lockKey = "lock:" + key;
                lock = distributedLock.lock(lockKey, 3000L,3);
                if (!lock) {
                    return null;
                }
                //此时应该需要重新再去查一下缓存，有可能其他线程获取设置了
                bean = cacheHandler.get(key, cls);
                if (bean != null) {
                    return bean;
                }
                //获取Bean
                bean = baseService.getObject(cls, id);
                if (bean != null) {
                    cacheHandler.set(key, bean, 300);
                }
            }
            return bean;
        } catch (Exception ex) {
            logger.error("Common Cache Bean get error:",ex);
            throw new ApplicationException(PmsCode.BEAN_GET_CACHE_ERROR);
        } finally {
            if (lock) {
                distributedLock.releaseLock(lockKey);
            }
        }
    }
}
