package cn.mulanbay.pms.persistent.dto.sport;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fenghong
 * @date 2024/2/8
 */
public class ExerciseBestDTO {

    private Date exerciseTime;

    private BigDecimal value;

    private BigDecimal maxSpeed;

    public ExerciseBestDTO(Date exerciseTime, BigDecimal value, BigDecimal maxSpeed) {
        this.exerciseTime = exerciseTime;
        this.value = value;
        this.maxSpeed = maxSpeed;
    }

    public Date getExerciseTime() {
        return exerciseTime;
    }

    public void setExerciseTime(Date exerciseTime) {
        this.exerciseTime = exerciseTime;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(BigDecimal maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}
