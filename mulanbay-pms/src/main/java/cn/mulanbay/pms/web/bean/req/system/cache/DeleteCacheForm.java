package cn.mulanbay.pms.web.bean.req.system.cache;

/**
 * @author fenghong
 * @date 2024/1/16
 */
public class DeleteCacheForm {

    private String cacheName;

    private String cacheKey;

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }
}
