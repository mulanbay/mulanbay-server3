package cn.mulanbay.pms.web.bean.req.ai;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotNull;

/**
 * 通用对象的获取详情请求类
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class AIModelPublishForm implements BindUser {

    @NotNull(message = "模型编号不能为空")
    private Long modelId;

    private Long userId;


    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
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
