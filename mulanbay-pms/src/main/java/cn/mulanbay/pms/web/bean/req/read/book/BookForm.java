package cn.mulanbay.pms.web.bean.req.read.book;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BookLanguage;
import cn.mulanbay.pms.persistent.enums.BookSource;
import cn.mulanbay.pms.persistent.enums.BookStatus;
import cn.mulanbay.pms.persistent.enums.BookType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class BookForm implements BindUser {

    private Long bookId;

    private Long userId;

    @NotNull(message = "图书分类不能为空")
    private Long cateId;

    @NotEmpty(message = "图书名称不能为空")
    private String bookName;

    @NotEmpty(message = "作者不能为空")
    private String author;

    @NotEmpty(message = "ISBN不能为空")
    private String isbn;

    //初步日期
    @NotNull(message = "出版年份不能为空")
    private Integer publishYear;

    //出版社
    @NotEmpty(message = "出版社不能为空")
    private String press;

    //国家
    @NotNull(message = "国家不能为空")
    private Long countryId;

    @NotNull(message = "图书类型不能为空")
    private BookType bookType;

    @NotNull(message = "语言不能为空")
    private BookLanguage language;

    // 重要等级(0-5)
    //@NotNull(message = "{validate.readingRecord.importantLevel.NotNull}")
    private Double score;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "期望完成时间不能为空")
    private Date expertFinishDate;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    private Date beginDate;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    private Date finishDate;

    //保存日期：如购入、借入
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    private Date storeDate;

    @NotNull(message = "图书状态不能为空")
    private BookStatus status;

    //@NotNull(message = "{validate.readingRecord.source.NotNull}")
    private BookSource source;

    private Boolean secondhand;

    //读完花费时间
    private Integer costDays;

    private String remark;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCateId() {
        return cateId;
    }

    public void setCateId(Long cateId) {
        this.cateId = cateId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public BookType getBookType() {
        return bookType;
    }

    public void setBookType(BookType bookType) {
        this.bookType = bookType;
    }

    public BookLanguage getLanguage() {
        return language;
    }

    public void setLanguage(BookLanguage language) {
        this.language = language;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Date getExpertFinishDate() {
        return expertFinishDate;
    }

    public void setExpertFinishDate(Date expertFinishDate) {
        this.expertFinishDate = expertFinishDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Date getStoreDate() {
        return storeDate;
    }

    public void setStoreDate(Date storeDate) {
        this.storeDate = storeDate;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public BookSource getSource() {
        return source;
    }

    public void setSource(BookSource source) {
        this.source = source;
    }

    public Boolean getSecondhand() {
        return secondhand;
    }

    public void setSecondhand(Boolean secondhand) {
        this.secondhand = secondhand;
    }

    public Integer getCostDays() {
        return costDays;
    }

    public void setCostDays(Integer costDays) {
        this.costDays = costDays;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
