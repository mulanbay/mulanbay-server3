package cn.mulanbay.pms.web.bean.res.main;

import cn.mulanbay.pms.handler.bean.fund.FundStatBean;

public class GeneralStatVo {

    private FundStatBean monthStat;

    private FundStatBean yearStat;

    public FundStatBean getMonthStat() {
        return monthStat;
    }

    public void setMonthStat(FundStatBean monthStat) {
        this.monthStat = monthStat;
    }

    public FundStatBean getYearStat() {
        return yearStat;
    }

    public void setYearStat(FundStatBean yearStat) {
        this.yearStat = yearStat;
    }
}
