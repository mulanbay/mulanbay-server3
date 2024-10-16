package cn.mulanbay.pms.persistent.dto.sport;

import java.util.Date;

public class ExerciseItemStat {

    private Date exerciseTime;

    private Object value;

    public ExerciseItemStat(Date exerciseTime, Object value) {
        this.exerciseTime = exerciseTime;
        this.value = value;
    }

    public Date getExerciseTime() {
        return exerciseTime;
    }

    public void setExerciseTime(Date exerciseTime) {
        this.exerciseTime = exerciseTime;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
