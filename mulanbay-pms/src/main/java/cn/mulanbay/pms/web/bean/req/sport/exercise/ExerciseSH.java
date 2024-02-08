package cn.mulanbay.pms.web.bean.req.sport.exercise;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.CrossType;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BestType;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExerciseSH extends PageSearch implements BindUser, FullEndDateTime {

    @Query(fieldName = "sport.sportId", op = Parameter.Operator.EQ)
    private Long sportId;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "exerciseTime", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "exerciseTime", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    public Long userId;

    @Query(fieldName = "mileageBest,fastBest", op = Parameter.Operator.IN, crossType = CrossType.OR)
    private List<BestType> bests;

    private Boolean containBest;

    private GroupType sortField;

    private String sortType;

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

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

    public List<BestType> getBests() {
        return bests;
    }

    public void setBests(List<BestType> bests) {
        this.bests = bests;
    }

    public Boolean getContainBest() {
        return containBest;
    }

    public void setContainBest(Boolean containBest) {
        this.containBest = containBest;
    }

    public GroupType getSortField() {
        return sortField;
    }

    public void setSortField(GroupType sortField) {
        this.sortField = sortField;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    @Override
    public PageRequest buildQuery() {
        //增加最佳的过滤
        if (containBest != null && containBest) {
            bests = new ArrayList<>();
            bests.add(BestType.CURRENT);
            bests.add(BestType.ONCE);
        }
        PageRequest pr = super.buildQuery();
        return pr;
    }
}
