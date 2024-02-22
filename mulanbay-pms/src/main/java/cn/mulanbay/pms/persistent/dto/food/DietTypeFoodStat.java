package cn.mulanbay.pms.persistent.dto.food;

/**
 * @author fenghong
 * @date 2024/2/22
 */
public class DietTypeFoodStat {

    private String breakfast;

    private String lunch;

    private String dinner;

    private String other;

    public DietTypeFoodStat(String breakfast, String lunch, String dinner, String other) {
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.other = other;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
