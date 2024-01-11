package cn.mulanbay.pms.web.bean.req.log.operLog;

import cn.mulanbay.pms.persistent.enums.LogCompareType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class OperLogGetEditReq {

    @NotEmpty(message = "ID不能为空")
    private String id;

    @NotNull(message = "比较类型不能为空")
    private LogCompareType compareType;

    @NotEmpty(message = "类名不能为空")
    private String beanName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LogCompareType getCompareType() {
        return compareType;
    }

    public void setCompareType(LogCompareType compareType) {
        this.compareType = compareType;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
