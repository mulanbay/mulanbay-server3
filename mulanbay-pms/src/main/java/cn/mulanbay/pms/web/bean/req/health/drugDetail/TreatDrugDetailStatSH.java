package cn.mulanbay.pms.web.bean.req.health.drugDetail;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class TreatDrugDetailStatSH extends PageSearch implements BindUser, FullEndDateTime {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "tdd.occur_time", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "tdd.occur_time", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "tdd.user_id", op = Parameter.Operator.EQ)
    public Long userId;

    @Query(fieldName = "td.drug_name", op = Parameter.Operator.LIKE)
    private String name;

    //是否合并相同的药名
    private boolean mergeSameName;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMergeSameName() {
        return mergeSameName;
    }

    public void setMergeSameName(boolean mergeSameName) {
        this.mergeSameName = mergeSameName;
    }
}
