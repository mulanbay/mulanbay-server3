package cn.mulanbay.pms.web.bean.req.food.diet;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.persistent.enums.DietSource;
import cn.mulanbay.pms.persistent.enums.DietType;
import cn.mulanbay.pms.persistent.enums.FoodType;

/**
 * @author fenghong
 * @date 2024/2/22
 */
public class LastDietSH extends QueryBuilder implements BindUser {

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    @Query(fieldName = "dietSource", op = Parameter.Operator.EQ)
    private DietSource dietSource;

    @Query(fieldName = "dietType", op = Parameter.Operator.EQ)
    private DietType dietType;

    @Query(fieldName = "foodType", op = Parameter.Operator.EQ)
    private FoodType foodType;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public DietSource getDietSource() {
        return dietSource;
    }

    public void setDietSource(DietSource dietSource) {
        this.dietSource = dietSource;
    }

    public DietType getDietType() {
        return dietType;
    }

    public void setDietType(DietType dietType) {
        this.dietType = dietType;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
    }
}
