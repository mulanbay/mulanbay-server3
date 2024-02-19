package cn.mulanbay.pms.persistent.dto.life;

/**
 * @author fenghong
 * @date 2024/2/14
 */
public class CityLocationDTO {

    private String city;

    private String location;

    public CityLocationDTO(String city, String location) {
        this.city = city;
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
