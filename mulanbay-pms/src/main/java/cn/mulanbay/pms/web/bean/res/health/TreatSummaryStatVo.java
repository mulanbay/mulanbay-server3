package cn.mulanbay.pms.web.bean.res.health;

import cn.mulanbay.pms.persistent.dto.health.TreatSummaryStat;
import cn.mulanbay.pms.web.bean.res.chart.BaseChartData;

import java.math.BigDecimal;
import java.util.Date;

public class TreatSummaryStatVo extends TreatSummaryStat {

    private BaseChartData chartData;

    public BaseChartData getChartData() {
        return chartData;
    }

    public void setChartData(BaseChartData chartData) {
        this.chartData = chartData;
    }
}
