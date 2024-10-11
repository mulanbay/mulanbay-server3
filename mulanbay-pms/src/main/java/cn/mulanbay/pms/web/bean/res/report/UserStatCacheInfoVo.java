package cn.mulanbay.pms.web.bean.res.report;

import cn.mulanbay.pms.persistent.dto.report.StatResultDTO;

public class UserStatCacheInfoVo {

    /**
     * 缓存中信息
     */
    private StatResultDTO cacheData;

    /**
     * 数据库中实时信息
     */
    private StatResultDTO dbData;

    /**
     * 缓存的失效时间
     */
    private Long cacheExpire;

    /**
     * 最后一次通知缓存的失效时间
     */
    private Long lastNotifyExpire;

    public StatResultDTO getCacheData() {
        return cacheData;
    }

    public void setCacheData(StatResultDTO cacheData) {
        this.cacheData = cacheData;
    }

    public StatResultDTO getDbData() {
        return dbData;
    }

    public void setDbData(StatResultDTO dbData) {
        this.dbData = dbData;
    }

    public Long getCacheExpire() {
        return cacheExpire;
    }

    public void setCacheExpire(Long cacheExpire) {
        this.cacheExpire = cacheExpire;
    }

    public Long getLastNotifyExpire() {
        return lastNotifyExpire;
    }

    public void setLastNotifyExpire(Long lastNotifyExpire) {
        this.lastNotifyExpire = lastNotifyExpire;
    }
}
