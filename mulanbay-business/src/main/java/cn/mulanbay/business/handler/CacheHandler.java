package cn.mulanbay.business.handler;

import cn.mulanbay.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存实现
 * 目前由redis方案
 *
 * @author fenghong
 * @create 2021-05-11 10:00
 */
public class CacheHandler extends BaseHandler  {

    private static final int DEFAULT_EXPIRE_SECONDS = 300;

    private static final int NO_EXPIRE = 0;

    private  static final Set<String> EMPTY_SET = new HashSet<>(0);

    @Value("${mulanbay.namespace}")
    String namespace;

    @Autowired
    private RedisTemplate redisTemplate;

    public CacheHandler() {
        super("缓存处理");
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void reload() {
        super.reload();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public Boolean selfCheck() {
        this.set(getFullKey("test"), "testData", 10);
        String value = this.getForString(getFullKey("test"));
        boolean b = "testData".equals(value);
        this.checkResult = b;
        return b;
    }

    /**
     * @param key
     * @param value         需要实现序列化接口
     * @param expireSeconds
     */
    public void addToList(String key, Object value, int expireSeconds) {
        if (expireSeconds == -1) {
            expireSeconds = DEFAULT_EXPIRE_SECONDS;
        }
        redisTemplate.opsForList().leftPush(this.getFullKey(key), value);
        if (expireSeconds > NO_EXPIRE) {
            redisTemplate.expire(getFullKey(key), expireSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取list
     *
     * @param key
     */
    public List getList(String key) {
        return redisTemplate.opsForList().range(getFullKey(key), 0, -1);
    }

    /**
     * @param key
     * @param value         需要实现序列化接口
     * @param expireSeconds
     */
    public void set(String key, Object value, int expireSeconds) {
        if (expireSeconds == -1) {
            expireSeconds = DEFAULT_EXPIRE_SECONDS;
        }
        if (expireSeconds == NO_EXPIRE) {
            redisTemplate.opsForValue().set(getFullKey(key), value);
        } else {
            redisTemplate.opsForValue().set(getFullKey(key), value, expireSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 设置超时
     * @param key
     * @param value         需要实现序列化接口
     * @param expires 毫秒
     */
    public void setMS(String key, Object value, long expires) {
        redisTemplate.opsForValue().set(getFullKey(key), value, expires, TimeUnit.MILLISECONDS);
    }

    /**
     * 相当于SETNX
     * @param key
     * @param value         需要实现序列化接口
     * @param expireSeconds
     */
    public boolean setNX(String key, Object value, int expireSeconds) {
        if (expireSeconds == -1) {
            expireSeconds = DEFAULT_EXPIRE_SECONDS;
        }
        if (expireSeconds == NO_EXPIRE) {
            return redisTemplate.opsForValue().setIfAbsent(getFullKey(key), value);
        } else {
            return redisTemplate.opsForValue().setIfAbsent(getFullKey(key), value, expireSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * HashMap模式
     *
     * @param key
     * @param value         需要实现序列化接口
     * @param expireSeconds
     */
    public void setHash(String key, Map value, int expireSeconds) {
        if (expireSeconds == -1) {
            expireSeconds = DEFAULT_EXPIRE_SECONDS;
        }
        redisTemplate.opsForHash().putAll(getFullKey(key), value);
        if (expireSeconds > NO_EXPIRE) {
            redisTemplate.expire(getFullKey(key), expireSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * HashMap模式
     *
     * @param key
     * @param value 需要实现序列化接口
     */
    public void setHash(String key, String hashKey,Object value) {
        redisTemplate.opsForHash().put(getFullKey(key),hashKey, value);
    }

    /**
     * 获取hash key值
     * @param key
     * @param hashKey
     * @param cls
     * @return
     * @param <T>
     */
    public <T> T getHash(String key, String hashKey, Class<T> cls) {
        return (T) redisTemplate.opsForHash().get(getFullKey(key), hashKey);
    }

    /**
     * 删除Hash key
     * @param key
     * @param hashKey
     * @return
     */
    public Long deleteHash(String key, String hashKey) {
        return redisTemplate.opsForHash().delete(getFullKey(key), hashKey);
    }

    /**
     * 获取缓存
     *
     * @param key
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> cls) {
        return (T) redisTemplate.opsForValue().get(getFullKey(key));
    }

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(getFullKey(key));
    }

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    public String getForString(String key) {
        return (String) redisTemplate.opsForValue().get(getFullKey(key));
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public boolean delete(String key) {
        return redisTemplate.delete(getFullKey(key));
    }

    /**
     * 批量删除key
     *
     * @param pattern:abc:*
     */
    public Long deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(getFullKey(pattern));
        if (StringUtil.isNotEmpty(keys)) {
            return redisTemplate.delete(keys);
        }
        return 0L;
    }

    /**
     * 增加
     *
     * @param key
     * @param n
     * @return
     */
    public long incre(String key, long n) {
        return redisTemplate.opsForValue().increment(getFullKey(key), n);
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(毫秒)
     *         0:代表为永久有效
     *         -2:没有该值
     *         -1：没有设置过期时间
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(getFullKey(key), TimeUnit.MILLISECONDS);
    }

    /**
     * 获取键集合
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern){
        Set<String> keys = redisTemplate.keys(this.getFullKey(pattern));
        return keys==null ? EMPTY_SET : keys;
    }
    /**
     * 获取key的全路径
     *
     * @param key
     * @return
     */
    public String getFullKey(String key) {
        if(key.startsWith(namespace)){
            return key;
        }
        return namespace + ":" + key;
    }

}
