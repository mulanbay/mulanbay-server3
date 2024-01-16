package cn.mulanbay.pms.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 缓存配置
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
@Configuration
public class CacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    /**
     * 设置key的序列化方法
     *
     * @param redisTemplate
     * @return
     */
    @Primary
    @Bean
    public RedisTemplate<Object,Object> redisStringTemplate(RedisTemplate<Object,Object> redisTemplate){
        StringRedisSerializer serializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(serializer);
        //redisTemplate.setValueSerializer(serializer);
        //redisTemplate.setHashKeySerializer(serializer);
        logger.info("set RedisTemplate Serializer");
        return redisTemplate;
    }
}
