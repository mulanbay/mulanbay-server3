package cn.mulanbay.pms.web.bean.req.health.test;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.TreatTestResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class TreatTestImportForm implements BindUser {

    @NotNull(message = "手术编号不能为空")
    private Long operationId;

    private Long userId;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @NotNull(message = "采集不能为空")
    private Date testTime;

    @NotEmpty(message = "数据不能为空")
    private String testData;

    public @NotNull(message = "手术编号不能为空") Long getOperationId() {
        return operationId;
    }

    public void setOperationId(@NotNull(message = "手术编号不能为空") Long operationId) {
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

    public @NotNull(message = "采集不能为空") Date getTestTime() {
        return testTime;
    }

    public void setTestTime(@NotNull(message = "采集不能为空") Date testTime) {
        this.testTime = testTime;
    }

    public @NotEmpty(message = "数据不能为空") String getTestData() {
        return testData;
    }

    public void setTestData(@NotEmpty(message = "数据不能为空") String testData) {
        this.testData = testData;
    }
}
