package cn.mulanbay.pms.persistent.dto.life;

public class ExperienceIdName {

    private Long expId;

    private String expName;

    public ExperienceIdName() {
    }

    public ExperienceIdName(Long expId, String expName) {
        this.expId = expId;
        this.expName = expName;
    }

    public Long getExpId() {
        return expId;
    }

    public void setExpId(Long expId) {
        this.expId = expId;
    }

    public String getExpName() {
        return expName;
    }

    public void setExpName(String expName) {
        this.expName = expName;
    }
}
