package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BookLanguage;
import cn.mulanbay.pms.persistent.enums.BookSource;
import cn.mulanbay.pms.persistent.enums.BookType;
import cn.mulanbay.pms.persistent.enums.BookStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 书籍
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "book")
public class Book implements java.io.Serializable {
    private static final long serialVersionUID = -6674046076514845193L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "book_id", unique = true, nullable = false)
    private Long bookId;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "cate_id")
    private BookCategory cate;

    @Column(name = "book_name")
    private String bookName;

    @Column(name = "author")
    private String author;

    @Column(name = "isbn")
    private String isbn;
    //出版日期
    @Column(name = "publish_year")
    private Integer publishYear;
    //出版社
    @Column(name = "press")
    private String press;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(name = "book_type")
    private BookType bookType;

    @Column(name = "language")
    private BookLanguage language;
    // 评分(0-5)
    @Column(name = "score")
    private Double score;

    /**
     * 期望完成时间
     */
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "expert_finish_date")
    private Date expertFinishDate;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "begin_date")
    private Date beginDate;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "finish_date")
    private Date finishDate;
    //保存日期：如购入、借入
    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @Column(name = "store_date")
    private Date storeDate;

    @Column(name = "status")
    private BookStatus status;

    @Column(name = "source")
    private BookSource source;
    // 是否二手
    @Column(name = "secondhand")
    private Boolean secondhand;
    //读完花费时间
    @Column(name = "cost_days")
    private Integer costDays;
    @Column(name = "remark")
    private String remark;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time",updatable = false)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time",insertable = false)
    private Date modifyTime;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BookCategory getCate() {
        return cate;
    }

    public void setCate(BookCategory cate) {
        this.cate = cate;
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Transient
    public String getStatusName() {
        return status==null ? null: status.getName();
    }

    @Transient
    public String getLanguageName() {
        return language==null ? null: language.getName();
    }

    @Transient
    public String getBookTypeName() {
        return bookType==null ? null: bookType.getName();
    }

    @Transient
    public String getSourceName() {
        return this.source==null ? null:source.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Book bean) {
            return bean.getBookId().equals(this.getBookId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bookId);
    }
}
