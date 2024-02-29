package cn.mulanbay.pms.persistent.dto.report;

public class PlanValueDTO {
    private Long planCountValue;

    private Long planValue;

    private String compareBussKey;

    public PlanValueDTO() {
    }

    public PlanValueDTO(Long planCountValue, Long planValue) {
        this.planCountValue = planCountValue;
        this.planValue = planValue;
    }

    public PlanValueDTO(Long planCountValue, Long planValue, String compareBussKey) {
        this.planCountValue = planCountValue;
        this.planValue = planValue;
        this.compareBussKey = compareBussKey;
    }

    public Long getPlanCountValue() {
        return planCountValue;
    }

    public void setPlanCountValue(Long planCountValue) {
        this.planCountValue = planCountValue;
    }

    public Long getPlanValue() {
        return planValue;
    }

    public void setPlanValue(Long planValue) {
        this.planValue = planValue;
    }

    public String getCompareBussKey() {
        return compareBussKey;
    }

    public void setCompareBussKey(String compareBussKey) {
        this.compareBussKey = compareBussKey;
    }
}
