package cn.mulanbay.pms.persistent.dto.sport;

import java.math.BigDecimal;

public class ExerciseMultiStat {

    //冗余字段，方便前端操作
    private Long sportId;
    //最佳值
    private BigDecimal maxValue;
    private Integer maxDuration;
    private BigDecimal maxSpeed;
    private BigDecimal maxMaxSpeed;
    private BigDecimal maxPace;
    private BigDecimal maxMaxPace;
    private Integer maxMaxHeartRate;
    private Integer maxAvgHeartRate;
    //结束
    //最小值
    private BigDecimal minValue;
    private Integer minDuration;
    private BigDecimal minSpeed;
    private BigDecimal minMaxSpeed;
    private BigDecimal minPace;
    private BigDecimal minMaxPace;
    private Integer minMaxHeartRate;
    private Integer minAvgHeartRate;
    //结束
    //平均值(有小数)
    private BigDecimal avgValue;
    private BigDecimal avgDuration;
    private BigDecimal avgSpeed;
    private BigDecimal avgMaxSpeed;
    private BigDecimal avgPace;
    private BigDecimal avgMaxPace;
    private BigDecimal avgMaxHeartRate;
    private BigDecimal avgAvgHeartRate;

    private String unit;

    public ExerciseMultiStat() {
    }

    public ExerciseMultiStat(BigDecimal maxValue, BigDecimal maxSpeed) {
        this.maxValue = maxValue;
        this.maxSpeed = maxSpeed;
    }

    public ExerciseMultiStat(Long sportId, BigDecimal maxValue, Integer maxDuration, BigDecimal maxSpeed, BigDecimal maxMaxSpeed, BigDecimal maxPace, BigDecimal maxMaxPace, Integer maxMaxHeartRate, Integer maxAvgHeartRate, BigDecimal minValue, Integer minDuration, BigDecimal minSpeed, BigDecimal minMaxSpeed, BigDecimal minPace, BigDecimal minMaxPace, Integer minMaxHeartRate, Integer minAvgHeartRate, BigDecimal avgValue, BigDecimal avgDuration, BigDecimal avgSpeed, BigDecimal avgMaxSpeed, BigDecimal avgPace, BigDecimal avgMaxPace, BigDecimal avgMaxHeartRate, BigDecimal avgAvgHeartRate) {
        this.sportId = sportId;
        this.maxValue = maxValue;
        this.maxDuration = maxDuration;
        this.maxSpeed = maxSpeed;
        this.maxMaxSpeed = maxMaxSpeed;
        this.maxPace = maxPace;
        this.maxMaxPace = maxMaxPace;
        this.maxMaxHeartRate = maxMaxHeartRate;
        this.maxAvgHeartRate = maxAvgHeartRate;
        this.minValue = minValue;
        this.minDuration = minDuration;
        this.minSpeed = minSpeed;
        this.minMaxSpeed = minMaxSpeed;
        this.minPace = minPace;
        this.minMaxPace = minMaxPace;
        this.minMaxHeartRate = minMaxHeartRate;
        this.minAvgHeartRate = minAvgHeartRate;
        this.avgValue = avgValue;
        this.avgDuration = avgDuration;
        this.avgSpeed = avgSpeed;
        this.avgMaxSpeed = avgMaxSpeed;
        this.avgPace = avgPace;
        this.avgMaxPace = avgMaxPace;
        this.avgMaxHeartRate = avgMaxHeartRate;
        this.avgAvgHeartRate = avgAvgHeartRate;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Integer maxDuration) {
        this.maxDuration = maxDuration;
    }

    public BigDecimal getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(BigDecimal maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public BigDecimal getMaxMaxSpeed() {
        return maxMaxSpeed;
    }

    public void setMaxMaxSpeed(BigDecimal maxMaxSpeed) {
        this.maxMaxSpeed = maxMaxSpeed;
    }

    public BigDecimal getMaxPace() {
        return maxPace;
    }

    public void setMaxPace(BigDecimal maxPace) {
        this.maxPace = maxPace;
    }

    public BigDecimal getMaxMaxPace() {
        return maxMaxPace;
    }

    public void setMaxMaxPace(BigDecimal maxMaxPace) {
        this.maxMaxPace = maxMaxPace;
    }

    public Integer getMaxMaxHeartRate() {
        return maxMaxHeartRate;
    }

    public void setMaxMaxHeartRate(Integer maxMaxHeartRate) {
        this.maxMaxHeartRate = maxMaxHeartRate;
    }

    public Integer getMaxAvgHeartRate() {
        return maxAvgHeartRate;
    }

    public void setMaxAvgHeartRate(Integer maxAvgHeartRate) {
        this.maxAvgHeartRate = maxAvgHeartRate;
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    public Integer getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(Integer minDuration) {
        this.minDuration = minDuration;
    }

    public BigDecimal getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(BigDecimal minSpeed) {
        this.minSpeed = minSpeed;
    }

    public BigDecimal getMinMaxSpeed() {
        return minMaxSpeed;
    }

    public void setMinMaxSpeed(BigDecimal minMaxSpeed) {
        this.minMaxSpeed = minMaxSpeed;
    }

    public BigDecimal getMinPace() {
        return minPace;
    }

    public void setMinPace(BigDecimal minPace) {
        this.minPace = minPace;
    }

    public BigDecimal getMinMaxPace() {
        return minMaxPace;
    }

    public void setMinMaxPace(BigDecimal minMaxPace) {
        this.minMaxPace = minMaxPace;
    }

    public Integer getMinMaxHeartRate() {
        return minMaxHeartRate;
    }

    public void setMinMaxHeartRate(Integer minMaxHeartRate) {
        this.minMaxHeartRate = minMaxHeartRate;
    }

    public Integer getMinAvgHeartRate() {
        return minAvgHeartRate;
    }

    public void setMinAvgHeartRate(Integer minAvgHeartRate) {
        this.minAvgHeartRate = minAvgHeartRate;
    }

    public BigDecimal getAvgValue() {
        return avgValue;
    }

    public void setAvgValue(BigDecimal avgValue) {
        this.avgValue = avgValue;
    }

    public BigDecimal getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(BigDecimal avgDuration) {
        this.avgDuration = avgDuration;
    }

    public BigDecimal getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(BigDecimal avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public BigDecimal getAvgMaxSpeed() {
        return avgMaxSpeed;
    }

    public void setAvgMaxSpeed(BigDecimal avgMaxSpeed) {
        this.avgMaxSpeed = avgMaxSpeed;
    }

    public BigDecimal getAvgPace() {
        return avgPace;
    }

    public void setAvgPace(BigDecimal avgPace) {
        this.avgPace = avgPace;
    }

    public BigDecimal getAvgMaxPace() {
        return avgMaxPace;
    }

    public void setAvgMaxPace(BigDecimal avgMaxPace) {
        this.avgMaxPace = avgMaxPace;
    }

    public BigDecimal getAvgMaxHeartRate() {
        return avgMaxHeartRate;
    }

    public void setAvgMaxHeartRate(BigDecimal avgMaxHeartRate) {
        this.avgMaxHeartRate = avgMaxHeartRate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
