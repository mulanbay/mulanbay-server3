package cn.mulanbay.pms.web.bean.req.food.category;

import cn.mulanbay.persistent.query.CrossType;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.web.bean.request.PageSearch;

public class FoodCategorySH extends PageSearch  {

    @Query(fieldName = "className,tags,type,remark", op = Parameter.Operator.LIKE, crossType = CrossType.OR)
    private String name;

    @Query(fieldName = "status", op = Parameter.Operator.EQ)
    private CommonStatus status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }
}
