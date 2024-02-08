package cn.mulanbay.pms.web.bean.req.sport.milestone;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.web.bean.request.PageSearch;

public class SportMilestoneSH extends PageSearch implements BindUser {

    @Query(fieldName = "exercise.exerciseId", op = Parameter.Operator.EQ)
    private Long exerciseId;

    @Query(fieldName = "sport.sportId", op = Parameter.Operator.EQ)
    private Long sportId;

    @Query(fieldName = "milestoneName", op = Parameter.Operator.LIKE)
    private String name;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    public Long userId;

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
