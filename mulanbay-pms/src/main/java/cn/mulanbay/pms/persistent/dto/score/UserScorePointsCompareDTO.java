package cn.mulanbay.pms.persistent.dto.score;

import java.math.BigDecimal;

public class UserScorePointsCompareDTO {

    private Number date;

    private BigDecimal score;

    private BigDecimal points;

    public UserScorePointsCompareDTO(Number date, BigDecimal score, BigDecimal points) {
        this.date = date;
        this.score = score;
        this.points = points;
    }

    public Number getDate() {
        return date;
    }

    public void setDate(Number date) {
        this.date = date;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getPoints() {
        return points;
    }

    public void setPoints(BigDecimal points) {
        this.points = points;
    }
}
