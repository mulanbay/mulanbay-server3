package cn.mulanbay.pms.util;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.dto.common.CalendarDateStat;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.BaseYoyStatSH;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import cn.mulanbay.pms.web.bean.res.chart.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 图表统计通用操作类
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
public class ChartUtil {

    /**
     * 补全日期 ,为统计数据补全日期
     *
     * @param data
     * @param sf
     * @return
     */
    public static ChartData completeDate(ChartData data, DateStatSH sf) {
        if (data.getIntXData().isEmpty()) {
            return data;
        }
        if (sf.isCompleteDate() == null || sf.isCompleteDate() == false) {
            return data;
        } else if (data.getYdata() == null || data.getYdata().isEmpty()) {
            return data;
        } else if (sf.getDateGroupType() == DateGroupType.DAY) {
            //日期类型不计算,有点复杂
            return data;
        } else {
            int[] se = getStartAndEnd(sf.getStartDate(), sf.getEndDate(), sf.getDateGroupType());
            int start = se[0];
            int end = se[1];
            int nextValue = data.getIntXData().get(0);
            int nn = 0;
            for (int i = start; i <= end; i++) {
                if (i < nextValue || nn == data.getIntXData().size()) {
                    // 没有，则增加
                    data.getIntXData().add(nn, i);
                    data.getXdata().add(nn, getStringXData(sf.getDateGroupType(), i));
                    for (ChartYData ydata : data.getYdata()) {
                        ydata.getData().add(nn, 0);
                    }
                } else if (i == nextValue) {
                }
                nn++;
                if (nn < data.getIntXData().size()) {
                    nextValue = data.getIntXData().get(nn);
                }
            }
            return data;
        }
    }

    public static int[] getStartAndEnd(Date startDate, Date endDate, DateGroupType dateGroupType) {
        if (dateGroupType == DateGroupType.WEEK) {
            int start = 1;
            int end = Constant.MAX_WEEK;
            if (startDate != null) {
                start = DateUtil.getWeek(startDate);
            }
            if (endDate != null) {
                end = DateUtil.getWeek(endDate);
                if (end == 1) {
                    //有时最后一天会变为1
                    end = Constant.MAX_WEEK;
                }
            }
            return new int[]{start, end};
        } else if (dateGroupType == DateGroupType.MONTH) {
            int start = 1;
            int end = Constant.MAX_MONTH;
            if (startDate != null) {
                start = DateUtil.getMonth(startDate) + 1;
            }
            if (endDate != null) {
                end = DateUtil.getMonth(endDate) + 1;
            }
            return new int[]{start, end};
        } else if (dateGroupType == DateGroupType.YEAR) {
            int end = DateUtil.getYear(endDate);
            if (end == 0) {
                end = DateUtil.getYear(new Date());
            }
            int start = DateUtil.getYear(startDate);
            if (start == 0) {
                start = end - 5;
            }
            return new int[]{start, end};
        } else {
            return new int[]{0, 0};
        }
    }

    private static int getNextValue(DateGroupType dateGroupType, int value) {
        if (dateGroupType == DateGroupType.DAY) {
            Date date = DateUtil.getDate(String.valueOf(value), "yyyyMMdd");
            Date newDate = DateUtil.getDate(1);
            return Integer.valueOf(DateUtil.getFormatDate(newDate, "yyyyMMdd"));
        } else {
            return value++;
        }
    }

    /**
     * 获取X轴索引
     * @param dateGroupType
     * @param indexValue
     * @param min
     * @param xdata
     * @return
     */
    public static int getXIndex(DateGroupType dateGroupType,int indexValue,int min,List<String> xdata){
        if(dateGroupType==DateGroupType.DAY){
            //循环
            for(int i =0; i<xdata.size();i++){
                if(xdata.get(i).equals(String.valueOf(indexValue))){
                    return i;
                }
            }
            return 0;
        }
        return indexValue-min;
    }
    /**
     * 获取最大最小值
     * @param dateGroupType
     * @param startDate
     * @param endDate
     * @return
     */
    public static int[] getMinMax(DateGroupType dateGroupType, Date startDate,Date endDate){
        int min =1;
        int max =1;
        if(dateGroupType==DateGroupType.MONTH){
            min = 1;
            max = Constant.MAX_MONTH;
        }else if(dateGroupType==DateGroupType.DAY){
            min = 1;
            max = Constant.MAX_DAY;
        }else if(dateGroupType==DateGroupType.HOUR){
            min = 1;
            max = Constant.MAX_HOUR;
        }else if(dateGroupType==DateGroupType.DAYOFMONTH){
            min = 1;
            max = Constant.MAX_MONTH_DAY;
        }else if(dateGroupType==DateGroupType.WEEK){
            min = 1;
            max = Constant.MAX_WEEK;
        }else if(dateGroupType==DateGroupType.DAYOFWEEK){
            min = 1;
            max = Constant.WEEK_DAY;
        }else if(dateGroupType==DateGroupType.YEAR){
            min = Integer.valueOf(DateUtil.getFormatDate(startDate,"yyyy"));
            max = Integer.valueOf(DateUtil.getFormatDate(endDate,"yyyy"));
        }
        return new int[]{min,max};
    }

    /**
     * 获取X轴的字符值列表
     * @param dateGroupType
     * @param min
     * @param max
     * @return
     */
    public static List<String> getStringXdataList(DateGroupType dateGroupType, int min,int max){
        List<String> res = new ArrayList<>();
        for(int i=min;i<=max;i++){
            String s = getStringXData(dateGroupType,i);
            res.add(s);
        }
        return res;
    }

    /**
     * 获取X轴的字符值
     *
     * @param dateGroupType
     * @param value
     * @return
     */
    public static String getStringXData(DateGroupType dateGroupType, int value) {
        if (dateGroupType == DateGroupType.MONTH) {
            return value + "月份";
        } else if (dateGroupType == DateGroupType.MINUTE) {
            return value + "分钟";
        } else if (dateGroupType == DateGroupType.HOUR) {
            //return value+":00~" +(value)+":59";
            return value + "点";
        } else if (dateGroupType == DateGroupType.WEEK) {
            return "第" + value + "周";
        } else if (dateGroupType == DateGroupType.YEAR) {
            return value + "年";
        } else if (dateGroupType == DateGroupType.DAYOFMONTH) {
            return value + "号";
        } else if (dateGroupType == DateGroupType.DAYOFWEEK) {
            return getWeekName(value);
        } else {
            return String.valueOf(value);
        }
    }

    private static String getWeekName(int index) {
        if (index == 1) {
            return "周一";
        } else if (index == 2) {
            return "周二";
        } else if (index == 3) {
            return "周三";
        } else if (index == 4) {
            return "周四";
        } else if (index == 5) {
            return "周五";
        } else if (index == 6) {
            return "周六";
        } else if (index == 7) {
            return "周日";
        } else {
            return "周" + index;
        }
    }

    /**
     * @param title
     * @param legendOne
     * @param sf
     * @param list
     * @return
     */
    public static ChartCalendarData createChartCalendarData(String title, String legendOne, String unit, DateStatSH sf,List<? extends CalendarDateStat> list) {
        ChartCalendarData chartData = new ChartCalendarData();
        int year = DateUtil.getYear(sf.getStartDate());
        int endYear = DateUtil.getYear(sf.getEndDate());
        if (year != endYear) {
            throw new ApplicationException(PmsCode.START_YEAR_NOT_EQUALS_END_YEAR);
        }
        chartData.setTitle(year + "年" + title);
        chartData.setYear(year);
        chartData.setLegendData(legendOne, 3);
        chartData.setUnit(unit);
        if (list != null && !list.isEmpty()) {
            chartData.setSubTitle("总次数：" + list.size());
            for (CalendarDateStat stat : list) {
                String dateString = DateUtil.getFormatDate(String.valueOf(stat.getDayIndexValue()), "yyyyMMdd", DateUtil.FormatDay1);
                double vv = stat.getCalendarStatValue();
                chartData.addSerieData(new Object[]{dateString, vv});
            }
        }
        return chartData;
    }

    /**
     * 生成基于时分统计的散点图形数据
     * @param dateList
     * @param title
     * @param name
     * @return
     */
    public static ScatterChartData createHMChartData(List<Date> dateList , String title, String name){
        ScatterChartData chartData = new ScatterChartData();
        chartData.setTitle(title);
        chartData.setxUnit("");
        chartData.setyUnit("点");
        chartData.addLegent(name);
        ScatterChartDetailData detailData = new ScatterChartDetailData();
        detailData.setName(name);
        double totalX = 0;
        int n = 0;
        for (Date date : dateList) {
            String x = DateUtil.getFormatDate(date,"yyyyMMdd");
            String y = DateUtil.getFormatDate(date,"HH.mm");
            detailData.addData(new Object[]{x, y});
            totalX += Double.valueOf(y);
            n++;
        }
        detailData.setxAxisAverage(totalX / n);
        chartData.addSeriesData(detailData);
        return chartData;
    }

    /**
     * 初始化同期对比数据
     *
     * @param sf
     * @param title
     * @param subTitle
     * @return
     */
    public static ChartData initYoyCharData(BaseYoyStatSH sf, String title, String subTitle) {
        ChartData chartData = new ChartData();
        chartData.setTitle(title);
        chartData.setSubTitle(subTitle);
        if (sf.getDateGroupType() == DateGroupType.MONTH) {
            for (int i = 1; i <= Constant.MAX_MONTH; i++) {
                chartData.getIntXData().add(i);
                chartData.getXdata().add(i + "月份");
            }
        } else if (sf.getDateGroupType() == DateGroupType.WEEK) {
            for (int i = 1; i <= Constant.MAX_WEEK; i++) {
                chartData.getIntXData().add(i);
                chartData.getXdata().add("第" + i + "周");
            }
        }
        return chartData;
    }

    /**
     * 获取日期的标题，只要用于报表的子标题
     *
     * @param sf
     * @return
     */
    public static String getDateTitle(DateStatSH sf) {
        if (sf.getStartDate() == null && sf.getEndDate() == null) {
            return "";
        } else if (sf.getStartDate() != null && sf.getEndDate() == null) {
            return "从" + DateUtil.getFormatDate(sf.getStartDate(), DateUtil.FormatDay1) + "开始";
        } else if (sf.getStartDate() == null && sf.getEndDate() != null) {
            return "截止" + DateUtil.getFormatDate(sf.getEndDate(), DateUtil.FormatDay1);
        } else {
            return DateUtil.getFormatDate(sf.getStartDate(), DateUtil.FormatDay1) + "~" +
                    DateUtil.getFormatDate(sf.getEndDate(), DateUtil.FormatDay1);
        }
    }

    /**
     * 获取日期的标题，只要用于报表的子标题
     *
     * @param sf
     * @return
     */
    public static String getDateTitle(DateStatSH sf,String cs) {
        String title = getDateTitle(sf);
        if(StringUtil.isNotEmpty(cs)){
            title+=",总计:"+cs;
        }
        return title;
    }

}
