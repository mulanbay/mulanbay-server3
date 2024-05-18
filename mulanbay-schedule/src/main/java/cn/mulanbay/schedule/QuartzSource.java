package cn.mulanbay.schedule;


import cn.mulanbay.schedule.enums.CostTimeCalcType;
import cn.mulanbay.schedule.lock.ScheduleLocker;

/**
 * 调度的资源
 *
 * @author fenghong
 * @create 2017-11-11 17:14
 **/
public class QuartzSource {

    /**
     * 花费时间计算方式
     */
    private CostTimeCalcType costTimeCalcType = CostTimeCalcType.MAX;

    /**
     * 花费时间计算天数
     */
    private int costTimeDays = 7 ;

    /**
     * 花费时间比例
     */
    private double costTimeRate = 1.2;

    /**
     *  分布式任务最小的花费时间(毫秒)
     */
    private long distriTaskMinCost=2000;

    /**
     * 部署点
     */
    private String deployId;

    /**
     * 是否支持分布式
     */
    private boolean distriable=false;

    /**
     * 分布式锁
     */
    private ScheduleLocker scheduleLocker;

    /**
     * 持久层操作
     */
    private SchedulePersistentProcessor schedulePersistentProcessor;

    /**
     * 通知类（调度执行失败的时候传入）
     */
    private NotifiableProcessor notifiableProcessor;

    public CostTimeCalcType getCostTimeCalcType() {
        return costTimeCalcType;
    }

    public void setCostTimeCalcType(CostTimeCalcType costTimeCalcType) {
        this.costTimeCalcType = costTimeCalcType;
    }

    public int getCostTimeDays() {
        return costTimeDays;
    }

    public void setCostTimeDays(int costTimeDays) {
        this.costTimeDays = costTimeDays;
    }

    public double getCostTimeRate() {
        return costTimeRate;
    }

    public void setCostTimeRate(double costTimeRate) {
        this.costTimeRate = costTimeRate;
    }

    public long getDistriTaskMinCost() {
        return distriTaskMinCost;
    }

    public void setDistriTaskMinCost(long distriTaskMinCost) {
        this.distriTaskMinCost = distriTaskMinCost;
    }

    public String getDeployId() {
        return deployId;
    }

    public void setDeployId(String deployId) {
        this.deployId = deployId;
    }

    public boolean getDistriable() {
        return distriable;
    }

    public void setDistriable(boolean distriable) {
        this.distriable = distriable;
    }

    public ScheduleLocker getScheduleLocker() {
        return scheduleLocker;
    }

    public void setScheduleLocker(ScheduleLocker scheduleLocker) {
        this.scheduleLocker = scheduleLocker;
    }

    public SchedulePersistentProcessor getSchedulePersistentProcessor() {
        return schedulePersistentProcessor;
    }

    public void setSchedulePersistentProcessor(SchedulePersistentProcessor schedulePersistentProcessor) {
        this.schedulePersistentProcessor = schedulePersistentProcessor;
    }

    public NotifiableProcessor getNotifiableProcessor() {
        return notifiableProcessor;
    }

    public void setNotifiableProcessor(NotifiableProcessor notifiableProcessor) {
        this.notifiableProcessor = notifiableProcessor;
    }

}
