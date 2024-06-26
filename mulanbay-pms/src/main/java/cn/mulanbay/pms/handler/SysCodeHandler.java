package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.business.handler.lock.DistributedLock;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.domain.SysCode;
import cn.mulanbay.pms.persistent.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static cn.mulanbay.pms.common.Constant.DAY_SECONDS;

/**
 * 系统代码处理
 *
 * @author fenghong
 * @date 2024/6/19
 */
@Component
public class SysCodeHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(SysCodeHandler.class);

    /**
     * 系统代码次数批量更新
     */
    @Value("${mulanbay.notify.code.batchUpdates}")
    long codeBatchUpdates;

    /**
     * 分布式锁重试次数
     */
    @Value("${mulanbay.lock.retryTimes:3}")
    int retryTimes;

    /**
     * 分布式锁超时时间(毫秒)
     */
    @Value("${mulanbay.lock.expire:3000}")
    long expire;

    /**
     * 限流停止服务时间(秒)
     */
    @Value("${mulanbay.security.lock.lockTime:600}")
    long lockTime;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    ThreadPoolHandler threadPoolHandler;

    @Autowired
    CommonCacheHandler commonCacheHandler;

    @Autowired
    SystemStatusHandler systemStatusHandler;

    @Autowired
    LogService logService;

    @Autowired
    DistributedLock distributedLock;

    public SysCodeHandler() {
        super("系统代码处理");
    }

    /**
     * 更新次数
     * @param code
     */
    public void updateCount(Integer code) {
        this.updateCount(code,1);
    }

    /**
     * 更新次数
     * @param code
     * @param addCount
     */
    public void updateCount(Integer code, long addCount){
        threadPoolHandler.pushThread(() -> {
            this.handleUpdate(code,addCount);
        });
    }

    /**
     * 更新次数
     * @param code
     * @param addCount
     */
    private void handleUpdate(Integer code, long addCount) {
        String lockKey = null;
        boolean lock = false;
        try {
            String key = CacheKey.getKey(CacheKey.SYS_CODE_COUNTS,code.toString());
            long cv = cacheHandler.incre(key,addCount);
            if(cv>=codeBatchUpdates){
                lockKey = CacheKey.getKey(CacheKey.SYS_CODE_COUNTS_LOCK,code.toString());
                lock = distributedLock.lock(lockKey, expire, retryTimes);
                if (lock) {
                    cacheHandler.incre(key,-cv);
                    logService.updateSysCodeCount(code,cv);
                }
            }
            this.checkLimit(code, (int) addCount);
        } catch (Exception e) {
            logger.error("更新系统代码次数异常", e);
        } finally {
            if (lock) {
                distributedLock.releaseLock(lockKey);
            }
        }
    }

    /**
     * 检测限制
     *
     * @param code
     * @param addCount
     */
    private void checkLimit(Integer code, int addCount){
        SysCode sc = this.getSysCode(code);
        if(sc==null){
            return;
        }
        if(sc.getSysLimit()<=0){
            return;
        }
        String key = this.getLimitKey(code);
        Integer count = cacheHandler.get(key,Integer.class);
        if(count==null){
            count=0;
        }
        count+=addCount;
        if(count>=sc.getSysLimit()){
            //锁定系统
            String msg = "系统代码"+code+"达到限流次数";
            Date expireTime = new Date(System.currentTimeMillis()+lockTime*1000);
            systemStatusHandler.lock(PmsCode.SERVER_LIMIT_STOP,msg,expireTime,null);
            //重新归零
            count = 0;
        }
        cacheHandler.set(key,count,DAY_SECONDS);
    }

    /**
     * 获取限流key
     * @param code
     * @return
     */
    public String getLimitKey(Integer code){
        String today = DateUtil.getToday();
        return CacheKey.getKey(CacheKey.SYS_CODE_COUNTS_LIMIT,code.toString(),today);
    }

    /**
     * 获取错误代码定义
     *
     * @param code
     * @return
     */
    public SysCode getSysCode(int code) {
        return commonCacheHandler.getBean(SysCode.class, code);
    }

    /**
     * 移除
     * @param code
     */
    public void refreshSysCode(int code) {
        commonCacheHandler.removeBean(SysCode.class, code);
    }
}
