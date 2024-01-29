package cn.mulanbay.pms.web.bean.res.main;

public class GeneralStatVo {

    private GeneralStatDetailVo monthStat;

    private GeneralStatDetailVo yearStat;

    public GeneralStatDetailVo getMonthStat() {
        return monthStat;
    }

    public void setMonthStat(GeneralStatDetailVo monthStat) {
        this.monthStat = monthStat;
    }

    public GeneralStatDetailVo getYearStat() {
        return yearStat;
    }

    public void setYearStat(GeneralStatDetailVo yearStat) {
        this.yearStat = yearStat;
    }
}
