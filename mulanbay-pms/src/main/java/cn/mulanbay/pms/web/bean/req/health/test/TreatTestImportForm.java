package cn.mulanbay.pms.web.bean.req.health.test;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class TreatTestImportForm implements BindUser {

    @NotNull(message = "手术编号不能为空")
    private Long operationId;

    private Long userId;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @NotNull(message = "采集时间不能为空")
    private Date testTime;

    @NotEmpty(message = "数据不能为空")
    private String testData;

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public  Date getTestTime() {
        return testTime;
    }

    public void setTestTime( Date testTime) {
        this.testTime = testTime;
    }

    public String getTestData() {
        return testData;
    }

    public void setTestData( String testData) {
        this.testData = testData;
    }
}
