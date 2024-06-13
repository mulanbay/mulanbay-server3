package cn.mulanbay.schedule.lock;

/**
 * ${DESCRIPTION}
 * 调度锁
 * @author fenghong
 * @create 2017-11-11 12:25
 **/
public interface ScheduleLocker {

    /**
     * 上锁
     * @param identityKey
     * @param expires 毫秒
     * @return
     */
    LockStatus lock(String identityKey, long expires);

    LockStatus unlock(String identityKey);

}
