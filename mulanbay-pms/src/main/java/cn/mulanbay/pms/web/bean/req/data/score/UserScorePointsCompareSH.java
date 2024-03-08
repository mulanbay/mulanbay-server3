package cn.mulanbay.pms.web.bean.req.data.score;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.web.bean.request.PageSearch;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class UserScorePointsCompareSH extends PageSearch implements BindUser {

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "开始时间不能为空")
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "解锁时间不能为空")
    private Date endDate;

    private Long userId;

    private DataType dataType;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public enum DataType {
        //相对和绝对
        OPPOSITE, ABSOLUTE
    }
}
