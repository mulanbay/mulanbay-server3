package cn.mulanbay.pms.web.bean.req.music.musicPractice;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.List;

public class MusicPracticeCompareStatSH extends QueryBuilder implements DateStatSH, BindUser, FullEndDateTime {


    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "practice_date", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "practice_date", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "user_id", op = Parameter.Operator.EQ)
    private Long userId;

    @NotNull(message = "乐器编号不能为空")
    @Size(min = 2, message = "请至少选择两种乐器")
    private List<Long> instrumentIds;

    private DateGroupType xgroupType;

    private DateGroupType ygroupType;

    public DateGroupType getXgroupType() {
        return xgroupType;
    }

    public void setXgroupType(DateGroupType xgroupType) {
        this.xgroupType = xgroupType;
    }

    public DateGroupType getYgroupType() {
        return ygroupType;
    }

    public void setYgroupType(DateGroupType ygroupType) {
        this.ygroupType = ygroupType;
    }

    @Override
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
    public DateGroupType getDateGroupType() {
        return null;
    }

    @Override
    public Boolean isCompleteDate() {
        return null;
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

    public List<Long> getInstrumentIds() {
        return instrumentIds;
    }

    public void setInstrumentIds(List<Long> instrumentIds) {
        this.instrumentIds = instrumentIds;
    }
}
