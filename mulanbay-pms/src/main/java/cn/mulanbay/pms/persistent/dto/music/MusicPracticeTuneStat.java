package cn.mulanbay.pms.persistent.dto.music;

public class MusicPracticeTuneStat {

    private String name;

    //次数
    private Long totalTimes;

    public MusicPracticeTuneStat(String name, Long totalTimes) {
        this.name = name;
        this.totalTimes = totalTimes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(Long totalTimes) {
        this.totalTimes = totalTimes;
    }
}
