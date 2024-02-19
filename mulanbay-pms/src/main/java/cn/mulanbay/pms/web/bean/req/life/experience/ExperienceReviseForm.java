package cn.mulanbay.pms.web.bean.req.life.experience;

public class ExperienceReviseForm {

    private Long expId;

    private Boolean reviseCost;

    private Boolean reviseDays;

    public Long getExpId() {
        return expId;
    }

    public void setExpId(Long expId) {
        this.expId = expId;
    }

    public Boolean getReviseCost() {
        return reviseCost;
    }

    public void setReviseCost(Boolean reviseCost) {
        this.reviseCost = reviseCost;
    }

    public Boolean getReviseDays() {
        return reviseDays;
    }

    public void setReviseDays(Boolean reviseDays) {
        this.reviseDays = reviseDays;
    }
}
