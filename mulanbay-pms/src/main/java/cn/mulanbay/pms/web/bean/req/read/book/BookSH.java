package cn.mulanbay.pms.web.bean.req.read.book;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BookStatus;
import cn.mulanbay.web.bean.request.PageSearch;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BookSH extends PageSearch implements BindUser, FullEndDateTime {

    @Query(fieldName = "bookName", op = Parameter.Operator.LIKE)
    private String name;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "finishDate", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "finishDate", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    public Long userId;

    @Query(fieldName = "status", op = Parameter.Operator.EQ)
    private BookStatus status;

    @Query(fieldName = "cate.cateId", op = Parameter.Operator.EQ)
    private Long cateId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
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

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public Long getCateId() {
        return cateId;
    }

    public void setCateId(Long cateId) {
        this.cateId = cateId;
    }
}
