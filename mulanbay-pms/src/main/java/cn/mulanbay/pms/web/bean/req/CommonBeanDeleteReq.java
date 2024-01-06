package cn.mulanbay.pms.web.bean.req;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotEmpty;

/**
 * 通用对象的删除请求类
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class CommonBeanDeleteReq implements BindUser {

    @NotEmpty(message = "主键编号不能为空")
    private String ids;

    private Long userId;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
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
