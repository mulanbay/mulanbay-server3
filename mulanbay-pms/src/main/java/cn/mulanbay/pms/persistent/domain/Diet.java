package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DietSource;
import cn.mulanbay.pms.persistent.enums.DietType;
import cn.mulanbay.pms.persistent.enums.FoodType;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 饮食记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "diet")
public class Diet implements java.io.Serializable {

    private static final long serialVersionUID = -2485959570366834206L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "diet_id", unique = true, nullable = false)
    private Long dietId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "diet_type")
    private DietType dietType;

    @Column(name = "diet_source")
    private DietSource dietSource;

    @Column(name = "foods")
    private String foods;

    @Column(name = "food_type")
    private FoodType foodType;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "occur_time")
    private Date occurTime;

    @Column(name = "shop")
    private String shop;

    @Column(name = "location")
    private String location;

    @Column(name = "tags")
    private String tags;

    @Column(name = "price",precision = 9,scale = 2)
    private BigDecimal price;

    @Column(name = "score")
    private Integer score;
    @Column(name = "remark")
    private String remark;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time",updatable = false)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time",insertable = false)
    private Date modifyTime;

    public Long getDietId() {
        return dietId;
    }

    public void setDietId(Long dietId) {
        this.dietId = dietId;
    }

    public Long getUserId() {
        return userId;
    }

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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Transient
    public String getDietTypeName() {
        return dietType==null? null: dietType.getName();
    }

    @Transient
    public String getDietSourceName() {
        return dietSource==null? null: dietSource.getName();
    }

    @Transient
    public String getFoodTypeName() {
        return foodType==null? null: foodType.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Diet bean) {
            return bean.getDietId().equals(this.getDietId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dietId);
    }
}
