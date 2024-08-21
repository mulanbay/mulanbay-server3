package cn.mulanbay.schedule;


import cn.mulanbay.schedule.enums.CostTimeCalcType;
import cn.mulanbay.schedule.lock.ScheduleLocker;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 调度的资源
 *
 * @author fenghong
 * @create 2017-11-11 17:14
 **/
public class QuartzSource {

    /**
     * 花费时间计算天数
     */
    @Value("${mulanbay.schedule.costTimeDays:7}")
    int costTimeDays;

    /**
     * 花费时间计算天数
     */
    @Value("${mulanbay.schedule.costTimeRate:1.2}")
    double costTimeRate;

    /**
     * 分布式任务最小的花费时间(毫秒)
     */
    @Value("${mulanbay.schedule.distriTaskMinCost:2000}")
    long distriTaskMinCost;

    /**
     * 部署点（针对不支持分布式的任务）
     */
    @Value("${mulanbay.nodeId:mulanbay}")
    private String deployId;

    /**
     * 调度系统是否支持分布式
     */
    @Value("${mulanbay.schedule.distriable:false}")
    boolean distriable;

    /**
     * 花费时间计算方式
     */
    @Value("${mulanbay.schedule.costTimeCalcType:MAX}")
    CostTimeCalcType costTimeCalcType;

    @Autowired
    SchedulePersistentProcessor schedulePersistentProcessor;

    @Resource(name = "scheduleLocker")
    ScheduleLocker scheduleLocker;

    @Autowired(required = false)
    NotifiableProcessor notifiableProcessor;


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
