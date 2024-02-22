package cn.mulanbay.pms.web.bean.req.food.diet;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DietPriceAnalyseSH extends QueryBuilder implements BindUser, DateStatSH {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occur_time", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occur_time", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "foods", op = Parameter.Operator.LIKE)
    private String name;

    @Query(fieldName = "diet_type", op = Parameter.Operator.EQ)
    private Integer dietType;

    @Query(fieldName = "diet_source", op = Parameter.Operator.EQ)
    private Integer dietSource;

    @Query(fieldName = "price", op = Parameter.Operator.GT)
    private Double minPrice;

    /**
     * 由于后端采用的是sql查询，枚举类型的参数绑定有些问题，需要转换为int
     */
    @Query(fieldName = "food_type", op = Parameter.Operator.EQ)
    private Integer foodType;

    @Query(fieldName = "location", op = Parameter.Operator.EQ)
    private String location;

    @Query(fieldName = "user_id", op = Parameter.Operator.EQ)
    public Long userId;

    private DietStatField statField;

    /**
     * 是否要预测
     */
    private Boolean predict = false;

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

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
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

    public DietStatField getStatField() {
        return statField;
    }

    public void setStatField(DietStatField statField) {
        this.statField = statField;
    }

    public Boolean getPredict() {
        return predict;
    }

    public void setPredict(Boolean predict) {
        this.predict = predict;
    }
}
