package cn.mulanbay.pms.web.bean.req.food.diet;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.*;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DietAnalyseSH extends PageSearch implements BindUser, DateStatSH {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "a.occur_time", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "a.occur_time", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "a.foods", op = Parameter.Operator.LIKE)
    private String name;

    @Query(fieldName = "a.diet_type", op = Parameter.Operator.EQ)
    private Integer dietType;

    @Query(fieldName = "a.diet_source", op = Parameter.Operator.EQ)
    private Integer dietSource;

    @Query(fieldName = "a.score", op = Parameter.Operator.GTE)
    private Integer minScore;

    @Query(fieldName = "a.score", op = Parameter.Operator.LTE)
    private Integer maxScore;

    /**
     * 由于后端采用的是sql查询，枚举类型的参数绑定有些问题，需要转换为int
     */
    @Query(fieldName = "a.food_type", op = Parameter.Operator.EQ)
    private Integer foodType;

    @Query(fieldName = "a.location", op = Parameter.Operator.EQ)
    private String location;

    @Query(fieldName = "a.user_id", op = Parameter.Operator.EQ)
    public Long userId;

    private ChartType chartType;

    private DietStatField field;

    private int minCount;

    //包含未知的数据
    private boolean includeUnknown;

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

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDietType() {
        return dietType;
    }

    public void setDietType(Integer dietType) {
        this.dietType = dietType;
    }

    public Integer getDietSource() {
        return dietSource;
    }

    public void setDietSource(Integer dietSource) {
        this.dietSource = dietSource;
    }

    public Integer getMinScore() {
        return minScore;
    }

    public void setMinScore(Integer minScore) {
        this.minScore = minScore;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public Integer getFoodType() {
        return foodType;
    }

    public void setFoodType(Integer foodType) {
        this.foodType = foodType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public DietStatField getField() {
        return field;
    }

    public void setField(DietStatField field) {
        this.field = field;
    }

    public int getMinCount() {
        return minCount;
    }

    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    public boolean isIncludeUnknown() {
        return includeUnknown;
    }

    public void setIncludeUnknown(boolean includeUnknown) {
        this.includeUnknown = includeUnknown;
    }

}
