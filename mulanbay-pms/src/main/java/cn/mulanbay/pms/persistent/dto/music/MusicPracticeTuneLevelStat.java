package cn.mulanbay.pms.persistent.dto.music;

import cn.mulanbay.pms.persistent.enums.TuneLevel;

import java.util.Date;

public class MusicPracticeTuneLevelStat {

    private String tune;

    private Date minPracticeDate;

    private Date maxPracticeDate;

    private Short levelIndex;

    public MusicPracticeTuneLevelStat(Date minPracticeDate, Date maxPracticeDate, Short levelIndex) {
        this.minPracticeDate = minPracticeDate;
        this.maxPracticeDate = maxPracticeDate;
        this.levelIndex = levelIndex;
    }

    public Date getMinPracticeDate() {
        return minPracticeDate;
    }

    public void setMinPracticeDate(Date minPracticeDate) {
        this.minPracticeDate = minPracticeDate;
    }

    public Date getMaxPracticeDate() {
        return maxPracticeDate;
    }

    public void setMaxPracticeDate(Date maxPracticeDate) {
        this.maxPracticeDate = maxPracticeDate;
    }

    public String getTune() {
        return tune;
    }

    public void setTune(String tune) {
        this.tune = tune;
    }

    public void setLevelIndex(Short levelIndex) {
        this.levelIndex = levelIndex;
    }

    public Short getLevelIndex() {
        return levelIndex;
    }

    public String getLevelName() {
        return getLevel() == null ? null : getLevel().getName();
    }

    public TuneLevel getLevel() {
        if(levelIndex==null){
            return null;
        }else{
            return TuneLevel.getLevel(levelIndex.intValue());
        }
    }

}
