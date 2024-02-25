package cn.mulanbay.pms.web.bean.res.life;

/**
 * @author fenghong
 * @date 2024/2/25
 */
public class ExperienceLocationVo {

    //开始地点
    private String start = null;
    //抵达地点
    private String arrive = null;
    //开始地点的经纬度
    private String startLocation = null;
    //抵达地点的经纬度
    private String arriveLocation = null;

    public ExperienceLocationVo() {
    }

    public ExperienceLocationVo(String start, String arrive, String startLocation, String arriveLocation) {
        this.start = start;
        this.arrive = arrive;
        this.startLocation = startLocation;
        this.arriveLocation = arriveLocation;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getArrive() {
        return arrive;
    }

    public void setArrive(String arrive) {
        this.arrive = arrive;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getArriveLocation() {
        return arriveLocation;
    }

    public void setArriveLocation(String arriveLocation) {
        this.arriveLocation = arriveLocation;
    }
}
