package cn.mulanbay.pms.web.bean.req.auth.role;

import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.persistent.enums.UserStatus;
import cn.mulanbay.web.bean.request.PageSearch;

/**
 * Created by fenghong on 2017/2/1.
 */
public class RoleSH extends PageSearch {

    @Query(fieldName = "roleName", op = Parameter.Operator.LIKE)
    private String name;

    @Query(fieldName = "status", op = Parameter.Operator.EQ)
    private UserStatus status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

}
