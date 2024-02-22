package cn.mulanbay.pms.web.bean.req.food.diet;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.DietSource;
import cn.mulanbay.pms.persistent.enums.DietType;
import cn.mulanbay.pms.persistent.enums.FoodType;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public class DietForm implements BindUser {

    private Long dietId;
    private Long userId;

    @NotNull(message = "饮食类型不能为空")
    private DietType dietType;

    @NotNull(message = "饮食来源不能为空")
    private DietSource dietSource;

    @NotNull(message = "食物不能为空")
    private String foods;

    @NotNull(message = "食物类型不能为空")
    private FoodType foodType;

    @NotNull(message = "发生时间不能为空")
    private Date occurTime;

    private String shop;

    private String location;

    private String tags;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private Integer score;

    private String remark;

    public Long getDietId() {
        return dietId;
    }

    public void setDietId(Long dietId) {
        this.dietId = dietId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getFoods() {
        return foods;
    }

    public void setFoods(String foods) {
        this.foods = foods;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
