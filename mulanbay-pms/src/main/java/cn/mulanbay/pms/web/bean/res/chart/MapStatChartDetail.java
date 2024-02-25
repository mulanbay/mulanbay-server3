package cn.mulanbay.pms.web.bean.res.chart;

/**
 * @Description:
 * @Author: fenghong
 * @Create : 2021/3/12
 */
public class MapStatChartDetail {

    //与地理位置的信息
    private String name;

    //默认显示值
    private int value;

    //天数
    private int days;

    //次数
    private int counts;

    //花费
    private int cost;

    //标签
    private String label;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
