package cn.mulanbay.pms.web.bean.res.system.cache;

/**
 * @author fenghong
 * @date 2024/1/16
 */
public class CacheVo {

    /**
     * 缓存名称
     */
    private String cacheName = "";

    /**
     * 缓存键名
     */
    private String cacheKey = "";

    /**
     * 缓存内容
     */
    private Object cacheValue = "";

    /**
     * 备注
     */
    private String remark = "";

    public CacheVo() {

    }

    public CacheVo(String cacheName, String remark) {
        this.cacheName = cacheName;
        this.remark = remark;
    }

    public CacheVo(String cacheName, String cacheKey, Object cacheValue) {
        this.cacheName = cacheName;
        this.cacheKey = cacheKey;
        this.cacheValue = cacheValue;
    }

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

    public Object getCacheValue() {
        return cacheValue;
    }

    public void setCacheValue(Object cacheValue) {
        this.cacheValue = cacheValue;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
