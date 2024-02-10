package cn.mulanbay.pms.web.bean.req.health.drugDetail;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.DateGroupType;

public class TreatDrugDetailCalendarStatSH implements BindUser {

    private Long drugId;

    private Integer year;

    private String month;

    private DateGroupType dateGroupType;

    private Long userId;

    /**
     * 是否合并相同的药名
     */
    private boolean mergeSameName;

    public Long getDrugId() {
        return drugId;
    }

    public void setDrugId(Long drugId) {
        this.drugId = drugId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public DateGroupType getDateGroupType() {
        return dateGroupType;
    }

    public void setDateGroupType(DateGroupType dateGroupType) {
        this.dateGroupType = dateGroupType;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isMergeSameName() {
        return mergeSameName;
    }

    public void setMergeSameName(boolean mergeSameName) {
        this.mergeSameName = mergeSameName;
    }
}
