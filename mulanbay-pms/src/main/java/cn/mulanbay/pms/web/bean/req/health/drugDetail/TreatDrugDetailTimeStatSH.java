package cn.mulanbay.pms.web.bean.req.health.drugDetail;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class TreatDrugDetailTimeStatSH implements BindUser {

    private Long drugId;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    private Date endDate;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
