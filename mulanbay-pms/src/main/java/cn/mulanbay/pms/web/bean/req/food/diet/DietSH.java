package cn.mulanbay.pms.web.bean.req.food.diet;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.CrossType;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DietSource;
import cn.mulanbay.pms.persistent.enums.DietType;
import cn.mulanbay.pms.persistent.enums.FoodType;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DietSH extends PageSearch implements BindUser, FullEndDateTime {

    @Query(fieldName = "dietId", op = Parameter.Operator.EQ)
    private Long dietId;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occurTime", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occurTime", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "foods,shop,tags", op = Parameter.Operator.LIKE, crossType = CrossType.OR)
    private String name;

    @Query(fieldName = "dietType", op = Parameter.Operator.EQ)
    private DietType dietType;

    @Query(fieldName = "dietSource", op = Parameter.Operator.EQ)
    private DietSource dietSource;

    @Query(fieldName = "foodType", op = Parameter.Operator.EQ)
    private FoodType foodType;

    @Query(fieldName = "score", op = Parameter.Operator.GTE)
    private Integer minScore;

    @Query(fieldName = "score", op = Parameter.Operator.LTE)
    private Integer maxScore;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    public Long userId;

    private String orderByField;

    public Long getDietId() {
        return dietId;
    }

    public void setDietId(Long dietId) {
        this.dietId = dietId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DietType getDietType() {
        return dietType;
    }

    public void setDietType(DietType dietType) {
        this.dietType = dietType;
    }

    public DietSource getDietSource() {
        return dietSource;
    }

    public void setDietSource(DietSource dietSource) {
        this.dietSource = dietSource;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
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

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(String orderByField) {
        this.orderByField = orderByField;
    }
}
