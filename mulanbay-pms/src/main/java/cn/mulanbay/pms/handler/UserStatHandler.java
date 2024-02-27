package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.persistent.domain.UserStat;
import cn.mulanbay.pms.persistent.dto.stat.StatResultDTO;
import cn.mulanbay.pms.persistent.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fenghong
 * @title: NotifyStatHandler
 * @description: 提醒统计, 首页的统计使用很频繁，通过日终已有的调度统计进行缓存
 * 开启日终统计缓存的逻辑：每日的调度凌晨查询统计提醒的统计值时根据job里面的缓存设置进行缓存（通常为24小时）
 * 首页的提醒统计列表就根据日终已有的统计值直接查询，加快查询速度
 * 因为提醒的统计值一天内的基本不会变化，如果想得到最新值，在用户提醒列表页面进行手动清除缓存就可以
 * @date 2019/12/27 3:05 下午
 */
@Component
public class UserStatHandler extends BaseHandler {

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    StatService statService;

    public UserStatHandler() {
        super("用户统计处理");
    }

    /**
     * @param un
     * @param queryExpireSeconds
     * @return
     */
    public StatResultDTO getStatResult(UserStat un, int queryExpireSeconds) {
        if (queryExpireSeconds <= 0) {
            return this.getStatResult(un);
        }
        Long userId = un.getUserId();
        String cacheKey = CacheKey.getKey(CacheKey.USER_NOTIFY_STAT, userId.toString(), un.getStatId().toString());
        StatResultDTO nr = cacheHandler.get(cacheKey, StatResultDTO.class);
        if (nr == null) {
            nr = this.getStatResult(un);
            cacheStatResult(nr, queryExpireSeconds);
        }
        return nr;
    }

    /**
     * 缓存结果
     *
     * @param nr
     * @param expireSeconds
     */
    public void cacheStatResult(StatResultDTO nr, int expireSeconds) {
        Long userId = nr.getUserStat().getUserId();
        String cacheKey = CacheKey.getKey(CacheKey.USER_NOTIFY_STAT, userId.toString(), nr.getUserStat().getStatId().toString());
        cacheHandler.set(cacheKey, nr, expireSeconds);
    }

    /**
     * 获取统计结果
     *
     * @param un
     * @return
     */
    public StatResultDTO getStatResult(UserStat un) {
        StatResultDTO nr = statService.getStatResult(un, un.getUserId());
        return nr;
    }

    /**
     * 清理缓存，删除该用户小所有的NotifyResult缓存
     *
     * @param userId
     * @return
     */
    public Long deleteCache(Long userId) {
        String cacheKeys = CacheKey.getKey(CacheKey.USER_NOTIFY_STAT, userId.toString(), "*");
        return cacheHandler.deleteByPattern(cacheKeys);
    }

    /**
     * 清理缓存
     *
     * @param userId
     * @param userNotifyId
     * @return
     */
    public boolean deleteCache(Long userId,Long userNotifyId) {
        String cacheKey = CacheKey.getKey(CacheKey.USER_NOTIFY_STAT, userId.toString(), userNotifyId.toString());
        return cacheHandler.delete(cacheKey);
    }
}
