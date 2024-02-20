package cn.mulanbay.pms.web.bean.res.read.book;

import cn.mulanbay.pms.persistent.dto.read.BookStat;
import cn.mulanbay.pms.web.bean.res.chart.ChartPieData;

/**
 * @author fenghong
 * @date 2024/2/20
 */
public class BookStatVo extends BookStat {

    private ChartPieData chartData;

    public ChartPieData getChartData() {
        return chartData;
    }

    public void setChartData(ChartPieData chartData) {
        this.chartData = chartData;
    }
}
