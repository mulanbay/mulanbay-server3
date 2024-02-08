package cn.mulanbay.pms.web.bean.res.sport;

import cn.mulanbay.pms.persistent.domain.Exercise;
import cn.mulanbay.pms.persistent.domain.SportMilestone;

import java.util.List;

public class ExerciseVo extends Exercise {

    /**
     * 最高安全心率
     */
    private int maxSafeHeartRate;

    private long sportMilestones;

    private List<SportMilestone> sportMilestoneList;

    public int getMaxSafeHeartRate() {
        return maxSafeHeartRate;
    }

    public void setMaxSafeHeartRate(int maxSafeHeartRate) {
        this.maxSafeHeartRate = maxSafeHeartRate;
    }

    public long getSportMilestones() {
        return sportMilestones;
    }

    public void setSportMilestones(long sportMilestones) {
        this.sportMilestones = sportMilestones;
    }

    public List<SportMilestone> getSportMilestoneList() {
        return sportMilestoneList;
    }

    public void setSportMilestoneList(List<SportMilestone> sportMilestoneList) {
        this.sportMilestoneList = sportMilestoneList;
    }
}
