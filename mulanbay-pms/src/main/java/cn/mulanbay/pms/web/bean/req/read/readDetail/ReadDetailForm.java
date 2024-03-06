package cn.mulanbay.pms.web.bean.req.read.readDetail;

import cn.mulanbay.common.aop.BindUser;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class ReadDetailForm implements BindUser {

    private Long detailId;
    private Long userId;

    @NotNull(message = "书籍不能为空")
    private Long bookId;
    //阅读日期
    @NotNull(message = "阅读时间不能为空")
    private Date readTime;

    @NotNull(message = "时长不能为空")
    private Integer duration;

    private String remark;

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
