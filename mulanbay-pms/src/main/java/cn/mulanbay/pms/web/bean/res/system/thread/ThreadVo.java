package cn.mulanbay.pms.web.bean.res.system.thread;

import java.util.Date;

public class ThreadVo {

    private long id;
    // 线程名称
    private String threadName;

    private boolean stop;

    // 如果是周期性线程，设置是先sleep再做业务还是先业务再sleep
    private boolean beforeSleep;

    private long interval;

    private Date createdTime;

    private Date lastExecuteTime;

    // 总执行次数
    private long totalCount;

    // 总执行失败次数
    private long failCount;

    // 持续运行时间
    private long duration;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isBeforeSleep() {
        return beforeSleep;
    }

    public void setBeforeSleep(boolean beforeSleep) {
        this.beforeSleep = beforeSleep;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getLastExecuteTime() {
        return lastExecuteTime;
    }

    public void setLastExecuteTime(Date lastExecuteTime) {
        this.lastExecuteTime = lastExecuteTime;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getFailCount() {
        return failCount;
    }

    public void setFailCount(long failCount) {
        this.failCount = failCount;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
